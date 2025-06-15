package diplom.work.roomsimulatorservice.model;

import diplom.work.roomsimulatorservice.domain.SimulationContext;

public interface ThermalModel {
    void applyStep(SimulationContext simulationContext, double heaterPower);
}
