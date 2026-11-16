package pos;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import pos.application.facade.PosFacade;
import pos.application.view.EvaluationView;
import pos.application.view.RubricView;
import pos.infrastructure.config.ApplicationConfig;

import static org.junit.jupiter.api.Assertions.*;

class FacadeBoundaryTest {
    @Test
    void returnedRubricAndEvaluationCannotChangeStoredState() {
        try (var context = new AnnotationConfigApplicationContext(ApplicationConfig.class)) {
            PosFacade facade = context.getBean(PosFacade.class);
            long studentId = facade.addStudent("Иван Иванов", "БПИ-1").id();
            facade.startRubricCreation();
            facade.addDraftCriterion("Работа", 10, 1);
            long criterionId = facade.publishRubric().criteria().getFirst().id();
            facade.evaluateStudent(studentId, criterionId, 5, "первый вариант");

            RubricView rubric = facade.getRubric();
            EvaluationView evaluation = facade.getStudentEvaluations(studentId).getFirst();
            assertThrows(UnsupportedOperationException.class, () -> rubric.criteria().clear());
            assertEquals(5, evaluation.score());
            assertEquals(50.0, facade.getStudentResult(studentId, "weighted"));
            assertEquals(5, facade.getStudentEvaluations(studentId).getFirst().score());
            assertEquals(1, facade.getRubric().criteria().size());
        }
    }

    @Test
    void deletingCriterionThroughFacadeAlsoRemovesItsEvaluation() {
        try (var context = new AnnotationConfigApplicationContext(ApplicationConfig.class)) {
            PosFacade facade = context.getBean(PosFacade.class);
            long studentId = facade.addStudent("Иван Иванов", "БПИ-1").id();
            facade.startRubricCreation();
            facade.addDraftCriterion("Работа", 10, 1);
            long criterionId = facade.publishRubric().criteria().getFirst().id();
            facade.evaluateStudent(studentId, criterionId, 5, "");

            facade.removeCriterion(criterionId);

            assertTrue(facade.getRubric().criteria().isEmpty());
            assertTrue(facade.getStudentEvaluations(studentId).isEmpty());
            assertEquals(0.0, facade.getStudentResult(studentId, "weighted"));
        }
    }

    @Test
    void resultsAndEvaluationsAreReturnedInStableIdOrder() {
        try (var context = new AnnotationConfigApplicationContext(ApplicationConfig.class)) {
            PosFacade facade = context.getBean(PosFacade.class);
            long firstStudent = facade.addStudent("Первый", "БПИ-1").id();
            long secondStudent = facade.addStudent("Второй", "БПИ-1").id();
            facade.startRubricCreation();
            facade.addDraftCriterion("Работа", 10, 1);
            facade.addDraftCriterion("Проект", 10, 1);
            var rubric = facade.publishRubric();
            long firstCriterion = rubric.criteria().get(0).id();
            long secondCriterion = rubric.criteria().get(1).id();
            facade.evaluateStudent(firstStudent, secondCriterion, 7, "");
            facade.evaluateStudent(firstStudent, firstCriterion, 8, "");

            assertEquals(java.util.List.of(firstCriterion, secondCriterion),
                    facade.getStudentEvaluations(firstStudent).stream()
                            .map(evaluation -> evaluation.criterionId()).toList());
            assertEquals(java.util.List.of(firstStudent, secondStudent),
                    facade.getAllStudentResults("weighted").stream()
                            .map(row -> row.student().id()).toList());
        }
    }

}
