package vyshkat.park;

import org.junit.jupiter.api.Test;
import vyshkat.equipment.ChargingStation;
import vyshkat.equipment.Helmet;
import vyshkat.transport.Bike;
import vyshkat.transport.Scooter;

import static org.junit.jupiter.api.Assertions.*;

class RentalParkTest {

    @Test
    void shouldReturnOnlyTransportItems() {
        RentalPark park = new RentalPark();
        Bike bike = new Bike("Bike", 7, true, 80);
        Helmet helmet = new Helmet("Helmet");
        park.addItem(bike);
        park.addItem(helmet);

        var result = park.getTransportItems();

        assertEquals(1, result.size());
        assertTrue(result.contains(bike));
        assertFalse(result.contains(helmet));
    }

    @Test
    void shouldReturnEnergyConsumersFromDifferentHierarchies() {
        RentalPark park = new RentalPark();
        Scooter scooter = new Scooter(
                "Scooter", 7, true, 80, 2.0, 70
        );
        ChargingStation station = new ChargingStation(
                "Charging station", 4.0, 80
        );
        Bike bike = new Bike("Bike", 7, true, 80);
        Helmet helmet = new Helmet("Helmet");
        park.addItem(scooter);
        park.addItem(station);
        park.addItem(bike);
        park.addItem(helmet);

        var result = park.getEnergyConsumerItems();

        assertEquals(2, result.size());
        assertTrue(result.contains(scooter));
        assertTrue(result.contains(station));
        assertFalse(result.contains(bike));
        assertFalse(result.contains(helmet));
    }
}
