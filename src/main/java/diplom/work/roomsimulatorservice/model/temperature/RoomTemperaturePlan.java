package diplom.work.roomsimulatorservice.model.temperature;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class RoomTemperaturePlan {
    private String roomName;
    private List<TemperatureSchedule> schedules;
}
