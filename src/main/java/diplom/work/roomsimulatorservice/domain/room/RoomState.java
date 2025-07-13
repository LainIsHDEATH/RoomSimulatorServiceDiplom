package diplom.work.roomsimulatorservice.domain.room;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class RoomState {
    private double airTemperature = 20;
    private double outsideTemperature;
    private double setpointTemperature;
    private double predictedTemperature;

    private double heaterPower = 0;

    private final Map<String, Double> surfaceTemperatures = new HashMap<>();

    private boolean windowOpen = false;
    private boolean doorOpen = false;

    private int peopleCount = 0;

    private Instant lastUpdated = Instant.now();

    public RoomState(double initialAirTemp, List<SurfaceParams> surfaces) {
        this.airTemperature = initialAirTemp;

        for (SurfaceParams s : surfaces) {
            surfaceTemperatures.put(s.getName(), initialAirTemp-2);
        }
    }

    public double getSurfaceTemperature(String key) {
        return surfaceTemperatures.getOrDefault(key, 0.0);
    }

    public void update(
            double newAirTemp,
            double outsideTemperature,
            double newHeaterPower,
            Map<String, Double> newSurfaceTemps,
            boolean windowOpen,
            boolean doorOpen,
            int peopleCount
    ) {
        this.airTemperature = newAirTemp;
        this.outsideTemperature = outsideTemperature;
        this.heaterPower = newHeaterPower;
        this.windowOpen = windowOpen;
        this.doorOpen = doorOpen;
        this.peopleCount = peopleCount;
        this.lastUpdated = Instant.now();
        this.surfaceTemperatures.clear();
        this.surfaceTemperatures.putAll(newSurfaceTemps);
    }
}
