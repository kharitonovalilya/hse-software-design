package pos.domain.strategy;

import pos.domain.entity.Evaluation;
import pos.domain.entity.Rubric;

import java.util.List;

public interface ResultCalculationStrategy {
    double calculate(Rubric rubric, List<Evaluation> evaluations);
}
