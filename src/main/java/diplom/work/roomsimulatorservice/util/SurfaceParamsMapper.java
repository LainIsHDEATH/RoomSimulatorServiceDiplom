package diplom.work.roomsimulatorservice.util;

import diplom.work.roomsimulatorservice.domain.room.SurfaceParams;
import diplom.work.roomsimulatorservice.dto.SurfaceParamsDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SurfaceParamsMapper {
    SurfaceParams toEntity(SurfaceParamsDTO dto);

    SurfaceParamsDTO toDto(SurfaceParams entity);
}