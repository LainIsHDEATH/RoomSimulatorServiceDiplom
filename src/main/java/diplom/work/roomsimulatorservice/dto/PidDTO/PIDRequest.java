package diplom.work.roomsimulatorservice.dto.PidDTO;

public record PIDRequest(
        Long simulationId,
        Integer pidConfigId,
        double targetTemperature,
        double currentTemperature,
        double deltaTime
) {}