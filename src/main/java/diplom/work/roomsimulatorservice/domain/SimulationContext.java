package diplom.work.roomsimulatorservice.domain;

import diplom.work.roomsimulatorservice.domain.room.Room;
import diplom.work.roomsimulatorservice.model.ThermalModel;
import diplom.work.roomsimulatorservice.service.strategies.SimulationStrategy;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

@RequiredArgsConstructor
@Getter
@Builder
public class SimulationContext {

    private final Map<Object, Object> localStates = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public <T> T state(Object key, Supplier<T> init) {
        return (T) localStates.computeIfAbsent(key, k -> init.get());
    }

    public void putState(Object key, Object state) {
        localStates.put(key, state);
    }

    private final long   simulationId;
    private final int    configId;
    private final int    modelId;

    private final Room               room;
    private final ThermalModel       thermalModel;
    private final SimulationStrategy strategy;
    private final SimulationClock    clock;

    private final long   iterations;
    private final double maxHeaterPower;

    public boolean isDone() {
        return clock.getStep().toSeconds() * iterations >=
                clock.now().getSecond() - clock.getStart().getSecond() ;
    }

    public double getProgress() {
        return 100.0 * (clock.getStep().toSeconds() * iterations) /
                (clock.now().getSecond() - clock.getStart().getSecond());
    }
}
