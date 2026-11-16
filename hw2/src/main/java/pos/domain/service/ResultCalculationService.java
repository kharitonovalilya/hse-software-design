package pos.domain.service;

import pos.domain.entity.Evaluation;
import pos.domain.entity.Rubric;
import pos.domain.strategy.ResultCalculationStrategy;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ResultCalculationService {

    public double calculate(
            Long studentId,
            Rubric rubric,
            List<Evaluation> evaluations,
            ResultCalculationStrategy strategy
    ) {
        Set<Long> criterionIds = new HashSet<>();
        for (Evaluation evaluation : evaluations) {
            if (!evaluation.getStudentId().equals(studentId)) {
                throw new IllegalArgumentException("Набор оценок содержит другого студента.");
            }
            rubric.validateScore(evaluation.getCriterionId(), evaluation.getScore());
            if (!criterionIds.add(evaluation.getCriterionId())) {
                throw new IllegalArgumentException("Набор оценок содержит повторный критерий.");
            }
        }
        return strategy.calculate(rubric, evaluations);
    }
}
