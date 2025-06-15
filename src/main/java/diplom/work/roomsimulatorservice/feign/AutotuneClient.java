package diplom.work.roomsimulatorservice.feign;

import diplom.work.roomsimulatorservice.dto.autotunePidDTO.AutotunePidDtoRequest;
import diplom.work.roomsimulatorservice.dto.autotunePidDTO.AutotunePidDtoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "pid-autotuner", url = "${feign.client.config.pid-autotuner.url}")
public interface AutotuneClient {

    @PostMapping("/api/power/{simulationId}")
    AutotunePidDtoResponse compute(@PathVariable Long simulationId,
                                   @RequestBody AutotunePidDtoRequest request);
}
