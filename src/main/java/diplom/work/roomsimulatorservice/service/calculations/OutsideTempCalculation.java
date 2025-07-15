package diplom.work.roomsimulatorservice.service.calculations;

import diplom.work.roomsimulatorservice.util.TemperatureLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

@Component
public class OutsideTempCalculation {

    public Queue<Double> computeOutsideTemperature(Integer year) {
        List<Double> allTemps = new ArrayList<>();
        try {
            if (year == 2023) {
                allTemps.addAll(TemperatureLoader.readTempsFromResource("response3.csv"));
                allTemps.addAll(TemperatureLoader.readTempsFromResource("response5.csv"));
            } else {
                allTemps.addAll(TemperatureLoader.readTempsFromResource("response.csv"));
                allTemps.addAll(TemperatureLoader.readTempsFromResource("response2.csv"));
                allTemps.addAll(TemperatureLoader.readTempsFromResource("response4.csv"));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return TemperatureLoader.upsampleToOneMinute(allTemps);
    }
}
