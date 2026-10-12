package vyshkat.park;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import vyshkat.equipment.ChargingStation;
import vyshkat.equipment.Helmet;
import vyshkat.transport.Bike;
import vyshkat.transport.ElectricBike;
import vyshkat.transport.Scooter;

import static org.junit.jupiter.api.Assertions.*;

class RentalParkServiceTest {

    private RentalPark rentalPark;
    private FakeServiceCenter serviceCenter;
    private RentalParkService rentalParkService;

    @BeforeEach
    void setUp() {
        rentalPark = new RentalPark();
        serviceCenter = new FakeServiceCenter();
        rentalParkService = new RentalParkService(rentalPark, serviceCenter);
    }

    @Test
    void shouldAddTransportWhenServiceCenterAcceptsIt() {
        serviceCenter.setInspectionResult(true);

        Bike bike = new Bike(
                "Bike",
                7,
                false,
                10
        );

        boolean result =
                rentalParkService.addTransport(bike);

        assertTrue(result);
        assertTrue(
                rentalPark.getAllInventoryItems().contains(bike)
        );
    }

    @Test
    void shouldNotAddTransportWhenServiceCenterRejectsIt() {
        serviceCenter.setInspectionResult(false);

        Bike bike = new Bike(
                "Bike",
                7,
                true,
                100
        );

        boolean result =
                rentalParkService.addTransport(bike);

        assertFalse(result);
        assertFalse(
                rentalPark.getAllInventoryItems().contains(bike)
        );
    }

    @Test
    void shouldCalculateTotalDailyEnergyConsumption() {
        serviceCenter.setInspectionResult(true);

        Scooter scooter = new Scooter(
                "Scooter",
                8,
                true,
                80,
                2.5,
                70
        );

        ElectricBike electricBike = new ElectricBike(
                "Electric bike",
                7,
                true,
                80,
                3.0,
                70
        );

        Bike bike = new Bike(
                "Bike",
                7,
                true,
                80
        );

        ChargingStation station = new ChargingStation(
                "Charging station",
                5.5,
                90
        );

        rentalParkService.addTransport(scooter);
        rentalParkService.addTransport(electricBike);
        rentalParkService.addTransport(bike);
        rentalParkService.addEquipment(station);

        assertEquals(
                11.0,
                rentalParkService.calculateTotalDailyEnergy(),
                0.0001
        );
    }

    @Test
    void shouldReturnOnlyBeginnerFriendlyTransport() {
        serviceCenter.setInspectionResult(true);

        Scooter suitable = new Scooter(
                "Scooter",
                6,
                true,
                80,
                2.0,
                70
        );

        Bike notSuitable = new Bike(
                "Bike",
                5,
                true,
                80
        );

        Helmet helmet = new Helmet("Helmet");

        rentalParkService.addTransport(suitable);
        rentalParkService.addTransport(notSuitable);
        rentalParkService.addEquipment(helmet);

        var result =
                rentalParkService.getBeginnerFriendlyTransport();

        assertEquals(1, result.size());
        assertTrue(result.contains(suitable));
        assertFalse(result.contains(notSuitable));
    }

    @Test
    void shouldReturnInventoryItems() {
        serviceCenter.setInspectionResult(true);

        Helmet helmet = new Helmet("Helmet");

        Bike bike = new Bike(
                "Bike",
                7,
                true,
                80
        );

        rentalParkService.addEquipment(helmet);
        rentalParkService.addTransport(bike);

        var items =
                rentalParkService.getInventoryItems();

        assertEquals(2, items.size());
        assertTrue(items.contains(helmet));
        assertTrue(items.contains(bike));
    }

    @Test
    void shouldReturnCorrectInventoryItemsCount() {
        serviceCenter.setInspectionResult(true);

        Helmet helmet = new Helmet("Helmet");

        Bike bike = new Bike(
                "Bike",
                7,
                true,
                80
        );

        rentalParkService.addEquipment(helmet);
        rentalParkService.addTransport(bike);

        assertEquals(
                2,
                rentalParkService.getInventoryCount()
        );
    }
}