package diplom.work.roomsimulatorservice.dto.LstmModelDTO;

public record LstmSensorDTO(
        Double tempIn,
        Double tempOut,
        Double tempSetpoint,
        Double heaterPower
) {
}
