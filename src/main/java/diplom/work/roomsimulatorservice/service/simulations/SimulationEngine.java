package diplom.work.roomsimulatorservice.service.simulations;

import diplom.work.roomsimulatorservice.domain.SimulationContext;
import diplom.work.roomsimulatorservice.domain.room.RoomState;
import diplom.work.roomsimulatorservice.dto.storage.storage_sensor_client.SensorDataDTO;
import diplom.work.roomsimulatorservice.feign.storage.StorageSimulationClient;
import diplom.work.roomsimulatorservice.service.KafkaSensorProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SimulationEngine {

    private final KafkaSensorProducer producer;
    private final StorageSimulationClient storageSimulationClient;

    @Async
    public void run(SimulationContext ctx) {
        try {
            storageSimulationClient.markRunning(ctx.getSimulationId());
            for (long step = 0; step < ctx.getIterations(); step++) {

                double heaterPower = ctx.getStrategy().compute(ctx);
                log.info("Heater power: " + heaterPower);
                ctx.getThermalModel().applyStep(ctx, heaterPower);

                RoomState state = ctx.getRoom().getRoomState();
                SensorDataDTO sensorDataDTO = new SensorDataDTO(
                        ctx.getSimulationId(),
                        ctx.getClock().now(),
                        state.getAirTemperature(),
                        state.getOutsideTemperature(),
                        state.getSetpointTemperature(),
                        state.getHeaterPower(),
                        state.getPredictedTemperature(),
                        state.isWindowOpen(),
                        state.isDoorOpen(),
                        state.getPeopleCount()
                );

                producer.sendSensorData(
                        sensorDataDTO
                );

                ctx.getClock().tick();
            }

            storageSimulationClient.markFinished(ctx.getSimulationId());

        } catch (Exception ex) {
            storageSimulationClient.markFailed(ctx.getSimulationId());
            throw ex;
        }
    }
}
