package diplom.work.roomsimulatorservice.service.strategies;

import diplom.work.roomsimulatorservice.util.ControllerType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
@Getter
public class StrategyRegistry {
    private final Map<ControllerType, SimulationStrategy> registry =
            new EnumMap<>(ControllerType.class);

    public StrategyRegistry(List<SimulationStrategy> strategies) {
        strategies.forEach(s -> registry.put(s.getType(), s));
    }

    public SimulationStrategy strategy(ControllerType type) {
        SimulationStrategy s = registry.get(type);
        if (s == null) {
            throw new IllegalArgumentException("No strategy registered for " + type);
        }
        return s;
    }

    public Map<ControllerType, SimulationStrategy> all() {
        return Map.copyOf(registry);
    }
}
