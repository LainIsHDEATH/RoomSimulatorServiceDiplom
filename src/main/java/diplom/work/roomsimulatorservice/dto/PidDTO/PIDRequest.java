package diplom.work.roomsimulatorservice.dto.PidDTO;

import java.time.LocalDateTime;

public record PIDRequest(
        Long pidConfigId,
        double targetTemperature,
        double currentTemperature,
        double deltaTime,
        LocalDateTime timestamp
) {}