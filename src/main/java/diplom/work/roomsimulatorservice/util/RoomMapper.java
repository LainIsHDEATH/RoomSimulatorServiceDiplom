package diplom.work.roomsimulatorservice.util;

import diplom.work.roomsimulatorservice.domain.room.Room;
import diplom.work.roomsimulatorservice.dto.storage.storage_room_client.RoomDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoomMapper {
    @Mapping(target = "roomState", ignore = true)
    Room toEntity(RoomDTO roomDTO);

    RoomDTO toDto(Room room);
}