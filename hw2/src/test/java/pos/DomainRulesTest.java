package pos;

import org.junit.jupiter.api.Test;
import pos.domain.entity.Evaluation;
import pos.domain.entity.Rubric;
import pos.domain.service.ResultCalculationService;
import pos.domain.strategy.EqualWeightStrategy;
import pos.domain.strategy.WeightedAverageStrategy;
import pos.domain.value.Score;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DomainRulesTest {
    @Test
    void scoreCannotBeNegative() {
        assertThrows(IllegalArgumentException.class, () -> new Score(-1));
        assertEquals(0, new Score(0).value());
    }

    @Test
    void criterionRequiresPositiveMaximumAndWeight() {
        Rubric rubric = new Rubric(1L);

        assertThrows(IllegalArgumentException.class, () -> rubric.addCriterion(2L, "Работа", 0, 1));
        assertThrows(IllegalArgumentException.class, () -> rubric.addCriterion(2L, "Работа", 10, 0));
        assertTrue(rubric.getCriterionList().isEmpty());
    }

    @Test
    void evaluationRequiresExistingCriterionAndScoreWithinItsMaximum() {
        Rubric rubric = new Rubric(1L);
        rubric.addCriterion(2L, "Работа", 10, 2);

        assertThrows(IllegalArgumentException.class, () -> new Evaluation(4L, 5L, 2L, new Score(11), "", rubric));
        assertThrows(IllegalArgumentException.class, () -> new Evaluation(4L, 5L, 99L, new Score(1), "", rubric));
        assertEquals(10, new Evaluation(4L, 5L, 2L, new Score(10), "", rubric).getScore().value());
    }

    @Test
    void rubricRejectsDuplicateNamesWithoutChangingExistingCriterion() {
        Rubric rubric = new Rubric(1L);
        rubric.addCriterion(2L, "Работа", 10, 1);
        rubric.addCriterion(3L, "Проект", 10, 1);

        assertThrows(IllegalArgumentException.class, () -> rubric.addCriterion(4L, " работа ", 10, 1));
        assertThrows(IllegalArgumentException.class, () -> rubric.renameCriterion(3L, "РАБОТА"));
        assertEquals("Проект", rubric.getCriterion(3L).getName());
        assertEquals(2, rubric.getCriterionList().size());
    }

    @Test
    void revisionRejectsInvalidScoreWithoutChangingPreviousValue() {
        Rubric rubric = new Rubric(1L);
        rubric.addCriterion(2L, "Работа", 10, 1);
        Evaluation evaluation = new Evaluation(3L, 4L, 2L, new Score(7), "", rubric);

        assertThrows(IllegalArgumentException.class, () -> evaluation.reviseScore(new Score(11), rubric));
        assertEquals(7, evaluation.getScore().value());
    }

    @Test
    void strategiesCalculatePercentUsingAllCriteriaAndMissingScoresAsZero() {
        Rubric rubric = new Rubric(1L);
        rubric.addCriterion(2L, "Тест", 10, 3);
        rubric.addCriterion(3L, "Проект", 20, 1);
        var evaluations = List.of(new Evaluation(4L, 5L, 2L, new Score(10), "", rubric));
        var calculation = new ResultCalculationService();

        assertEquals(75.0, calculation.calculate(5L, rubric, evaluations, new WeightedAverageStrategy()));
        assertEquals(50.0, calculation.calculate(5L, rubric, evaluations, new EqualWeightStrategy()));
        assertEquals(0.0, calculation.calculate(5L, rubric, List.of(), new WeightedAverageStrategy()));
    }

    @Test
    void calculationRejectsScoreThatExceedsCurrentCriterionMaximum() {
        Rubric current = new Rubric(1L);
        current.addCriterion(2L, "Работа", 10, 1);
        Rubric foreign = new Rubric(3L);
        foreign.addCriterion(2L, "Работа", 100, 1);
        Evaluation evaluation = new Evaluation(4L, 5L, 2L, new Score(99), "", foreign);

        assertThrows(IllegalArgumentException.class,
                () -> new ResultCalculationService().calculate(
                        5L, current, List.of(evaluation), new WeightedAverageStrategy()));
    }

    @Test
    void duplicateCriterionEvaluationIsRejectedByCalculation() {
        Rubric rubric = new Rubric(1L);
        rubric.addCriterion(2L, "Тест", 10, 1);
        var evaluations = List.of(
                new Evaluation(3L, 4L, 2L, new Score(5), "", rubric),
                new Evaluation(5L, 4L, 2L, new Score(6), "", rubric));

        assertThrows(IllegalArgumentException.class,
                () -> new ResultCalculationService().calculate(4L, rubric, evaluations, new WeightedAverageStrategy()));
    }
}
