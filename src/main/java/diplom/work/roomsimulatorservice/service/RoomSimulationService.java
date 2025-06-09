package diplom.work.roomsimulatorservice.service;

import diplom.work.roomsimulatorservice.dto.PidDTO.PIDRequest;
import diplom.work.roomsimulatorservice.dto.PidDTO.PIDResponse;
import diplom.work.roomsimulatorservice.dto.storage.storage_room_client.RoomDTO;
import diplom.work.roomsimulatorservice.dto.storage.storage_sensor_client.SensorDataDTO;
import diplom.work.roomsimulatorservice.dto.storage.storage_simulation_client.SimulationDTO;
import diplom.work.roomsimulatorservice.feign.LSTMClient;
import diplom.work.roomsimulatorservice.feign.PIDClient;
import diplom.work.roomsimulatorservice.feign.storage.StorageRoomClient;
import diplom.work.roomsimulatorservice.feign.storage.StorageSensorDataClient;
import diplom.work.roomsimulatorservice.feign.storage.StorageSimulationClient;
import diplom.work.roomsimulatorservice.model.room.Room;
import diplom.work.roomsimulatorservice.model.room.RoomParams;
import diplom.work.roomsimulatorservice.model.room.RoomState;
import diplom.work.roomsimulatorservice.model.surface.SurfaceParams;
import diplom.work.roomsimulatorservice.util.RoomMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class RoomSimulationService {

    private final PIDClient pidClient;
    private final LSTMClient lstmClient;
    private final StorageRoomClient storageRoomClient;
    private final StorageSimulationClient storageSimulationClient;
    private final StorageSensorDataClient storageSensorDataClient;
    private final RoomMapper roomMapper;
    private final KafkaSensorProducer kafkaSensorProducer;
    private final Random random = new Random();
    private final RoomRegistryService roomRegistryService;

    /**
     * Старт симуляции в отдельном потоке. Возвращает её Id.
     */
    public Long startSimulation(Long roomId, String roomName, Double initialAirTemp, String controllerType, Long iterations, Double timestep) {
        if (!roomRegistryService.exists(roomName)) {
            registerRoom(roomId, roomName);
        }

        Long simulationId = getSimulationId(roomId, controllerType);

        // Запускаем асинхронно, чтобы вернуть управление HTTP-потоку
        runSimulationAsync(simulationId, roomName, controllerType, iterations, timestep);

        return simulationId;
    }

    private void registerRoom(Long roomId, String roomName) {
        RoomDTO roomDTO = storageRoomClient.getRoomById(roomId).getBody();
        Room room = roomMapper.toEntity(roomDTO);
        RoomParams roomParams = room.getRoomParams();
        roomRegistryService.register(room.getId(), roomName, roomParams, new RoomState());
    }

    private Long getSimulationId(Long roomId, String controllerType) {
        SimulationDTO simulationDTO = new SimulationDTO(null, controllerType, "CREATED");
        return Objects.requireNonNull(storageSimulationClient.saveSimulation(roomId, simulationDTO).getBody()).id();
    }

    @Async
    void runSimulationAsync(Long simulationId, String roomName, String controllerType, Long iterations, Double timestep) {
        LocalDateTime timestamp = LocalDateTime.of(2025, 1, 1, 0, 0, 0);
        //double setpoint = getTargetTemperature(timestamp);   // например, постоянная цель
        double tempOut = computeOutsideTemperature(timestamp);
        double dt = timestep;          // шаг времени, сек
        Room room = roomRegistryService.getByName(roomName).get();
        RoomParams roomParams = room.getRoomParams();
        RoomState roomState = room.getRoomState();

        for (int step = 0; step < iterations; step++) {
            simulateStep(timestamp, dt, roomParams, roomState);
            sendDataToStorage(simulationId, roomState, tempOut, timestamp);
            timestamp = timestamp.plusSeconds((long) dt);

//            // 2) при PID+LSTM: предскажем temp через N минут
//            Double predicted = null;
//            if ("PID+LSTM".equals(controllerType)) {
////                predicted = lstmClient.predict(req.roomName(), modelId, tempIn, tempOut);
//                // можно поправить setpoint на основе predicted
//            }

//            // 3) вычислить мощность через PID или RL
//            double power;
//            switch (req.controllerType()) {
//                case "RL":
//                    power = rlClient.control(req.roomName(), modelId, tempIn, setpoint, dt);
//                    break;
//                default:
//                    power = pidClient.control(req.roomName(), tempIn, setpoint, dt);
//            }

//            // 4) обновить температуру комнаты (простая модель)
//            tempIn += (power / 1000 - 0.01 * (tempIn - tempOut)) * dt;

//            // 5) сформировать DTO и отправить в Kafka
//            SensorDataDTO dto = new SensorDataDTO(
//                    simId, step, Instant.now(),
//                    tempIn, tempOut, setpoint, power,
//                    predicted, req.roomName()
//            );
//            producer.sendSensorData(dto);
        }
    }


    public void simulateStep(LocalDateTime timestamp, double dt, RoomParams roomParams, RoomState roomState/*, boolean lstm*/) {
        double tempOut = computeOutsideTemperature(timestamp);

        // Влияние людей (добавим 100 Вт, если есть)
        double peopleHeat = computePeopleHeat(roomState);

        Map<String, Double> newSurfaceTemps = computeNewSurfacesTemps(roomParams, roomState, tempOut, dt);
        double totalHeatFlow = computeSurfaceHeatFlow(roomParams, roomState, tempOut, dt);

        // Влияние нагревателя (Вт)
        boolean lstm = false;
        double heaterPowerWatts = computeHeaterHeat(roomState, lstm, tempOut, dt, timestamp);

        // Общий поток
        double totalQ = totalHeatFlow + heaterPowerWatts + peopleHeat;

        // Изменение температуры воздуха
        double dT_air = totalQ / roomParams.getAirHeatCapacity();
        double newAirTemp = roomState.getAirTemperature() + dT_air * dt;

        roomState.update(
                newAirTemp,
                heaterPowerWatts,
                newSurfaceTemps,
                false,
                false,
                0
        );
    }

    private Map<String, Double> computeNewSurfacesTemps(RoomParams roomParams, RoomState roomState, double outsideTemperature, double dt) {
        Map<String, Double> newSurfaceTemps = new HashMap<>();
        for (SurfaceParams surface : roomParams.getSurfaces()) {
            SurfaceParams effectiveSurface = surface;

            // Модификация параметров если окно или дверь открыты
            if ("Window".equalsIgnoreCase(surface.getName()) && roomState.isWindowOpen()) {
                effectiveSurface = createOpenSurface(surface);
            }
            if ("Door".equalsIgnoreCase(surface.getName()) && roomState.isDoorOpen()) {
                effectiveSurface = createOpenSurface(surface);
            }

            String surfaceName = surface.getName();
            double surfaceTemp = roomState.getSurfaceTemperature(surfaceName);
            double roomTemp = roomState.getAirTemperature();

            // 1. Определяем температуру "снаружи" (улица или другая комната)
            double adjacentTemp;
            if (surface.getAdjacentRoomName() == null) {
                // наружная поверхность — улица
                adjacentTemp = outsideTemperature;
            } else {
                // внутренняя поверхность — другая комната
                RoomState adjacentRoom = roomState;
                adjacentTemp = adjacentRoom != null ? adjacentRoom.getAirTemperature() : outsideTemperature;
            }

            // 2. Вычисляем изменение температуры поверхности
            double dT_surface = computeSurfaceTemperatureChange(effectiveSurface, roomTemp, surfaceTemp, adjacentTemp);
            surfaceTemp += dT_surface * dt;

            newSurfaceTemps.put(surfaceName, surfaceTemp);
            // 3. Вычисляем поток тепла в комнату от этой поверхности
//            double qToRoom = effectiveSurface.getUInternal() * effectiveSurface.getArea() * (surfaceTemp - roomTemp);
//            totalHeatFlow += qToRoom;
        }
        return newSurfaceTemps;
    }

    private double computeSurfaceHeatFlow(RoomParams roomParams, RoomState roomState, double outsideTemperature, double dt) {
        double totalHeatFlow = 0;
        double roomTemp = roomState.getAirTemperature();
        for (SurfaceParams surface : roomParams.getSurfaces()) {
            String surfaceName = surface.getName();
            double surfaceTemp = roomState.getSurfaceTemperature(surfaceName);

            double qToRoom = surface.getUInternal() * surface.getArea() * (surfaceTemp - roomTemp);
            totalHeatFlow += qToRoom;
        }
        return totalHeatFlow;
    }

    private SurfaceParams createOpenSurface(SurfaceParams original) {
        return SurfaceParams.builder()
                .name(original.getName())
                .area(original.getArea())
                .thickness(0.001) // почти ноль
                .density(1.293) // воздух
                .thermalConductivity(0.025) // воздух
                .uInternal(1000.0)
                .uExternal(1000.0)
                .specificHeat(1005.4)
                .adjacentRoomName(original.getAdjacentRoomName())
                .build();
    }

    private double computeSurfaceTemperatureChange(SurfaceParams surface, double roomTemp, double surfaceTemp, double adjacentTemp) {
        double q_in = surface.getUInternal() * surface.getArea() * (roomTemp - surfaceTemp);
        double q_out = surface.getUExternal() * surface.getArea() * (surfaceTemp - adjacentTemp);
        return (q_in - q_out) / surface.getHeatCapacity(); // °C/сек
    }

    private double computeOutsideTemperature(LocalDateTime timestamp) {
        // Получаем угол для синусоиды: полный цикл за 24 часа
        int secondsInDay = timestamp.toLocalTime().toSecondOfDay();
        double angle = 2 * Math.PI * secondsInDay / (24 * 60 * 60);

        // Среднее 10°C, амплитуда 5°C → диапазон [5, 15]
        return 10.0 + 5.0 * Math.sin(angle);
    }

    private double getTargetTemperature(LocalDateTime timestamp) {
        int hour = timestamp.getHour();

        if (hour < 4) {
            return 20.0;
        } else if (hour < 12) {
            return 19.0;
        } else {
            return 21.0;
        }
    }

    private double computePeopleHeat(RoomState roomState) {
        return roomState.getPeopleCount() * 100.0;
    }

    private double i = 0;
    private double predictedTemperature = -1;
    private double targetTemperatureDesiredCorrected = 0; // Бажана користувачем уставка

    private double computeHeaterHeat(RoomState roomState, boolean lstm, double outsideTemperature, double dt, LocalDateTime timestamp) {

        if (predictedTemperature == -1) {
            targetTemperatureDesiredCorrected = getTargetTemperature(timestamp); // Бажана користувачем уставка
        }

        double pidSetpoint; // Уставка, яка буде подана в PID

        if (lstm & i == 120) {
            predictedTemperature = predictTemperature(roomState);

            double predictedError = predictedTemperature - getTargetTemperature(timestamp);
            double correction = predictedError * 0.9;

            // Обмежити корекцію, щоб уставка не виходила за розумні межі від бажаної
            //correction = Math.max(-2.0, Math.min(2.0, correction)); // Приклад обмеження в +-2°C

            pidSetpoint = getTargetTemperature(timestamp) + correction;
            targetTemperatureDesiredCorrected = pidSetpoint;

            // Обмежити саму уставку PID в розумних межах
            //pidSetpoint = Math.max(10.0, Math.min(25.0, pidSetpoint)); // Приклад обмеження від 10 до 25°C

            System.out.println("LSTM Predict: " + predictedTemperature + ", Desired: " + getTargetTemperature(timestamp) + ", PID Setpoint: " + pidSetpoint); // Для налагодження
            i = 0;
        } else {
            // Класичний режим: PID прагне до бажаної уставки
            pidSetpoint = targetTemperatureDesiredCorrected;
            i++;
        }

        // Запит до PID-контролера
        PIDRequest pidRequest = new PIDRequest(
                roomState.getRoomName(),
                pidSetpoint, // !!! СКОРИГОВАНА АБО БАЖАНА УСТАВКА подається в PID як ЦІЛЬ
                roomState.getAirTemperature(), // !!! ПОТОЧНА ВИМІРЯНА ТЕМПЕРАТУРА подається в PID як ВИМІРЯНЕ ЗНАЧЕННЯ
                dt
        );

        PIDResponse pidResponse = pidClient.compute(pidRequest);
        return pidResponse.outputPower(); // PID видає потрібну потужність
    }

    private double predictTemperature(RoomState roomState) {
        try {
            return lstmClient.predictTemperature();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return roomState.getAirTemperature();
        }
    }

    private void sendDataToStorage(Long simulationId, RoomState roomState, double outsideTemperature, LocalDateTime timestamp) {
        SensorDataDTO sensorDataDTO = new SensorDataDTO(
                simulationId,
                timestamp,
                roomState.getAirTemperature(),
                outsideTemperature,
                getTargetTemperature(timestamp),
                roomState.getHeaterPower(),
                null,
                roomState.isWindowOpen(),
                roomState.isDoorOpen(),
                roomState.getPeopleCount()
        );
//        storageClient.addStep(simulationId, sensorDataDTO);
        kafkaSensorProducer.sendSensorData(sensorDataDTO);
    }

}