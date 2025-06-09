package diplom.work.roomsimulatorservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "lstm-model", url = "${feign.client.config.lstm-model.url}")
public interface LSTMClient {

    @GetMapping("/predict-temperature")
    double predictTemperature();
    //double predict(@RequestBody SensorDTO sensor);

    // Входные параметры: external_temp, heater_power, people_count, door_open, window_open
//    record SensorDTO(
//            double room_temperature,
//            double external_temperature,
//            double heater_power,
//            double target_temperature
//    ) {}
}
