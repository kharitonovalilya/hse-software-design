package pos;

import org.junit.jupiter.api.Test;
import pos.application.service.EvaluationService;
import pos.application.service.RubricService;
import pos.application.service.StudentService;
import pos.infrastructure.id.SequentialIdGenerator;
import pos.infrastructure.persistence.InMemoryEvaluationRepository;
import pos.infrastructure.persistence.InMemoryRubricRepository;
import pos.infrastructure.persistence.InMemoryStudentRepository;

import static org.junit.jupiter.api.Assertions.*;

class ApplicationRulesTest {
    private final SequentialIdGenerator ids = new SequentialIdGenerator();
    private final InMemoryStudentRepository students = new InMemoryStudentRepository();
    private final InMemoryRubricRepository rubrics = new InMemoryRubricRepository();
    private final InMemoryEvaluationRepository evaluations = new InMemoryEvaluationRepository();
    private final StudentService studentService = new StudentService(ids, students, evaluations);
    private final RubricService rubricService = new RubricService(ids, rubrics, evaluations);
    private final EvaluationService evaluationService = new EvaluationService(evaluations, students, rubrics, ids);

    @Test
    void evaluationRequiresExistingStudentAndUniqueStudentCriterionPair() {
        rubricService.startRubricCreation();
        rubricService.addDraftCriterion("Работа", 10, 1);
        long criterionId = rubricService.publishRubric().getCriterionList().getFirst().getId();
        long studentId = studentService.createStudent("Иван Иванов", "БПИ-1").getId();

        assertThrows(IllegalArgumentException.class,
                () -> evaluationService.createEvaluation(999L, criterionId, 5, ""));
        evaluationService.createEvaluation(studentId, criterionId, 5, "");
        assertThrows(IllegalStateException.class,
                () -> evaluationService.createEvaluation(studentId, criterionId, 6, ""));
        assertEquals(1, evaluationService.getStudentEvaluations(studentId).size());
    }

    @Test
    void deletingCriterionAndStudentRemovesOnlyTheirEvaluations() {
        long firstStudent = studentService.createStudent("Первый", "БПИ-1").getId();
        long secondStudent = studentService.createStudent("Второй", "БПИ-1").getId();
        rubricService.startRubricCreation();
        rubricService.addDraftCriterion("Работа", 10, 1);
        rubricService.addDraftCriterion("Проект", 10, 1);
        var rubric = rubricService.publishRubric();
        long firstCriterion = rubric.getCriterionList().get(0).getId();
        long secondCriterion = rubric.getCriterionList().get(1).getId();
        evaluationService.createEvaluation(firstStudent, firstCriterion, 5, "");
        evaluationService.createEvaluation(firstStudent, secondCriterion, 6, "");
        evaluationService.createEvaluation(secondStudent, secondCriterion, 7, "");

        rubricService.removeCriterion(firstCriterion);
        assertEquals(1, evaluationService.getStudentEvaluations(firstStudent).size());
        assertEquals(1, evaluationService.getStudentEvaluations(secondStudent).size());
        studentService.deleteStudent(firstStudent);
        assertTrue(evaluations.findByStudentId(firstStudent).isEmpty());
        assertEquals(1, evaluationService.getStudentEvaluations(secondStudent).size());
    }

    @Test
    void draftCanBeCorrectedCancelledAndStartedAgain() {
        rubricService.startRubricCreation();
        rubricService.addDraftCriterion("Черновое название", 10, 1);
        long criterionId = rubricService.getDraftCriteria().getFirst().getId();

        rubricService.renameDraftCriterion(criterionId, "Работа");
        rubricService.changeDraftCriterionWeight(criterionId, 3);
        assertEquals("Работа", rubricService.getDraftCriteria().getFirst().getName());
        assertEquals(3, rubricService.getDraftCriteria().getFirst().getWeight());
        rubricService.removeDraftCriterion(criterionId);
        assertThrows(IllegalStateException.class, rubricService::publishRubric);
        rubricService.cancelDraft();
        rubricService.startRubricCreation();
        rubricService.addDraftCriterion("Новый критерий", 5, 1);
        assertEquals("Новый критерий", rubricService.publishRubric()
                .getCriterionList().getFirst().getName());
    }

}
