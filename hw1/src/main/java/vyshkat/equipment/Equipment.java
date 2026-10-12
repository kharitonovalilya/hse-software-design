package vyshkat.equipment;

import vyshkat.config.InventoryNumberGenerator;
import vyshkat.interfaces.InventoryItem;

public abstract class Equipment implements InventoryItem {
    private final int inventoryNumber;
    private final String name;

    protected Equipment(String name) {
        this.inventoryNumber = InventoryNumberGenerator.nextNumber();
        this.name = name;
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
