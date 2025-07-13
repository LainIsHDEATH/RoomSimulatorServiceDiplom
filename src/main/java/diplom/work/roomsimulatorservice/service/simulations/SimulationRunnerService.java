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
import diplom.work.roomsimulatorservice.service.strategies.TrainRlSimulationStrategy;
import diplom.work.roomsimulatorservice.util.ControllerType;
import diplom.work.roomsimulatorservice.util.RoomMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

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
        RoomDTO roomDto = roomClient.getRoomById(roomId).getBody();
        if (roomDto == null)
            throw new IllegalStateException("Room " + roomId + " not found");
        Room room = roomMapper.toEntity(roomDto);

        PidConfigDTO pidCfg = null;
        AiModelDTO   aiCfg  = null;

        ControllerType type = dto.controllerType();

        switch (type) {
            case PID -> {
                Integer cfgId = Objects.requireNonNull(dto.pidConfigId(),
                        "pidConfigId required for PID");
                pidCfg = pidClient.getConfigById(cfgId).getBody();
            }
            case PID_LSTM -> {
                Integer cfgId   = Objects.requireNonNull(dto.pidConfigId(),
                        "pidConfigId required for PID_LSTM");
                Integer modelId = Objects.requireNonNull(dto.modelId(),
                        "modelId required for PID_LSTM");
                pidCfg = pidClient.getConfigById(cfgId).getBody();
                aiCfg  = aiModelClient.getAiModelById(modelId).getBody();
            }
            case RL -> {
                Integer modelId = Objects.requireNonNull(dto.modelId(),
                        "modelId required for RL controller");
                aiCfg = aiModelClient.getAiModelById(modelId).getBody();
            }
            case TRAIN_RL -> 
            {}
            case AUTOTUNE_PID ->
            {}
            default -> throw new IllegalArgumentException("Unsupported controller: " + type);
        }

        double INITIAL_AIR_TEMP = 20.0;
        double MAX_HEATER_POWER = 3000.0;
        LocalDateTime START_TS  = LocalDateTime.of(2023, 9, 1, 0, 0);

        room.setRoomState(new RoomState(
                INITIAL_AIR_TEMP,
                room.getRoomParams().getSurfaces())
        );

        SimulationClock clock = new SimulationClock(
                START_TS,
                Duration.of(dto.timestepSeconds() != null ? dto.timestepSeconds() : 60, ChronoUnit.SECONDS)
        );

        SimulationDTO simDto = new SimulationDTO(
                roomId,
                type,
                "CREATED",
                dto.iterations(),
                dto.timestepSeconds()
        );
        Long simId = simulationClient.saveSimulation(roomId, simDto).getBody().id();

        SimulationContext ctx = SimulationContext.builder()
                .simulationId(simId)
                .configId(pidCfg != null ? pidCfg.id() : 0)
                .modelId(aiCfg  != null ? aiCfg.id()  : 0)
                .room(room)
                .thermalModel(simpleThermalModel)
                .strategy(strategyRegistry.strategy(type))
                .clock(clock)
                .iterations(dto.iterations())
                .maxHeaterPower(MAX_HEATER_POWER)
                .build();
        simulationRegistry.put(ctx);

        log.info("Start sim {}, controller {}, pidCfg={}, model={}",
                simId, type, pidCfg != null ? pidCfg.id() : "—",
                aiCfg  != null ? aiCfg.id()  : "—");

        engine.run(ctx);

        return simId;
    }
}
