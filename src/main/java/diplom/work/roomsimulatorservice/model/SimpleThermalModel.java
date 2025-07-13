package diplom.work.roomsimulatorservice.model;

import diplom.work.roomsimulatorservice.domain.SimulationContext;
import diplom.work.roomsimulatorservice.domain.room.Room;
import diplom.work.roomsimulatorservice.domain.room.RoomParams;
import diplom.work.roomsimulatorservice.domain.room.RoomState;
import diplom.work.roomsimulatorservice.service.calculations.OutsideTempCalculation;
import diplom.work.roomsimulatorservice.service.calculations.PhysicsCalculation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.Queue;

@Component("thermalModel0")
@RequiredArgsConstructor
@Slf4j
public class SimpleThermalModel implements ThermalModel {

    private final PhysicsCalculation physicsCalculation;
    private final OutsideTempCalculation outsideTempCalculation;

    @Override
    public void applyStep(SimulationContext ctx, double heaterPower) {
        Room room = ctx.getRoom();
        RoomParams roomParams = room.getRoomParams();
        RoomState roomState = room.getRoomState();

        Queue<Double> extQueue = ctx.state("OUT_TEMP_QUEUE", () ->
                outsideTempCalculation.computeOutsideTemperature(2023)
        );

        Optional<Double> tOutOpt = Optional.ofNullable(extQueue.poll());
        if (!tOutOpt.isPresent()) {
            ctx.putState("OUT_TEMP_QUEUE", outsideTempCalculation.computeOutsideTemperature(2023));
            tOutOpt = Optional.ofNullable(((Queue<Double>) ctx.state("OUT_TEMP_QUEUE", () -> null)).poll());
        }
        double outsideTemp = tOutOpt.orElse(roomState.getOutsideTemperature());

        double heaterMaxPower = ctx.getMaxHeaterPower();
        double heaterPowerWatts = Math.min(heaterMaxPower,
                Math.max(0, heaterMaxPower * (heaterPower / 100.0)));
        log.info("Heater Power: " + heaterPowerWatts);

        double totalHeatFlow = physicsCalculation.computeSurfaceHeatFlow(roomParams, roomState);
        log.info("Total Flow: " + totalHeatFlow);

        double totalQ = totalHeatFlow + heaterPowerWatts;
        log.info("Total Q: " + totalQ);

        double dT_air = totalQ / roomParams.getAirHeatCapacity();
        double newAirTemp = roomState.getAirTemperature() +
                dT_air * ctx.getClock().getStep().toSeconds();

        Map<String, Double> newSurfaceTemps = physicsCalculation.computeNewSurfacesTemps(
                roomParams,
                roomState,
                roomState.getOutsideTemperature(),
                ctx.getClock().getStep().toSeconds());

        log.info("[SIM:{}] t={}, step={}s, heaterMaxPower={}, heaterPowerWatts={}, T_air(prev)={}, T_air(new)={}, T_setpoint={}, T_out={}, totalHeatFlowW={}, totalQ_W={}, surfaces={} ",
                ctx.getSimulationId(),
                ctx.getClock().now(),
                ctx.getClock().getStep().toSeconds(),
                heaterMaxPower,
                heaterPowerWatts,
                roomState.getAirTemperature(),
                newAirTemp,
                roomState.getSetpointTemperature(),
                outsideTemp,
                totalHeatFlow,
                totalQ,
                newSurfaceTemps);

        roomState.update(
                newAirTemp,
                outsideTemp,
                heaterPowerWatts,
                newSurfaceTemps,
                false,
                false,
                0
        );
    }
}
