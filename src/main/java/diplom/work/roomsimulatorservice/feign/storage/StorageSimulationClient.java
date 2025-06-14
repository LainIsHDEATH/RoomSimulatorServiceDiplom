package diplom.work.roomsimulatorservice.feign.storage;

import diplom.work.roomsimulatorservice.dto.storage.storage_simulation_client.SimulationDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "storage-simulation-service", url = "${feign.client.config.storage-service.url}")
public interface StorageSimulationClient {

    @PostMapping("/api/simulations/{roomId}")
    public ResponseEntity<SimulationDTO> saveSimulation(@PathVariable Long roomId,
                                                        @RequestBody SimulationDTO simulationDTO);
}
