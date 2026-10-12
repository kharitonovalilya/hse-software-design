package vyshkat.app;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import vyshkat.equipment.Helmet;
import vyshkat.park.DefaultServiceCenter;
import vyshkat.park.RentalPark;
import vyshkat.park.RentalParkService;
import vyshkat.transport.Bike;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class ConsoleApplicationTest {

    private final InputStream originalIn = System.in;
    private final PrintStream originalOut = System.out;

    @AfterEach
    void restoreStreams() {
        System.setIn(originalIn);
        System.setOut(originalOut);
    }

    @Test
    void shouldAddBikeFromConsole() {
        RentalPark rentalPark = new RentalPark();
        RentalParkService service = new RentalParkService(
                rentalPark,
                new DefaultServiceCenter()
        );

        setInput("""
                н
                1
                2
                Велосипед
                7
                д
                80
                0
                """);

        ConsoleApplication app = new ConsoleApplication(service);

        app.run();

        assertEquals(1, rentalPark.getAllInventoryItems().size());

        var item = rentalPark.getAllInventoryItems().getFirst();

        assertInstanceOf(Bike.class, item);
        assertEquals("Велосипед", item.getName());
    }

    @Test
    void shouldAddHelmetFromConsole() {
        RentalPark rentalPark = new RentalPark();
        RentalParkService service = new RentalParkService(
                rentalPark,
                new DefaultServiceCenter()
        );

        setInput("""
                н
                2
                1
                Шлем
                0
                """);

        ConsoleApplication app = new ConsoleApplication(service);

        app.run();

        assertEquals(1, rentalPark.getAllInventoryItems().size());
        assertInstanceOf(
                Helmet.class,
                rentalPark.getAllInventoryItems().getFirst()
        );
    }

    @Test
    void shouldPrintBasicReports() {
        RentalPark rentalPark = new RentalPark();
        RentalParkService service = new RentalParkService(
                rentalPark,
                new DefaultServiceCenter()
        );

        service.addEquipment(new Helmet("Шлем"));

        service.addTransport(
                new Bike(
                        "Велосипед",
                        7,
                        true,
                        80
                )
        );

        setInput("""
                н
                3
                4
                5
                6
                0
                """);

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        ConsoleApplication app = new ConsoleApplication(service);

        app.run();

        String result = output.toString();

        assertTrue(result.contains("=== Весь инвентарь ==="));
        assertTrue(result.contains("Всего объектов на балансе: 2"));
        assertTrue(result.contains("Суммарное суточное энергопотребление:"));
        assertTrue(result.contains("=== Транспорт для новичков ==="));
    }

    private void setInput(String input) {
        System.setIn(
                new ByteArrayInputStream(
                        input.getBytes(StandardCharsets.UTF_8)
                )
        );
    }
}