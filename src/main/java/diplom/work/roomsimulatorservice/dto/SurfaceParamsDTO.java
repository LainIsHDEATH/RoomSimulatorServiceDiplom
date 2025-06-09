package diplom.work.roomsimulatorservice.dto;

public record SurfaceParamsDTO(
        String name,
        double area,
        double thickness,
        double density,
        double uInternal,
        double uExternal,
        double thermalConductivity,
        double specificHeat,
        String adjacentRoomName
) {
}
