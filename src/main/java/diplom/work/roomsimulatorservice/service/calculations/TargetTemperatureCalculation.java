package diplom.work.roomsimulatorservice.service.calculations;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TargetTemperatureCalculation {

    public double getTargetTemperature(LocalDateTime timestamp) {
        int hour = timestamp.getHour();

        if (hour < 4) {
            return 20.0;
        } else if (hour < 12) {
            return 19.0;
        } else {
            return 23.0;
        }
    }
}
