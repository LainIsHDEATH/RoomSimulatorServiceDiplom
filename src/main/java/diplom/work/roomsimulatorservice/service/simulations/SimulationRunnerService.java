package diplom.work.roomsimulatorservice.service.simulations;

import diplom.work.roomsimulatorservice.domain.SimulationClock;
import diplom.work.roomsimulatorservice.domain.SimulationContext;
import diplom.work.roomsimulatorservice.domain.room.Room;
import diplom.work.roomsimulatorservice.domain.room.RoomState;
import diplom.work.roomsimulatorservice.dto.SimulationRequestDTO;
import diplom.work.roomsimulatorservice.dto.storage.storage_ai_client.AiModelDTO;
import diplom.work.roomsimulatorservice.dto.storage.storage_pid_client.PidConfigDTO;
import diplom.work.roomsimulatorservice.dto.storage.storage_room_client.RoomDTO;
import diplom.work.roomsimulatorservice.dto.storage.storage_simulation_client.SimulationDTO;
import diplom.work.roomsimulatorservice.feign.storage.StorageAiModelClient;
import diplom.work.roomsimulatorservice.feign.storage.StoragePidConfigClient;
import diplom.work.roomsimulatorservice.feign.storage.StorageRoomClient;
import diplom.work.roomsimulatorservice.feign.storage.StorageSimulationClient;
import diplom.work.roomsimulatorservice.model.SimpleThermalModel;
import diplom.work.roomsimulatorservice.service.strategies.StrategyRegistry;
import diplom.work.roomsimulatorservice.util.ControllerType;
import diplom.work.roomsimulatorservice.util.RoomMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class SimulationRunnerService {

    private final StorageRoomClient roomClient;
    private final StoragePidConfigClient pidClient;
    private final StorageSimulationClient simulationClient;
    private final StorageAiModelClient aiModelClient;

    private final SimpleThermalModel simpleThermalModel;
    private final StrategyRegistry strategyRegistry;
    private final SimulationRegistry simulationRegistry;
    private final SimulationEngine engine;

    private final RoomMapper roomMapper;

    public Long startSimulation(SimulationRequestDTO dto) {
        Long roomId = dto.roomId();
        // 1. загружаем доменные данные
        RoomDTO roomDto = roomClient.getRoomById(roomId).getBody();
        PidConfigDTO pidDto;
        AiModelDTO aiDto;
        if (dto.pidConfigId() != 0) {
            pidDto = pidClient.getConfigById(dto.pidConfigId()).getBody();
        }
        if (dto.modelId() != 0) {
            aiDto = aiModelClient.getAiModelById(dto.modelId()).getBody();
        }
//        AiModelDTO modelDTO = aiModelClient.getAiModelById(dto.modelId()).getBody();
        Room room = roomMapper.toEntity(roomDto);
        double INITIAL_AIR_TEMP = 20.0;
        double MAX_HEATER_POWER = 2000.0;
        LocalDateTime START_TIMESTAMP = LocalDateTime.of(2025, 1, 1, 0, 0, 0);
        room.setRoomState(new RoomState(INITIAL_AIR_TEMP, room.getRoomParams().getSurfaces()));

        // 2. строим clock (поддерживает виртуальное время симулятора)
        SimulationClock clock = new SimulationClock(
                START_TIMESTAMP,
                dto.timestepSeconds() != null
                        ? Duration.of(dto.timestepSeconds(), ChronoUnit.SECONDS)
                        : Duration.of(60, ChronoUnit.SECONDS)
        );

        // 3. инициализируем simulation в БД и получаем id
        SimulationDTO simulationDTO = new SimulationDTO(
                roomId,
                dto.controllerType(),
                "CREATED",
                dto.iterations(),
                dto.timestepSeconds()
        );

        Long simId = simulationClient.saveSimulation(
                        roomId,
                        simulationDTO)
                .getBody().id();

        // 3. собираем Context Builder-ом
        SimulationContext ctx = SimulationContext.builder()
                .simulationId(simId)
                .configId(dto.pidConfigId())
                .modelId(dto.modelId())
                .room(room)
                .thermalModel(simpleThermalModel)
                .strategy(strategyRegistry.strategy(dto.controllerType()))
                .clock(clock)
                .iterations(dto.iterations())
                .maxHeaterPower(MAX_HEATER_POWER)
                .build();
        simulationRegistry.put(ctx);

        log.info("SimulationId: {}, configId: {}, modelId: {}, thermalMModel: {}, strategy: {}", simId, ctx.getConfigId(), ctx.getModelId(), ctx.getThermalModel(), ctx.getStrategy());
        // 4. передаём движку
        engine.run(ctx);

        return simId;
    }
}
