package diplom.work.roomsimulatorservice.feign.storage;

import diplom.work.roomsimulatorservice.dto.storage.storage_room_client.RoomDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "storage-room-service", url = "${feign.client.config.storage-service.url}")
public interface StorageRoomClient {

    @GetMapping("/api/rooms/{roomId}")
    ResponseEntity<RoomDTO> getRoomById(@PathVariable Long roomId);
}
