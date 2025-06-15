package diplom.work.roomsimulatorservice.dto;

import diplom.work.roomsimulatorservice.util.ControllerType;

public record SimulationRequestDTO(
    long roomId,
    ControllerType controllerType,
    Integer pidConfigId,
    Integer modelId,
    Long iterations,
    Integer timestepSeconds
//    List<SchedulePoint> schedule;
) {
}
