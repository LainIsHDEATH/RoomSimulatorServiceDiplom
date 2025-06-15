package diplom.work.roomsimulatorservice.service.strategies;

import diplom.work.roomsimulatorservice.util.ControllerType;
import diplom.work.roomsimulatorservice.domain.SimulationContext;
import diplom.work.roomsimulatorservice.dto.PidDTO.PIDRequest;
import diplom.work.roomsimulatorservice.dto.PidDTO.PIDResponse;
import diplom.work.roomsimulatorservice.feign.PIDClient;
import diplom.work.roomsimulatorservice.service.calculations.TargetTemperatureCalculation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class PidSimulationStrategy implements SimulationStrategy {

    private final PIDClient pidClient;
    private final TargetTemperatureCalculation targetTemperatureCalculation;

    @Override
    public ControllerType getType() {
        return ControllerType.PID;
    }

    @Override
    public double compute(SimulationContext ctx) {
        double pidSetpoint = targetTemperatureCalculation.getTargetTemperature(ctx.getClock().now()); // Уставка, яка буде подана в PID
        ctx.getRoom().getRoomState().setSetpointTemperature(pidSetpoint);

        PIDRequest pidRequest = new PIDRequest(
                ctx.getSimulationId(),
                ctx.getConfigId(),
                pidSetpoint,
                ctx.getRoom().getRoomState().getAirTemperature(),
                ctx.getClock().dtSeconds()
        );

        PIDResponse pidResponse = pidClient.compute(pidRequest);
        return pidResponse.outputPower();
    }
}
