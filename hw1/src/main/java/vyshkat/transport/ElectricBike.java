package vyshkat.transport;

import vyshkat.interfaces.EnergyConsumer;

public class ElectricBike extends Transport implements EnergyConsumer {

    private final double dailyEnergyConsumption;
    private final int batteryChargePercentage;

    public ElectricBike(String name, int friendlyForBeginner, boolean brakesWorking, int tyreCondition, double dailyEnergyConsumption, int batteryChargePercentage) {
        super(name, friendlyForBeginner, brakesWorking, tyreCondition);
        this.batteryChargePercentage = batteryChargePercentage;
        if (dailyEnergyConsumption < 0) {
            throw new IllegalArgumentException("dailyEnergyConsumption must be non-negative");
        }
        if (batteryChargePercentage < 0 || batteryChargePercentage > 100) {
            throw new IllegalArgumentException("batteryChargePercentage must be between 0 and 100");
        }
        this.dailyEnergyConsumption = dailyEnergyConsumption;
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
