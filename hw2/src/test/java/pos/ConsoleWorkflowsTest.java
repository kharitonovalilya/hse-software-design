package pos;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import pos.application.facade.PosFacade;
import pos.console.ConsoleApplication;
import pos.infrastructure.config.ApplicationConfig;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class ConsoleWorkflowsTest {
    @TempDir
    Path directory;

    @Test
    void createsRubricRevisesEvaluationCalculatesBothStrategiesAndExportsCsv() throws Exception {
        try (var context = new AnnotationConfigApplicationContext(ApplicationConfig.class)) {
            PosFacade facade = context.getBean(PosFacade.class);
            Path csv = directory.resolve("results.csv");
            String input = """
                    2
                    1
                    2
                    Работа
                    10
                    3
                    2
                    Проект
                    20
                    1
                    9
                    3
                    0
                    1
                    1
                    Иван Иванов
                    БПИ-1
                    0
                    3
                    1
                    4
                    2
                    5
                    первый вариант
                    2
                    4
                    3
                    4
                    5
                    10
                    4
                    4
                    5
                    исправлено
                    0
                    4
                    1
                    4
                    2
                    3
                    equal
                    2
                    3
                    weighted
                    4
                    """ + csv + "\n0\n0\n";

            String output = runConsole(facade, input);

            assertTrue(output.contains("Черновик рубрикатора:"));
            assertTrue(output.contains("Иван Иванов: 75.00%"));
            assertTrue(output.contains("Иван Иванов (БПИ-1): 50.00%"));
            assertEquals(10, facade.getStudentEvaluations(4L).getFirst().score());
            assertEquals("исправлено", facade.getStudentEvaluations(4L).getFirst().comment());
            assertTrue(Files.readString(csv).contains("\"Иван Иванов\",\"БПИ-1\",75.00"));
        }
    }

    @Test
    void correctsAndCancelsDraftThenChangesPublishedCriteria() {
        try (var context = new AnnotationConfigApplicationContext(ApplicationConfig.class)) {
            PosFacade facade = context.getBean(PosFacade.class);
            String input = """
                    2
                    1
                    2
                    Ошибка
                    10
                    1
                    10
                    2
                    Работа
                    11
                    2
                    3
                    12
                    2
                    13
                    да
                    1
                    2
                    Работа
                    10
                    1
                    3
                    5
                    Проект
                    20
                    1
                    6
                    5
                    Защита
                    7
                    5
                    2
                    8
                    5
                    да
                    4
                    0
                    0
                    """;

            String output = runConsole(facade, input);

            assertTrue(output.contains("Черновик отменён."));
            assertTrue(output.contains("Критерий удалён."));
            assertEquals(1, facade.getRubric().criteria().size());
            assertEquals("Работа", facade.getRubric().criteria().getFirst().name());
        }
    }

    private String runConsole(PosFacade facade, String input) {
        PrintStream previous = System.out;
        var output = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
            new ConsoleApplication(facade, new Scanner(input)).run();
        } finally {
            System.setOut(previous);
        }
        return output.toString(StandardCharsets.UTF_8);
    }
}
