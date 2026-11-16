package pos;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import pos.application.view.ResultRow;
import pos.application.view.StudentView;
import pos.infrastructure.export.CsvResultExporter;

import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CsvResultExporterTest {
    @TempDir
    Path directory;

    @Test
    void quotesCommasQuotesAndNewlinesAndReplacesExistingCsv() throws Exception {
        Path file = directory.resolve("results.csv");
        Files.writeString(file, "old data");
        var student = new StudentView(1L, "Иванов, \"Иван\"", "БПИ\n1");

        new CsvResultExporter().export(List.of(new ResultRow(student, 37.5)), file.toString());

        assertEquals("student_id,full_name,group,result_percent" + System.lineSeparator()
                        + "1,\"Иванов, \"\"Иван\"\"\",\"БПИ\n1\",37.50" + System.lineSeparator(),
                Files.readString(file));
    }

    @Test
    void preservesFormulaLikeTextAsRequested() throws Exception {
        Path file = directory.resolve("formula.csv");
        var student = new StudentView(2L, "=1+1", "+группа");

        new CsvResultExporter().export(
                List.of(new ResultRow(student, 0.0)), file.toString());

        assertEquals("student_id,full_name,group,result_percent" + System.lineSeparator()
                        + "2,\"=1+1\",\"+группа\",0.00" + System.lineSeparator(),
                Files.readString(file));
    }

    @Test
    void reportsFileWriteFailureWithStandardException() {
        var student = new StudentView(1L, "Иван Иванов", "БПИ-1");

        assertThrows(UncheckedIOException.class, () -> new CsvResultExporter().export(
                List.of(new ResultRow(student, 50.0)), directory.toString()));
    }

}
