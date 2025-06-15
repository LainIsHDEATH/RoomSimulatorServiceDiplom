package diplom.work.roomsimulatorservice.domain;

import lombok.Getter;

import java.time.Duration;
import java.time.LocalDateTime;

@Getter
public class SimulationClock {

    private LocalDateTime current;
    private final LocalDateTime start;
    private final Duration step;

    public SimulationClock(LocalDateTime startAt, Duration step) {
        this.current = startAt;
        this.step = step;
        this.start = startAt;
    }

    public LocalDateTime now() {
        return current;
    }

    public double dtSeconds() {
        return step.toSeconds();
    }

    public void tick() {
        current = current.plus(step);
    }
}
