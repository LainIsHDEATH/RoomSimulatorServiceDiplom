package diplom.work.roomsimulatorservice.controller;

import diplom.work.roomsimulatorservice.domain.SimulationContext;
import diplom.work.roomsimulatorservice.dto.RlTrainDTO.RlTrainDtoResponse;
import diplom.work.roomsimulatorservice.dto.RlTrainDTO.RlTrainSimulationDtoRequest;
import diplom.work.roomsimulatorservice.dto.SimulationRequestDTO;
import diplom.work.roomsimulatorservice.dto.SimulationResponseDTO;
import diplom.work.roomsimulatorservice.dto.SimulationStatusDTO;
import diplom.work.roomsimulatorservice.dto.autotunePidDTO.AutotunePidSimulationDtoRequest;
import diplom.work.roomsimulatorservice.service.simulations.SimulationRegistry;
import diplom.work.roomsimulatorservice.service.simulations.SimulationRunnerService;
import diplom.work.roomsimulatorservice.util.ControllerType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/simulations")
@RequiredArgsConstructor
@Slf4j
public class SimulationController {

    private final SimulationRunnerService runnerService;
    private final SimulationRegistry registry;

    @PostMapping("/start")
    public ResponseEntity<SimulationResponseDTO> start(@RequestBody SimulationRequestDTO dto) {
        log.info(dto.controllerType().name());
        long simulationId = runnerService.startSimulation(dto);
        return ResponseEntity
                .accepted()
                .body(new SimulationResponseDTO(simulationId));
    }

    @PostMapping("/autotune/cohen-coon")
    public ResponseEntity<SimulationResponseDTO> autotune(@RequestBody AutotunePidSimulationDtoRequest dto) {
        long simulationId = runnerService.startSimulation(new SimulationRequestDTO(
                dto.roomId(), ControllerType.AUTOTUNE_PID, 0, 0, dto.iterations(), dto.timestepSeconds()
        ));
        return ResponseEntity
                .accepted()
                .body(new SimulationResponseDTO(simulationId));
    }

    @PostMapping("/train-rl")
    public ResponseEntity<SimulationResponseDTO> rlTrain(@RequestBody RlTrainSimulationDtoRequest dto) {
        long simulationId = runnerService.startSimulation(new SimulationRequestDTO(
                dto.roomId(), ControllerType.TRAIN_RL, 0, 0, dto.iterations(), dto.timestepSeconds()
        ));
        return ResponseEntity
                .accepted()
                .body(new SimulationResponseDTO(simulationId));
    }


    @GetMapping("/{id}/status")
    public ResponseEntity<SimulationStatusDTO> status(@PathVariable long id) {

        SimulationContext ctx = registry.get(id)
                .orElseThrow(() -> new IllegalArgumentException("Simulation " + id + " not found"));

        SimulationStatusDTO dto = new SimulationStatusDTO(
                id,
                ctx.isDone() ? "FINISHED" : "RUNNING",
                ctx.getProgress()
        );
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{id}/room-state")
    public ResponseEntity<?> roomState(@PathVariable long id) {

        SimulationContext ctx = registry.get(id)
                .orElseThrow(() -> new IllegalArgumentException("Simulation " + id + " not found"));

        return ResponseEntity.ok(ctx.getRoom().getRoomState());
    }
}
