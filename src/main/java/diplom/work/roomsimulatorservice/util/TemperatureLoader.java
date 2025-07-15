package diplom.work.roomsimulatorservice.util;

import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class TemperatureLoader {

    public static List<Double> readTempsFromResource(String filename) throws IOException {
        List<Double> temps = new ArrayList<>();
        ClassPathResource resource = new ClassPathResource(filename);
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) { firstLine = false; continue; }
                String[] parts = line.split(",");
                if (parts.length > 3) {
                    temps.add(Double.parseDouble(parts[3].trim()));
                }
            }
        }
        return temps;
    }

    public static Queue<Double> upsampleToOneMinute(List<Double> data60) {
        Queue<Double> result = new LinkedList<>();
        if (data60 == null || data60.isEmpty()) {
            return result;
        }

        result.add(data60.getFirst());
        for (int idx = 0; idx < data60.size() - 1; idx++) {
            double startVal = data60.get(idx);
            double endVal = data60.get(idx + 1);

            for (int i = 1; i < 60; i++) {
                double interpolated = startVal + (endVal - startVal) * (i / 60.0);
                BigDecimal bd = BigDecimal.valueOf(interpolated)
                        .setScale(2, RoundingMode.HALF_UP);
                result.add(bd.doubleValue());
            }
            result.add(endVal);
        }
        return result;
    }
}
