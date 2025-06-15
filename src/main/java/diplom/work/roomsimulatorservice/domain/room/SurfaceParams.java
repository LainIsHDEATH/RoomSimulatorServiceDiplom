package diplom.work.roomsimulatorservice.domain.room;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@ToString
public class SurfaceParams implements Serializable {
    private final String name;

    private final double area;          // A
    private final double thickness; // d
    private final double density; // p = m/V

    @JsonProperty("uInternal")
    private final double uInternal;     // U внутрішня
    @JsonProperty("uExternal")
    private final double uExternal;     // U зовнішня
    private final double thermalConductivity; // k_lambda // λ
    private final double specificHeat; // c
    private final double mass; // m = ρ · A · d
    private final double heatCapacity;  // C = c * m
    private final double totalU; // U // U = 1 / (1/Uвнутр + d/λ + 1/Uзовн)

    private final String adjacentRoomName;

    @JsonProperty("uInternal")
    public double getUInternal() { return uInternal; }

    @JsonProperty("uExternal")
    public double getUExternal() { return uExternal; }

    @JsonCreator
    @Builder
    public SurfaceParams(@JsonProperty("name") String name,
                         @JsonProperty("area") double area,
                         @JsonProperty("thickness") double thickness,
                         @JsonProperty("density") double density,
                         @JsonProperty("uInternal") @JsonAlias("uinternal") double uInternal,
                         @JsonProperty("uExternal") @JsonAlias("uexternal") double uExternal,
                         @JsonProperty("thermalConductivity") double thermalConductivity,
                         @JsonProperty("specificHeat") double specificHeat,
                         @JsonProperty("adjacentRoomName") String adjacentRoomName) {
        this.name = name;
        this.area = area;
        this.thickness = thickness;
        this.density = density;
        this.uInternal = uInternal;
        this.uExternal = uExternal;
        this.thermalConductivity = thermalConductivity;
        this.specificHeat = specificHeat;
        this.adjacentRoomName = adjacentRoomName;

        this.mass = density * area * thickness;
        this.heatCapacity = specificHeat * mass;
        this.totalU = 1.0 / (1.0 / uInternal + thickness / thermalConductivity + 1.0 / uExternal);
    }
}
