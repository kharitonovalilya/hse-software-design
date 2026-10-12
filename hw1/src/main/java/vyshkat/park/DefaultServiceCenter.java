package vyshkat.park;

import org.springframework.stereotype.Component;
import vyshkat.interfaces.EnergyConsumer;
import vyshkat.interfaces.ServiceCenter;
import vyshkat.transport.Transport;

@Component
public class DefaultServiceCenter implements ServiceCenter {

    private static final int MIN_TYRE_CONDITION = 60;
    private static final int MIN_BATTERY_CHARGE = 20;

    @Override
    public boolean inspect(Transport transport) {
        if (!transport.isBrakesWorking()) {
            return false;
        }
        if (transport.getTyreCondition() < MIN_TYRE_CONDITION) {
            return false;
        }
        if (transport instanceof EnergyConsumer energyConsumer) {
            return energyConsumer.getBatteryChargePercentage() >= MIN_BATTERY_CHARGE;
        }
        return true;
    }
}
