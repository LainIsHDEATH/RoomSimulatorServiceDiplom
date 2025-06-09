package diplom.work.roomsimulatorservice.dto.PidDTO;

public record PIDRequest(
        String roomName,
        double targetTemperature,
        double currentTemperature,
        double deltaTime
) {}