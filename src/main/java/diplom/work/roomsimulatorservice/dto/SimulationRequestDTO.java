package diplom.work.roomsimulatorservice.dto;

public record SimulationRequestDTO(
        Long roomId,
        String controllerType,  // e.g. "PID", "PID+LSTM", "RL"
        Long pidConfigId,     // конфигурация PID
        Long modelId,         // может быть null для PID
        Long iterationsRequested,
        Long timestep
) {
}
