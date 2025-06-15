package diplom.work.roomsimulatorservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("simulation.pid-lstm")
public record StrategyProperties(int predictionInterval, double correctionGain) {}
