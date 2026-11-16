package pos.application.view;

public record EvaluationView(Long id, Long studentId, Long criterionId, int score, String comment) {
}
