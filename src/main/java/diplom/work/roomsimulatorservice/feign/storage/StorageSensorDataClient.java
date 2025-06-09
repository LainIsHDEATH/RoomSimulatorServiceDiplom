package diplom.work.roomsimulatorservice.feign.storage;

import diplom.work.roomsimulatorservice.dto.storage.storage_sensor_client.SensorDataDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "storage-service", url = "${feign.client.config.storage-service.url}")
public interface StorageSensorDataClient {

    @PostMapping("/api/sensor-data/simulation-data/{simulationId}/step")
    ResponseEntity<SensorDataDTO> addStep(@PathVariable Long simulationId,
                                          @RequestBody SensorDataDTO sensorDataDTO);

    @PostMapping("/simulation-data/{simulationId}/batch")
    ResponseEntity<Void> addBatch(@PathVariable Long simulationId,
                                  @RequestBody List<SensorDataDTO> sensorDataDtoList);

    @GetMapping("/simulation-data/{simulationId}/last")
    ResponseEntity<SensorDataDTO> getLastBySimulationId(@PathVariable Long simulationId);


}
