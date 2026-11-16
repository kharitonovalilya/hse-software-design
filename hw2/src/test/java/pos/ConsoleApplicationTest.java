package pos;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import pos.application.facade.PosFacade;
import pos.console.ConsoleApplication;
import pos.infrastructure.config.ApplicationConfig;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class ConsoleApplicationTest {
    @Test
    void businessErrorKeepsUserInSectionAndRejectedDeletionKeepsStudent() {
        try (var context = new AnnotationConfigApplicationContext(ApplicationConfig.class)) {
            PosFacade facade = context.getBean(PosFacade.class);
            var output = new ByteArrayOutputStream();
            PrintStream previous = System.out;
            try {
                System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
                new ConsoleApplication(facade, new Scanner("1\n3\n99\n1\nИван Иванов\nБПИ-1\n6\n1\nнет\n2\n0\n0\n")).run();
            } finally {
                System.setOut(previous);
            }

            assertEquals(1, facade.getStudents().size());
            assertTrue(output.toString(StandardCharsets.UTF_8).contains("Ошибка:"));
            assertTrue(output.toString(StandardCharsets.UTF_8).contains("Удаление отменено."));
            assertTrue(output.toString(StandardCharsets.UTF_8).contains("#1 | Иван Иванов"));
        }
    }

    @Test
    void endOfInputInSectionEndsApplicationNormally() {
        try (var context = new AnnotationConfigApplicationContext(ApplicationConfig.class)) {
            var output = new ByteArrayOutputStream();
            PrintStream previous = System.out;
            try {
                System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
                new ConsoleApplication(context.getBean(PosFacade.class), new Scanner("1\n")).run();
            } finally {
                System.setOut(previous);
            }

            assertTrue(output.toString(StandardCharsets.UTF_8).contains("Ввод завершён."));
        }
    }

}
