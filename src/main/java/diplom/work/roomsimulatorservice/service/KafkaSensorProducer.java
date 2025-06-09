package diplom.work.roomsimulatorservice.service;

import diplom.work.roomsimulatorservice.dto.storage.storage_sensor_client.SensorDataDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaSensorProducer {
    private final KafkaTemplate<String, SensorDataDTO> kafkaTemplate;

    public void sendSensorData(SensorDataDTO data) {
        kafkaTemplate.send("simulation-data-topic", data.simulationId().toString(), data);
    }
}
