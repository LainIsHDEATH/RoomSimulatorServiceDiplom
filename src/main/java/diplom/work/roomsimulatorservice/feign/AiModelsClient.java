package diplom.work.roomsimulatorservice.feign;

import diplom.work.roomsimulatorservice.dto.LstmModelDTO.LstmModelDtoRequest;
import diplom.work.roomsimulatorservice.dto.LstmModelDTO.LstmModelDtoResponse;
import diplom.work.roomsimulatorservice.dto.RlModelDTO.RlModelDtoRequest;
import diplom.work.roomsimulatorservice.dto.RlModelDTO.RlModelDtoResponse;
import diplom.work.roomsimulatorservice.dto.autotunePidDTO.AutotunePidDtoRequest;
import diplom.work.roomsimulatorservice.dto.autotunePidDTO.AutotunePidDtoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "AI-models", url = "${feign.client.config.AI-models.url}")
public interface AiModelsClient {

    @GetMapping("/api/predict")
    LstmModelDtoResponse predictTemperatureLSTM(@RequestBody LstmModelDtoRequest request);

    @GetMapping("/api/compute")
    RlModelDtoResponse computeHeatRL(@RequestBody RlModelDtoRequest request);
}
