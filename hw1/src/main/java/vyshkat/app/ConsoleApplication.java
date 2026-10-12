package vyshkat.app;

import org.springframework.stereotype.Component;
import vyshkat.equipment.ChargingStation;
import vyshkat.equipment.Equipment;
import vyshkat.equipment.Helmet;
import vyshkat.interfaces.InventoryItem;
import vyshkat.park.RentalParkService;
import vyshkat.transport.Bike;
import vyshkat.transport.ElectricBike;
import vyshkat.transport.Scooter;
import vyshkat.transport.Transport;

import java.util.List;
import java.util.Scanner;

@Component
public class ConsoleApplication {

    private final RentalParkService rentalParkService;
    private final Scanner scanner = new Scanner(System.in);

    public ConsoleApplication(RentalParkService rentalParkService) {
        this.rentalParkService = rentalParkService;
    }

    public void run() {
        if (readYesNo("Запустить демонстрацию перед началом работы? (д/н): ")) {
            runDemo();
        }
        runMenu();
    }

    private void runMenu() {
        while (true) {
            printMenu();
            int command = readInt("Выберите команду: ");

            switch (command) {
                case 1 -> addTransport();
                case 2 -> addEquipment();
                case 3 -> printAllInventory();
                case 4 -> printInventoryCount();
                case 5 -> printTotalEnergyConsumption();
                case 6 -> printBeginnerFriendlyTransport();
                case 0 -> {
                    System.out.println("До свидания!");
                    return;
                }
                default -> System.out.println("Неизвестная команда.");
            }
        }
    }

    private void printMenu() {
        System.out.println();
        System.out.println("=== ВышКат ===");
        System.out.println("1. Добавить транспорт");
        System.out.println("2. Добавить оборудование");
        System.out.println("3. Показать весь инвентарь");
        System.out.println("4. Показать количество объектов");
        System.out.println("5. Показать суммарное суточное энергопотребление");
        System.out.println("6. Показать транспорт для новичков");
        System.out.println("0. Выход");
    }

    private void addTransport() {
        System.out.println();
        System.out.println("Выберите тип транспорта:");
        System.out.println("1. Самокат");
        System.out.println("2. Велосипед");
        System.out.println("3. Электровелосипед");

        int type = readInt("Тип: ");
        if (type < 1 || type > 3) {
            System.out.println("Неизвестный тип транспорта.");
            return;
        }

        String name = readString("Название: ");
        int friendliness = readIntInRange(
                "Удобство для новичка (1-10): ", 1, 10
        );
        boolean brakesWorking = readYesNo("Тормоза исправны? (д/н): ");
        int tyreCondition = readIntInRange(
                "Состояние шин (0-100): ", 0, 100
        );

        Transport transport;

        if (type == 1 || type == 3) {
            double energy = readNonNegativeDouble(
                    "Суточное энергопотребление (кВт·ч): "
            );
            int batteryCharge = readIntInRange(
                    "Заряд батареи (0-100): ", 0, 100
            );

            if (type == 1) {
                transport = new Scooter(
                        name,
                        friendliness,
                        brakesWorking,
                        tyreCondition,
                        energy,
                        batteryCharge
                );
            } else {
                transport = new ElectricBike(
                        name,
                        friendliness,
                        brakesWorking,
                        tyreCondition,
                        energy,
                        batteryCharge
                );
            }
        } else {
            transport = new Bike(
                    name,
                    friendliness,
                    brakesWorking,
                    tyreCondition
            );
        }

        System.out.println(
                "Присвоен инвентарный номер: " + transport.getInventoryNumber()
        );

        boolean accepted = rentalParkService.addTransport(transport);
        System.out.println(
                "Результат техосмотра: "
                        + (accepted ? "принят." : "отклонён.")
        );
    }

    private void addEquipment() {
        System.out.println();
        System.out.println("Выберите тип оборудования:");
        System.out.println("1. Шлем");
        System.out.println("2. Зарядная станция");

        int type = readInt("Тип: ");
        if (type < 1 || type > 2) {
            System.out.println("Неизвестный тип оборудования.");
            return;
        }

        String name = readString("Название: ");
        Equipment equipment;

        if (type == 1) {
            equipment = new Helmet(name);
        } else {
            double energy = readNonNegativeDouble(
                    "Суточное энергопотребление (кВт·ч): "
            );
            int batteryCharge = readIntInRange(
                    "Заряд батареи (0-100): ", 0, 100
            );
            equipment = new ChargingStation(name, energy, batteryCharge);
        }

        rentalParkService.addEquipment(equipment);

        System.out.println(
                "Оборудование добавлено. Инвентарный номер: "
                        + equipment.getInventoryNumber()
        );
    }

