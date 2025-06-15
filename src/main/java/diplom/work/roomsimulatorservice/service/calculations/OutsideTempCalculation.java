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
        Path path;
        Path path2;
        Path path3;

        List<Double> allTemps = new ArrayList<>();
        try {
            if (year == 2023) {
                path = Path.of("src", "main", "resources", "response3.csv");
                allTemps.addAll(TemperatureLoader.readTempsFromCsv(path));
            } else {
                path = Path.of("src", "main", "resources", "response.csv");
                path2 = Path.of("src", "main", "resources", "response2.csv");
                path3 = Path.of("src", "main", "resources", "response4.csv");
                allTemps.addAll(TemperatureLoader.readTempsFromCsv(path));
                allTemps.addAll(TemperatureLoader.readTempsFromCsv(path2));
                allTemps.addAll(TemperatureLoader.readTempsFromCsv(path3));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return TemperatureLoader.upsampleToOneMinute(allTemps);
    }
}
