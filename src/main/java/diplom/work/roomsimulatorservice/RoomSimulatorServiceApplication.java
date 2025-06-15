package diplom.work.roomsimulatorservice;

import diplom.work.roomsimulatorservice.config.StrategyProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableFeignClients(basePackages = "diplom.work.roomsimulatorservice.feign") // путь к интерфейсу PIDClient
@EnableAsync
@EnableConfigurationProperties(StrategyProperties.class)
@EnableScheduling
public class RoomSimulatorServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RoomSimulatorServiceApplication.class, args);
    }

}
