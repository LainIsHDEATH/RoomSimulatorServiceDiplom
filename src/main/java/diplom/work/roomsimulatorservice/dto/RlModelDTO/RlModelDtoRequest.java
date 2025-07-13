package diplom.work.roomsimulatorservice.dto.RlModelDTO;

public record RlModelDtoRequest (
        Long modelId,
        double roomTemp,
        double outdoorTemp
){
}
