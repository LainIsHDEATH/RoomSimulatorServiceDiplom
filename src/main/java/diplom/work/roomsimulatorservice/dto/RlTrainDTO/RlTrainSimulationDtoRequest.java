package diplom.work.roomsimulatorservice.dto.RlTrainDTO;

import diplom.work.roomsimulatorservice.util.ControllerType;

public record RlTrainSimulationDtoRequest(
        long roomId,
        ControllerType controllerType,
        Long iterations,
        Integer timestepSeconds
) {
}
