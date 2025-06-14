package diplom.work.roomsimulatorservice.feign.storage;

import diplom.work.roomsimulatorservice.dto.storage.storage_user_client.UserResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "storage-user-service", url = "${feign.client.config.storage-service.url}")
public interface StorageUserClient {

    @GetMapping("/api/users/email/{email}")
    ResponseEntity<UserResponseDTO> getUserByEmail(@PathVariable String email);
}