    private void printAllInventory() {
        List<InventoryItem> items = rentalParkService.getInventoryItems();

        if (items.isEmpty()) {
            System.out.println("Парк пуст.");
            return;
        }

        System.out.println();
        System.out.println("=== Весь инвентарь ===");
        for (InventoryItem item : items) {
            System.out.println(
                    item.getName()
                            + " | инвентарный номер: "
                            + item.getInventoryNumber()
            );
        }
    }

    private void printInventoryCount() {
        System.out.println(
                "Всего объектов на балансе: " + rentalParkService.getInventoryCount()
        );
    }

    private void printTotalEnergyConsumption() {
        System.out.println(
                "Суммарное суточное энергопотребление: "
                        + rentalParkService.calculateTotalDailyEnergy()
                        + " kWh"
        );
    }

    private void printBeginnerFriendlyTransport() {
        List<Transport> transport = rentalParkService.getBeginnerFriendlyTransport();

        if (transport.isEmpty()) {
            System.out.println("Нет транспорта, подходящего для новичков.");
            return;
        }

        System.out.println();
        System.out.println("=== Транспорт для новичков ===");
        for (Transport item : transport) {
            System.out.println(
                    item.getName()
                            + " | инвентарный номер: "
                            + item.getInventoryNumber()
            );
        }
    }

    private void runDemo() {
        System.out.println();
        System.out.println("=== Демонстрация ===");

        Helmet helmet = new Helmet("Шлем");
        ChargingStation station = new ChargingStation(
                "Зарядная станция", 4.5, 80
        );
        rentalParkService.addEquipment(helmet);
        rentalParkService.addEquipment(station);

        Scooter scooter = new Scooter(
                "Самокат", 8, true, 85, 2.5, 75
        );
        Bike bike = new Bike(
                "Велосипед", 5, false, 90
        );
        ElectricBike electricBike = new ElectricBike(
                "Электровелосипед", 7, true, 70, 3.0, 50
        );

        printInspectionResult(scooter, rentalParkService.addTransport(scooter));
        printInspectionResult(bike, rentalParkService.addTransport(bike));
        printInspectionResult(
                electricBike,
                rentalParkService.addTransport(electricBike)
        );

        printAllInventory();
        printInventoryCount();
        printTotalEnergyConsumption();
        printBeginnerFriendlyTransport();

        System.out.println("=== Демонстрация завершена ===");
    }

    private void printInspectionResult(Transport transport, boolean accepted) {
        System.out.println(
                transport.getName()
                        + " #"
                        + transport.getInventoryNumber()
                        + ": "
                        + (accepted ? "принят" : "отклонён")
        );
    }

    private String readString(String message) {
        System.out.print(message);
        return scanner.nextLine().trim();
    }

    private int readInt(String message) {
        while (true) {
            System.out.print(message);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException exception) {
                System.out.println("Введите целое число.");
            }
        }
    }

    private int readIntInRange(String message, int min, int max) {
        while (true) {
            int value = readInt(message);
            if (value >= min && value <= max) {
                return value;
            }
            System.out.println(
                    "Значение должно быть от " + min + " до " + max + "."
            );
        }
    }

    private double readDouble(String message) {
        while (true) {
            System.out.print(message);
            try {
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException exception) {
                System.out.println("Введите число.");
            }
        }
    }

    private double readNonNegativeDouble(String message) {
        while (true) {
            double value = readDouble(message);
            if (value >= 0) {
                return value;
            }
            System.out.println("Значение не может быть отрицательным.");
        }
    }

    private boolean readYesNo(String message) {
        while (true) {
            String value = readString(message);

            if (value.equalsIgnoreCase("д")
                    || value.equalsIgnoreCase("да")
                    || value.equalsIgnoreCase("y")
                    || value.equalsIgnoreCase("yes")) {
                return true;
            }

            if (value.equalsIgnoreCase("н")
                    || value.equalsIgnoreCase("нет")
                    || value.equalsIgnoreCase("n")
                    || value.equalsIgnoreCase("no")) {
                return false;
            }

            System.out.println("Введите д/н или да/нет.");
        }
    }
}
