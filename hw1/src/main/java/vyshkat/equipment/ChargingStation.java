package vyshkat.equipment;

import vyshkat.interfaces.EnergyConsumer;

public class ChargingStation extends Equipment implements EnergyConsumer {
    private final double dailyEnergyConsumption;
    private final int batteryChargePercentage;

    public ChargingStation(String name, double dailyEnergyConsumption, int batteryChargePercentage) {
        super(name);
        if (batteryChargePercentage < 0 || batteryChargePercentage > 100) {
            throw new IllegalArgumentException("batteryChargePercentage must be between 0 and 100");
        }
        if (dailyEnergyConsumption < 0) {
            throw new IllegalArgumentException("dailyEnergyConsumption must be non-negative");
        }
        this.dailyEnergyConsumption = dailyEnergyConsumption;
        this.batteryChargePercentage = batteryChargePercentage;
    }

    @Override
    public double getDailyEnergyConsumption() {
        return dailyEnergyConsumption;
    }

    @Override
    public int getBatteryChargePercentage() {
        return batteryChargePercentage;
    }
}
