package diplom.work.roomsimulatorservice.feign;

import diplom.work.roomsimulatorservice.dto.PidDTO.PIDRequest;
import diplom.work.roomsimulatorservice.dto.PidDTO.PIDResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "pid-controller", url = "${feign.client.config.pid-controller.url}")
public interface PIDClient {

    @PostMapping("/api/pid/compute")
    PIDResponse compute(@RequestBody PIDRequest request);
}
