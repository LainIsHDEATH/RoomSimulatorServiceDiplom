package diplom.work.roomsimulatorservice.service.strategies;

import diplom.work.roomsimulatorservice.util.ControllerType;
import diplom.work.roomsimulatorservice.domain.SimulationContext;
import diplom.work.roomsimulatorservice.dto.autotunePidDTO.AutotunePidDtoRequest;
import diplom.work.roomsimulatorservice.dto.autotunePidDTO.AutotunePidDtoResponse;
import diplom.work.roomsimulatorservice.feign.AutotuneClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AutotunePidSimulationStrategy implements SimulationStrategy {

    private final AutotuneClient autotuneClient;

    @Override
    public ControllerType getType() {
        return ControllerType.AUTOTUNE_PID;
    }

    @Override
    public double compute(SimulationContext ctx) {
        AutotunePidDtoRequest autotunePidDtoRequest = new AutotunePidDtoRequest(
                ctx.getRoom().getRoomState().getAirTemperature()
        );

        AutotunePidDtoResponse autotunePidDtoResponse = autotuneClient.compute(ctx.getSimulationId(), autotunePidDtoRequest);
        return autotunePidDtoResponse.outputPower();
    }
}
