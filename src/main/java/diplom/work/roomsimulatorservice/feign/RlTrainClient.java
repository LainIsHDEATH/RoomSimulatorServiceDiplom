package diplom.work.roomsimulatorservice.feign;

import diplom.work.roomsimulatorservice.dto.RlTrainDTO.RlTrainDtoRequest;
import diplom.work.roomsimulatorservice.dto.RlTrainDTO.RlTrainDtoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "rl-trainer", url = "${feign.client.config.RL-trainer.url}")
public interface RlTrainClient {

    @PostMapping("/compute")
    RlTrainDtoResponse compute(@RequestBody RlTrainDtoRequest request);
}
