package diplom.work.roomsimulatorservice.dto.storage.storage_simulation_client;

import diplom.work.roomsimulatorservice.util.ControllerType;

public record SimulationDTO(
        Long id,
        ControllerType controllerType,
        String status,
        Long iterations,
        Integer timestepSeconds
) {
}
