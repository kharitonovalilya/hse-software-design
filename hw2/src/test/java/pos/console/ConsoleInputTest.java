package pos.console;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class ConsoleInputTest {
    @Test
    void retriesInvalidNumbersAndConfirmationWithoutLosingFollowingInput() {
        ConsoleInput input = new ConsoleInput(new Scanner("слово\n-1\n7\n0\n8\nможет быть\nнет\n"));

        String output = captureOutput(() -> {
            assertEquals(7, input.readId("ID: "));
            assertEquals(8, input.readPositiveInt("Вес: "));
            assertFalse(input.confirm("Удалить?"));
        });

        assertTrue(output.contains("Введите положительный ID."));
        assertTrue(output.contains("Допустимый диапазон:"));
        assertTrue(output.contains("Ответьте да или нет."));
    }

    @Test
    void eofIsReportedByStandardScannerException() {
        ConsoleInput input = new ConsoleInput(new Scanner(""));

        captureOutput(() -> assertThrows(NoSuchElementException.class,
                () -> input.readLine("Имя: ")));
    }

    private String captureOutput(Runnable action) {
        PrintStream previous = System.out;
        var output = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
            action.run();
        } finally {
            System.setOut(previous);
        }
        return output.toString(StandardCharsets.UTF_8);
    }
}
