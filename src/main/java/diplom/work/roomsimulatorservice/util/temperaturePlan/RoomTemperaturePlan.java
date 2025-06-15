package diplom.work.roomsimulatorservice.util.temperaturePlan;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class RoomTemperaturePlan {
    private String roomName;
    private List<TemperatureSchedule> schedules;
}
