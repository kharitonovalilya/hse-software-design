package pos.domain.strategy;

import pos.domain.entity.Criterion;
import pos.domain.entity.Evaluation;
import pos.domain.entity.Rubric;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeightedAverageStrategy implements ResultCalculationStrategy {
    @Override
    public double calculate(Rubric rubric, List<Evaluation> evaluations) {
        Map<Long, Integer> scores = new HashMap<>();
        for (Evaluation evaluation : evaluations) {
            scores.put(evaluation.getCriterionId(), evaluation.getScore().value());
        }
        double weightedSum = 0;
        double totalWeight = 0;
        for (Criterion criterion : rubric.getCriterionList()) {
            int score = scores.getOrDefault(criterion.getId(), 0);
            weightedSum += (double) score / criterion.getMaxScore() * criterion.getWeight();
            totalWeight += criterion.getWeight();
        }
        if (totalWeight == 0) {
            return 0;
        }
        return weightedSum / totalWeight * 100;
    }
}
