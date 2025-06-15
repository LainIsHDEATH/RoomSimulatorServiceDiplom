package diplom.work.roomsimulatorservice.service.strategies;

import diplom.work.roomsimulatorservice.config.StrategyProperties;
import diplom.work.roomsimulatorservice.dto.LstmModelDTO.LstmSensorDTO;
import diplom.work.roomsimulatorservice.util.ControllerType;
import diplom.work.roomsimulatorservice.domain.SimulationContext;
import diplom.work.roomsimulatorservice.domain.room.RoomState;
import diplom.work.roomsimulatorservice.dto.LstmModelDTO.LstmModelDtoRequest;
import diplom.work.roomsimulatorservice.dto.PidDTO.PIDRequest;
import diplom.work.roomsimulatorservice.dto.PidDTO.PIDResponse;
import diplom.work.roomsimulatorservice.feign.AiModelsClient;
import diplom.work.roomsimulatorservice.feign.PIDClient;
import diplom.work.roomsimulatorservice.service.calculations.TargetTemperatureCalculation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class PidLstmSimulationStrategy implements SimulationStrategy {

    private final AiModelsClient aiModelsClient;
    private final PIDClient pidClient;
    private final TargetTemperatureCalculation targetTemperatureCalculation;
    private final StrategyProperties props;   // @ConfigurationProperties

    /*───────────────────────────── ВНУТРЕННЕЕ СОСТОЯНИЕ ─────────────────────────────*/

    private record PidLstmState(
            double               lastCorrectedSetpoint,
            int                  stepsAfterPrediction,
            Deque<LstmSensorDTO> buffer
    ) {
        private static final int MAX = 30;

        PidLstmState() {
            this(0.0, 0, new ArrayDeque<>(MAX));
        }

        PidLstmState(double newSetpoint, int newSteps, PidLstmState prev) {
            this(newSetpoint, newSteps, prev.buffer);
        }

        void push(LstmSensorDTO dto) {
            if (buffer.size() >= MAX) buffer.pollFirst();
            buffer.addLast(dto);
        }

        boolean ready()           { return buffer.size() == MAX; }

        List<LstmSensorDTO> snapshot() { return new ArrayList<>(buffer); }
    }

    @Override
    public ControllerType getType() {
        return ControllerType.PID_LSTM;
    }

    private double predictTemperature(Integer modelId, Deque<LstmSensorDTO> seq) {
        LstmModelDtoRequest req = new LstmModelDtoRequest(modelId, new ArrayList<>(seq));
        try {
            return aiModelsClient.predictTemperatureLSTM(req).predictedTemp();
        } catch (Exception e) {
            log.warn("LSTM fallback: {}", e.getMessage());
            return seq.peekLast().tempIn();
        }
    }


    @Override
    public double compute(SimulationContext ctx) {

        PidLstmState st = ctx.state(this, PidLstmState::new);

        RoomState    room     = ctx.getRoom().getRoomState();
        double       dt       = ctx.getClock().dtSeconds();
        LocalDateTime ts      = ctx.getClock().now();

        st.push(new LstmSensorDTO(
                room.getAirTemperature(),
                room.getOutsideTemperature(),
                targetTemperatureCalculation.getTargetTemperature(ts),
                room.getHeaterPower()
        ));

        double userSetpoint = targetTemperatureCalculation.getTargetTemperature(ts);
        double pidSetpoint  = (st.lastCorrectedSetpoint == 0)
                ? userSetpoint
                : st.lastCorrectedSetpoint;

        if (st.ready() && st.stepsAfterPrediction >= props.predictionInterval()) {

            double tPred = predictTemperature(ctx.getModelId(), st.buffer);
            room.setPredictedTemperature(tPred);

            double correction = (tPred - userSetpoint) * props.correctionGain();
            pidSetpoint = userSetpoint + correction;

            st = new PidLstmState(pidSetpoint, 0, st);
        } else {
            st = new PidLstmState(pidSetpoint, st.stepsAfterPrediction + 1, st);
        }

        ctx.putState(this, st);
        room.setSetpointTemperature(pidSetpoint);

        PIDRequest pidReq = new PIDRequest(
                ctx.getSimulationId(),
                ctx.getConfigId(),
                pidSetpoint,
                room.getAirTemperature(),
                dt
        );

        PIDResponse pidResp = pidClient.compute(pidReq);
        return pidResp.outputPower();
    }
}
