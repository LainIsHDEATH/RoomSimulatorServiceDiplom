package diplom.work.roomsimulatorservice.service.strategies;

import diplom.work.roomsimulatorservice.util.ControllerType;
import diplom.work.roomsimulatorservice.domain.SimulationContext;
import diplom.work.roomsimulatorservice.dto.RlModelDTO.RlModelDtoRequest;
import diplom.work.roomsimulatorservice.dto.RlModelDTO.RlModelDtoResponse;
import diplom.work.roomsimulatorservice.feign.AiModelsClient;
import diplom.work.roomsimulatorservice.service.calculations.TargetTemperatureCalculation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RlSimulationStrategy implements SimulationStrategy {
    private final AiModelsClient aiModelsClient;
    private final TargetTemperatureCalculation targetTemperatureCalculation;

    @Override
    public ControllerType getType() {
        return ControllerType.RL;
    }

    @Override
    public double compute(SimulationContext ctx) {
        double tempSetpoint = targetTemperatureCalculation.getTargetTemperature(ctx.getClock().now());
        ctx.getRoom().getRoomState().setSetpointTemperature(tempSetpoint);

        RlModelDtoRequest rlModelDtoRequest = new RlModelDtoRequest(
                ctx.getModelId(),
                ctx.getRoom().getRoomState().getAirTemperature(),
                ctx.getRoom().getRoomState().getOutsideTemperature()
        );

        RlModelDtoResponse rlModelDtoResponse = aiModelsClient.computeHeatRL(rlModelDtoRequest);
        return rlModelDtoResponse.heaterPower();
    }
}
