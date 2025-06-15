package diplom.work.roomsimulatorservice.service.calculations;

import diplom.work.roomsimulatorservice.domain.room.SurfaceParams;

public class OpenWindowCalculation {

    public SurfaceParams createOpenSurface(SurfaceParams original) {
        return SurfaceParams.builder()
                .name(original.getName())
                .area(original.getArea())
                .thickness(0.001) // почти ноль
                .density(1.293) // воздух
                .thermalConductivity(0.025) // воздух
                .uInternal(1000.0)
                .uExternal(1000.0)
                .specificHeat(1005.4)
                .adjacentRoomName(original.getAdjacentRoomName())
                .build();
    }
}
