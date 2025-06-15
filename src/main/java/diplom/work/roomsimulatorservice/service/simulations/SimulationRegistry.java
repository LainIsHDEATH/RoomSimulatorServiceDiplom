package diplom.work.roomsimulatorservice.service.simulations;

import diplom.work.roomsimulatorservice.domain.SimulationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SimulationRegistry {

    private final Map<Long, SimulationContext> running = new ConcurrentHashMap<>();

    public void put(SimulationContext ctx) {
        running.put(ctx.getSimulationId(), ctx);
    }

    public Optional<SimulationContext> get(long id) {
        return Optional.ofNullable(running.get(id));
    }

    @Scheduled(fixedRate = 60000)
    public void remove() {
        for (SimulationContext ctx : running.values()) {
            if (ctx.isDone()) {
                running.remove(ctx.getSimulationId());
            }
        }
    }
}
