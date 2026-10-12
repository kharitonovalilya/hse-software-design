package vyshkat.transport;

import vyshkat.config.InventoryNumberGenerator;
import vyshkat.interfaces.InventoryItem;

public abstract class Transport implements InventoryItem {
    private final int inventoryNumber;
    private final String name;
    private final int friendlyForBeginner;
    private final boolean brakesWorking;
    private final int tyreCondition;

    protected Transport(String name, int friendlyForBeginner, boolean brakesWorking, int tyreCondition) {
        this.brakesWorking = brakesWorking;
        this.tyreCondition = tyreCondition;
        this.inventoryNumber = InventoryNumberGenerator.nextNumber();
        this.name = name;
        if (friendlyForBeginner <= 0 || friendlyForBeginner > 10) {
            throw new IllegalArgumentException("friendlyForBeginner must be between 1 and 10");
        }
        if (tyreCondition < 0 || tyreCondition > 100) {
            throw new IllegalArgumentException("tyreCondition must be between 0 and 100");
        }
        this.friendlyForBeginner = friendlyForBeginner;
    }

    public boolean isSuitableForBeginner() {
        return this.friendlyForBeginner >= 6;
    }

    public boolean isBrakesWorking() {
        return this.brakesWorking;
    }

    public int getTyreCondition() {
        return tyreCondition;
    }

    @Override
    public int getInventoryNumber() {
        return inventoryNumber;
    }

    @Override
    public String getName() {
        return name;
    }
}
