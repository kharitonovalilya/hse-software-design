package vyshkat.config;

public final class InventoryNumberGenerator {

    private static int nextNumber = 1;

    private InventoryNumberGenerator() {
    }

    public static int nextNumber() {
        return nextNumber++;
    }
}