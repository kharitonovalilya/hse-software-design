package vyshkat.park;

import org.springframework.stereotype.Service;
import vyshkat.equipment.Equipment;
import vyshkat.interfaces.EnergyConsumer;
import vyshkat.interfaces.InventoryItem;
import vyshkat.interfaces.ServiceCenter;
import vyshkat.transport.Transport;

import java.util.List;

@Service
public class RentalParkService {
    private final RentalPark rentalPark;
    private final ServiceCenter serviceCenter;

    public RentalParkService(RentalPark rentalPark, ServiceCenter serviceCenter) {
        this.rentalPark = rentalPark;
        this.serviceCenter = serviceCenter;
    }

    public boolean addTransport(Transport transport) {
        if (serviceCenter.inspect(transport)) {
            rentalPark.addItem(transport);
            return true;
        }
        return false;
    }

    public void addEquipment(Equipment equipment) {
        rentalPark.addItem(equipment);
    }

    public List<Transport> getBeginnerFriendlyTransport() {
        List<Transport> transports = rentalPark.getTransportItems();
        return transports.stream()
                .filter(Transport::isSuitableForBeginner)
                .toList();
    }

    public double calculateTotalDailyEnergy() {
        double result = 0;
        List<EnergyConsumer> energyConsumers = rentalPark.getEnergyConsumerItems();
        for (EnergyConsumer energyConsumer : energyConsumers) {
            result += energyConsumer.getDailyEnergyConsumption();
        }
        return result;
    }

    public List<InventoryItem> getInventoryItems() {
        return rentalPark.getAllInventoryItems();
    }

    public int getInventoryCount() {
        return rentalPark.getAllInventoryItems().size();
    }
}
