package pos.application.view;

import java.util.List;

public record RubricView(Long id, List<CriterionView> criteria) {
    public RubricView {
        criteria = List.copyOf(criteria);
    }

    public CriterionView getCriterion(Long criterionId) {
        return criteria.stream()
                .filter(criterion -> criterion.id().equals(criterionId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Критерий не найден: " + criterionId));
    }
}
