package diplom.work.roomsimulatorservice.dto;

public record SimulationStatusDTO(long simulationId,
                                  String status,     // RUNNING | FINISHED | FAILED …
                                  double progress) {
}
