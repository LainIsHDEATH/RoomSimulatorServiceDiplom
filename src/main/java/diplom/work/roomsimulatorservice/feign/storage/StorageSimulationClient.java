package diplom.work.roomsimulatorservice.feign.storage;

import diplom.work.roomsimulatorservice.dto.storage.storage_simulation_client.SimulationDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "storage-simulation-service", url = "${feign.client.config.storage-service.url}")
public interface StorageSimulationClient {

    @PostMapping("/api/simulations/{roomId}")
    public ResponseEntity<SimulationDTO> saveSimulation(@PathVariable Long roomId,
                                                        @RequestBody SimulationDTO simulationDTO);

    @GetMapping("/{id}")
    public ResponseEntity<SimulationDTO> getSimulationById(@PathVariable Long id);

    @PutMapping("/api/simulations/running/{id}")
    public ResponseEntity<SimulationDTO> markRunning(@PathVariable Long id);

    @PutMapping("/api/simulations/finished/{id}")
    public ResponseEntity<SimulationDTO> markFinished(@PathVariable Long id);

    @PutMapping("/api/simulations/failed/{id}")
    public ResponseEntity<SimulationDTO> markFailed(@PathVariable Long id);
}
