package diplom.work.roomsimulatorservice.model.room;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import diplom.work.roomsimulatorservice.model.surface.SurfaceParams;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class RoomParams implements Serializable {

    private final double volume;
    private final List<SurfaceParams> surfaces;
    private final double airDensity;
    private final double airSpecificHeat;

    private final double airVolume;
    private final double airMass;
    private final double airHeatCapacity;

    @JsonCreator
    public RoomParams(
            @JsonProperty("volume")          double volume,
            @JsonProperty("airDensity")      double airDensity,
            @JsonProperty("airSpecificHeat") double airSpecificHeat,
            @JsonProperty("surfaces")        List<SurfaceParams> surfaces
    ) {
        this.volume          = volume;
        this.airDensity      = airDensity;
        this.airSpecificHeat = airSpecificHeat;
        this.surfaces        = surfaces;

        this.airVolume       = volume;
        this.airMass         = airDensity * volume;
        this.airHeatCapacity = airMass * airSpecificHeat;
    }
}
