package pos.infrastructure.export;

import org.springframework.stereotype.Component;
import pos.application.port.ResultExporter;
import pos.application.view.ResultRow;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Locale;

@Component
public class CsvResultExporter implements ResultExporter {

    @Override
    public void export(List<ResultRow> results, String destination) {
        try {
            write(results, Path.of(destination));
        } catch (IOException exception) {
            throw new UncheckedIOException("Не удалось записать ведомость: " + exception.getMessage(), exception);
        }
    }

    private void write(List<ResultRow> results, Path destination) throws IOException {
        Path parent = destination.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        try (BufferedWriter writer = Files.newBufferedWriter(
                destination, StandardCharsets.UTF_8)) {
            writer.write("student_id,full_name,group,result_percent");
            writer.newLine();
            for (ResultRow row : results) {
                writer.write(
                        row.student().id() + "," +
                                escape(row.student().fullName()) + "," +
                                escape(row.student().group()) + "," +
                                String.format(
                                        Locale.ROOT, "%.2f", row.resultPercent()
                                )
                );
                writer.newLine();
            }
        }
    }

    private String escape(String value) {
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }
}
