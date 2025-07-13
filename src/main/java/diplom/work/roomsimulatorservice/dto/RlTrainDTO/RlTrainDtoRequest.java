package diplom.work.roomsimulatorservice.dto.RlTrainDTO;

public record RlTrainDtoRequest(
        Long simulationId,
        double roomTemp,
        double outdoorTemp,
        double setpointTemp
) {
}
