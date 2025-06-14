package diplom.work.roomsimulatorservice.controller;

import diplom.work.roomsimulatorservice.dto.AutotuneCohenCoonRequestDTO;
import diplom.work.roomsimulatorservice.dto.SimulationRequestDTO;
import diplom.work.roomsimulatorservice.dto.SimulationResponseDTO;
import diplom.work.roomsimulatorservice.dto.storage.storage_pid_client.PidConfigDTO;
import diplom.work.roomsimulatorservice.feign.storage.StoragePidConfigClient;
import diplom.work.roomsimulatorservice.model.room.RoomParams;
import diplom.work.roomsimulatorservice.model.room.RoomState;
import diplom.work.roomsimulatorservice.service.RoomRegistryService;
import diplom.work.roomsimulatorservice.service.RoomSimulationService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/simulator-runner")
@RequiredArgsConstructor
public class RoomController {

    private final RoomSimulationService simulationService;
    private final RoomRegistryService roomRegistryService;
    private final StoragePidConfigClient storagePidConfigClient;

    @PostMapping("/start")
    public ResponseEntity<SimulationResponseDTO> startSimulation(@RequestBody SimulationRequestDTO simulationRequestDTO) {
        System.out.println(simulationRequestDTO.toString());
        Long roomId = simulationRequestDTO.roomId();
        String controllerType = simulationRequestDTO.controllerType();
        Long pidConfigId = simulationRequestDTO.pidConfigId();
        Long modelId = simulationRequestDTO.modelId();
        Long iterations = simulationRequestDTO.iterations();
        Long timestepSeconds = simulationRequestDTO.timestepSeconds();
        Double initialAirTemp = 20.0;
        return ResponseEntity.ok(
                new SimulationResponseDTO(
                        simulationService.startSimulation(roomId, initialAirTemp, controllerType, pidConfigId, modelId, iterations, timestepSeconds)));
    }

    @PostMapping("/autotune/cohen-coon")
    public ResponseEntity<SimulationResponseDTO> autotuneCohenCoon(@RequestBody AutotuneCohenCoonRequestDTO autotuneCohenCoonRequestDTO) {
        Long roomId = autotuneCohenCoonRequestDTO.roomId();
        System.out.println(roomId);

        String controllerType = "PID";
        PidConfigDTO pidConfigDTO = new PidConfigDTO(roomId, 0.0, 0.0, 0.0, "Autotune Cohen-Coon", true);
        Long pidConfigId = storagePidConfigClient.create(roomId, pidConfigDTO).getBody().id();
        Long modelId = null;
        Long iterations = 3000L;
        Long timestepSeconds = 5L;
        Double initialAirTemp = 20.0;
        return ResponseEntity.ok(
                new SimulationResponseDTO(
                        simulationService.startSimulation(roomId, initialAirTemp, controllerType, pidConfigId, modelId, iterations, timestepSeconds)));
    }

    @GetMapping("/state")
    public RoomState getState(@RequestParam String roomName) {
        return roomRegistryService.getByName(roomName).get().getRoomState();
    }
}
