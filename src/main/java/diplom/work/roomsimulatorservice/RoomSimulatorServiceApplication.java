package diplom.work.roomsimulatorservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableFeignClients(basePackages = "diplom.work.roomsimulatorservice.feign") // путь к интерфейсу PIDClient
@EnableAsync
public class RoomSimulatorServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RoomSimulatorServiceApplication.class, args);
    }

}
