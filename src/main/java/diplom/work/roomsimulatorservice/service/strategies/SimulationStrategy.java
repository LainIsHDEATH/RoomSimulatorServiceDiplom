package diplom.work.roomsimulatorservice.service.strategies;

import diplom.work.roomsimulatorservice.util.ControllerType;
import diplom.work.roomsimulatorservice.domain.SimulationContext;
import org.springframework.stereotype.Component;

@Component
public interface SimulationStrategy {
    ControllerType getType();  // тип контроллера, для регистрации в мапе

    double compute(SimulationContext ctx);
}
