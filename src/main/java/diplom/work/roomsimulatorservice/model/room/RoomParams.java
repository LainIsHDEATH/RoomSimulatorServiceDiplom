package diplom.work.roomsimulatorservice.model.room;


import diplom.work.roomsimulatorservice.model.surface.SurfaceParams;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class RoomParams implements Serializable {

    private final double volume; // V

    private final List<SurfaceParams> surfaces; // Roof, Walls, Doors, Windows

    private final double airDensity;           // кг/м³
    private final double airSpecificHeat;      // Дж/кг·К

    private final double airVolume;            // м³

    private final double airMass;              // кг
    private final double airHeatCapacity;      // Дж/К

    @Builder
    public RoomParams(double volume,
                      List<SurfaceParams> surfaces,
                      double airDensity,
                      double airSpecificHeat) {
        this.volume = volume;
        this.surfaces = surfaces;
        this.airDensity = airDensity;
        this.airSpecificHeat = airSpecificHeat;
        this.airVolume = volume;
        this.airMass = airDensity * airVolume;
        this.airHeatCapacity = airMass * airSpecificHeat;
    }
}
