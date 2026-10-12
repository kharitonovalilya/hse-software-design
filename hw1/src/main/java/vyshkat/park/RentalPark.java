package vyshkat.park;

import org.springframework.stereotype.Component;
import vyshkat.interfaces.EnergyConsumer;
import vyshkat.interfaces.InventoryItem;
import vyshkat.transport.Transport;

import java.util.ArrayList;
import java.util.List;

@Component
public class RentalPark {
    private final List<InventoryItem> inventoryItems = new ArrayList<>();

    public List<InventoryItem> getAllInventoryItems() {
        return List.copyOf(inventoryItems);
    }

    public List<Transport> getTransportItems() {
        return inventoryItems.stream()
                .filter(item -> item instanceof Transport)
                .map(item -> (Transport) item)
                .toList();
    }

    public List<EnergyConsumer> getEnergyConsumerItems() {
        return inventoryItems.stream()
                .filter(item -> item instanceof EnergyConsumer)
                .map(item -> (EnergyConsumer) item)
                .toList();
    }

    public void addItem(InventoryItem item) {
        inventoryItems.add(item);
    }

}
