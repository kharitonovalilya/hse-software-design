package pos.application.port;

import pos.application.view.ResultRow;

import java.util.List;

public interface ResultExporter {
    void export(List<ResultRow> results, String destination);
}
