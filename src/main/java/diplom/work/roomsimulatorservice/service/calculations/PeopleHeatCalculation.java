package diplom.work.roomsimulatorservice.service.calculations;

import org.springframework.stereotype.Component;

@Component
public class PeopleHeatCalculation {

    public double computePeopleHeat(int peopleCount) {
        return peopleCount * 100.0;
    }
}
