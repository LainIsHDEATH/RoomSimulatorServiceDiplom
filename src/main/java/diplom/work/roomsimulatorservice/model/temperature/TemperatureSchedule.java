package diplom.work.roomsimulatorservice.model.temperature;

import java.time.LocalTime;

public class TemperatureSchedule {
    private LocalTime startTime;   // Например: 00:00
    private LocalTime endTime;     // Например: 04:00
    private double targetTemperature;
}
