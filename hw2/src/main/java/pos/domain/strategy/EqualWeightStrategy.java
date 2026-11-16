package pos.domain.strategy;

import pos.domain.entity.Criterion;
import pos.domain.entity.Evaluation;
import pos.domain.entity.Rubric;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EqualWeightStrategy implements ResultCalculationStrategy {
    @Override
    public double calculate(Rubric rubric, List<Evaluation> evaluations) {
        Map<Long, Integer> scores = new HashMap<>();
        for (Evaluation evaluation : evaluations) {
            scores.put(evaluation.getCriterionId(), evaluation.getScore().value());
        }
        double sum = 0;
        for (Criterion criterion : rubric.getCriterionList()) {
            int score = scores.getOrDefault(criterion.getId(), 0);
            sum += (double) score / criterion.getMaxScore();
        }
        return rubric.getCriterionList().isEmpty() ? 0 : sum / rubric.getCriterionList().size() * 100;
    }
}
