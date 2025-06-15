package diplom.work.roomsimulatorservice.service.calculations;

import diplom.work.roomsimulatorservice.domain.room.RoomParams;
import diplom.work.roomsimulatorservice.domain.room.RoomState;
import diplom.work.roomsimulatorservice.domain.room.SurfaceParams;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class PhysicsCalculation {

    public Map<String, Double> computeNewSurfacesTemps(RoomParams roomParams, RoomState roomState, double outsideTemperature, double dt) {
        Map<String, Double> newSurfaceTemps = new HashMap<>();
        double roomTemp = roomState.getAirTemperature();

        for (SurfaceParams surface : roomParams.getSurfaces()) {
            String surfaceName = surface.getName();
            double surfaceTemp = roomState.getSurfaceTemperature(surfaceName);

            double adjacentTemp;

            if (surface.getAdjacentRoomName() == null) {
                adjacentTemp = outsideTemperature;
            } else {
                adjacentTemp = 20.0;
            }

            double dT_surface = computeSurfaceTemperatureChange(surface, roomTemp, surfaceTemp, adjacentTemp);
            surfaceTemp += dT_surface * dt;

            newSurfaceTemps.put(surfaceName, surfaceTemp);
        }
        return newSurfaceTemps;
    }

    private double computeSurfaceTemperatureChange(SurfaceParams s,
                                                   double airT, double wallT,
                                                   double outT) {

        double hInt = s.getUInternal();
        double uTotal = calcU(s);

        double area = s.getArea();
        double qIn  = hInt  * area * (airT  - wallT);
        double qOut = uTotal * area * (wallT - outT);

        return (qIn - qOut) / s.getHeatCapacity();
    }

    public double computeSurfaceHeatFlow(RoomParams roomParams, RoomState roomState) {

        double qTotal = 0;
        double airT = roomState.getAirTemperature();

        for (SurfaceParams s : roomParams.getSurfaces()) {
            String name = s.getName();
            double surfaceTemp = roomState.getSurfaceTemperature(name);

            double qConvInt = s.getUInternal() * s.getArea() * (airT - surfaceTemp);
            qTotal -= qConvInt;
        }
        return qTotal;   // Вт
    }

    private double calcU(SurfaceParams s) {
        double hInt = s.getUInternal();          // 8  Вт/(м²·К)
        double hExt = s.getUExternal();          // 25 Вт/(м²·К)
        double rCond = s.getThickness() / s.getThermalConductivity(); // L/λ
        double rTotal = (1.0 / hInt) + rCond + (1.0 / hExt);
        return 1.0 / rTotal;                     // Вт/(м²·К)
    }
}
