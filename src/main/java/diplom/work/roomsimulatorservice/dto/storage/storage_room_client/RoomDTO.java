package diplom.work.roomsimulatorservice.dto.storage.storage_room_client;

import diplom.work.roomsimulatorservice.domain.room.RoomParams;

public record RoomDTO(
        Long id,
        String name,
        RoomParams roomParams
) {
}
