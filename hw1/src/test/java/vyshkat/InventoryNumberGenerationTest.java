package vyshkat;

import org.junit.jupiter.api.Test;
import vyshkat.equipment.Helmet;
import vyshkat.transport.Bike;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InventoryNumberGenerationTest {

    @Test
    void shouldAssignSequentialInventoryNumbersToDifferentItemTypes() {
        Bike bike = new Bike("Bike", 7, true, 80);
        Helmet helmet = new Helmet("Helmet");

        assertEquals(
                bike.getInventoryNumber() + 1,
                helmet.getInventoryNumber()
        );
    }
}
