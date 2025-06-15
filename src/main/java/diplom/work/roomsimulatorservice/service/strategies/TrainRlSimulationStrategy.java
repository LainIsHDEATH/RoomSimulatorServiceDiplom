package diplom.work.roomsimulatorservice.service.strategies;

import diplom.work.roomsimulatorservice.dto.RlTrainDTO.RlTrainDtoResponse;
import diplom.work.roomsimulatorservice.util.ControllerType;
import diplom.work.roomsimulatorservice.domain.SimulationContext;
import diplom.work.roomsimulatorservice.dto.RlModelDTO.RlModelDtoResponse;
import diplom.work.roomsimulatorservice.dto.RlTrainDTO.RlTrainDtoRequest;
import diplom.work.roomsimulatorservice.feign.RlTrainClient;
import diplom.work.roomsimulatorservice.service.calculations.TargetTemperatureCalculation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TrainRlSimulationStrategy implements SimulationStrategy {
    private final RlTrainClient rlTrainClient;
    private final TargetTemperatureCalculation targetTemperatureCalculation;

    @Override
    public ControllerType getType() {
        return ControllerType.TRAIN_RL;
    }

    @Override
    public double compute(SimulationContext ctx) {
        double tempSetpoint = targetTemperatureCalculation.getTargetTemperature(ctx.getClock().now());
        ctx.getRoom().getRoomState().setSetpointTemperature(tempSetpoint);

        RlTrainDtoRequest rlModelDtoRequest = new RlTrainDtoRequest(
                ctx.getSimulationId(),
                ctx.getRoom().getRoomState().getAirTemperature(),
                ctx.getRoom().getRoomState().getOutsideTemperature()
        );

        RlTrainDtoResponse rlTrainDtoResponse = rlTrainClient.compute(rlModelDtoRequest);
        log.info("OutputPower: {}", rlTrainDtoResponse.heaterPower());
        return rlTrainDtoResponse.heaterPower();
    }
}
