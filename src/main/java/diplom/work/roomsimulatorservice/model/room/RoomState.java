package diplom.work.roomsimulatorservice.model.room;


import diplom.work.roomsimulatorservice.model.surface.SurfaceParams;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Getter
@NoArgsConstructor
public class RoomState {
    String roomName;
    // Температура воздуха в комнате (в °C)
    private double airTemperature = 20;
    // Подаваемая мощность нагрева (в Вт)
    private double heaterPower = 0;
    // Карта: имя поверхности → её температура
    private final Map<String, Double> surfaceTemperatures = new HashMap<>();
    // Открытые поверхности
    private boolean windowOpen = false;
    private boolean doorOpen = false;
    // Присутствие людей
    private int peopleCount = 0;
    // Время последнего обновления
    private Instant lastUpdated = Instant.now();

    public RoomState(String roomName, double initialAirTemp) {
        this.roomName = roomName;
        this.airTemperature = initialAirTemp;
    }

    public double getSurfaceTemperature(String key) {
        return surfaceTemperatures.getOrDefault(key, 0.0);
    }

    /**
     * Инициализировать поверхности один раз при старте
     */
    public void initSurfaces(RoomParams params) {
        params.getSurfaces()
                .forEach(s -> surfaceTemperatures
                        .put(s.getName(), airTemperature - 2));
    }

    /**
     * Обновить состояние через один шаг симуляции
     */
    public void update(
            double newAirTemp,
            double newHeaterPower,
            Map<String, Double> newSurfaceTemps,
            boolean windowOpen,
            boolean doorOpen,
            int peopleCount
    ) {
        this.airTemperature = newAirTemp;
        this.heaterPower = newHeaterPower;
        this.windowOpen = windowOpen;
        this.doorOpen = doorOpen;
        this.peopleCount = peopleCount;
        this.lastUpdated = Instant.now();
        this.surfaceTemperatures.clear();
        this.surfaceTemperatures.putAll(newSurfaceTemps);
    }
}
