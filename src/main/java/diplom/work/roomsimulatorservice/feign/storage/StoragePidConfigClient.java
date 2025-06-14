package diplom.work.roomsimulatorservice.feign.storage;

import diplom.work.roomsimulatorservice.dto.storage.storage_pid_client.PidConfigDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "storage-pid-service", url = "${feign.client.config.storage-service.url}")
public interface StoragePidConfigClient {

    @PostMapping("/api/pid-configs/room-configs/{roomId}")
    public ResponseEntity<PidConfigDTO> create(@PathVariable Long roomId,
                                               @RequestBody PidConfigDTO pidConfigDTO);

    @GetMapping("/api/pid-configs/room-configs/{roomId}/active")
    ResponseEntity<PidConfigDTO> getActive(@PathVariable Long roomId);
}
