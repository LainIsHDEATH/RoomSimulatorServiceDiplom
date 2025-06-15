package diplom.work.roomsimulatorservice.dto.autotunePidDTO;

import diplom.work.roomsimulatorservice.util.ControllerType;

public record AutotunePidSimulationDtoRequest(
        long roomId,
        ControllerType controllerType,
        Long iterations,
        Integer timestepSeconds
) {
}
