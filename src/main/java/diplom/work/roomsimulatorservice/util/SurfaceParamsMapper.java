package diplom.work.roomsimulatorservice.util;

import diplom.work.roomsimulatorservice.dto.SurfaceParamsDTO;
import diplom.work.roomsimulatorservice.model.surface.SurfaceParams;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SurfaceParamsMapper {
    SurfaceParams toEntity(SurfaceParamsDTO dto);

    SurfaceParamsDTO toDto(SurfaceParams entity);
}