package vyshkat.park;

import org.junit.jupiter.api.Test;
import vyshkat.transport.Bike;
import vyshkat.transport.Scooter;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DefaultServiceCenterTest {

    private final DefaultServiceCenter serviceCenter = new DefaultServiceCenter();

    @Test
    void shouldAcceptTransportAtMinimumAllowedTyreCondition() {
        Bike bike = new Bike("Bike", 7, true, 60);

        assertTrue(serviceCenter.inspect(bike));
    }

    @Test
    void shouldRejectTransportWithBrokenBrakes() {
        Bike bike = new Bike("Bike", 7, false, 100);

        assertFalse(serviceCenter.inspect(bike));
    }

    @Test
    void shouldRejectTransportWithWornTyres() {
        Bike bike = new Bike("Bike", 7, true, 59);

        assertFalse(serviceCenter.inspect(bike));
    }

    @Test
    void shouldRejectEnergyConsumerWithLowBatteryCharge() {
        Scooter scooter = new Scooter(
                "Scooter", 7, true, 80, 2.0, 19
        );

        assertFalse(serviceCenter.inspect(scooter));
    }

    @Test
    void shouldAcceptEnergyConsumerAtMinimumBatteryCharge() {
        Scooter scooter = new Scooter(
                "Scooter", 7, true, 80, 2.0, 20
        );

        assertTrue(serviceCenter.inspect(scooter));
    }
}
