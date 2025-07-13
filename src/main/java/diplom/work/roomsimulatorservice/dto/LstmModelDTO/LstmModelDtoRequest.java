package diplom.work.roomsimulatorservice.dto.LstmModelDTO;

import java.util.List;

public record LstmModelDtoRequest (
        Long modelId,
        List<LstmSensorDTO> sensorDataDTOList
) {
}
