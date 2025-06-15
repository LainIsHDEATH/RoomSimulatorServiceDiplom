package diplom.work.roomsimulatorservice.dto.LstmModelDTO;

import java.util.List;

public record LstmModelDtoRequest (
        Integer modelId,
        List<LstmSensorDTO> sensorDataDTOList
) {
}
