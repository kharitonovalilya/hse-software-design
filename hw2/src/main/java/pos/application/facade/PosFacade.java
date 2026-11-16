package pos.application.facade;

import pos.application.port.ResultExporter;
import pos.application.service.EvaluationService;
import pos.application.service.ResultService;
import pos.application.service.RubricService;
import pos.application.service.StudentService;
import pos.application.view.CriterionView;
import pos.application.view.EvaluationView;
import pos.application.view.ResultRow;
import pos.application.view.RubricView;
import pos.application.view.StudentView;
import pos.domain.entity.Criterion;
import pos.domain.entity.Evaluation;
import pos.domain.entity.Rubric;
import pos.domain.entity.Student;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class PosFacade {
    private final StudentService studentService;
    private final RubricService rubricService;
    private final EvaluationService evaluationService;
    private final ResultService resultService;
    private final ResultExporter resultExporter;

    public PosFacade(
            StudentService studentService,
            RubricService rubricService,
            EvaluationService evaluationService,
            ResultService resultService,
            ResultExporter resultExporter
    ) {
        this.studentService = studentService;
        this.rubricService = rubricService;
        this.evaluationService = evaluationService;
        this.resultService = resultService;
        this.resultExporter = resultExporter;
    }

    public StudentView addStudent(String fullName, String group) {
        return studentView(studentService.createStudent(fullName, group));
    }

    public List<StudentView> getStudents() {
        return studentService.getAllStudents().stream()
                .sorted(Comparator.comparing(Student::getId))
                .map(PosFacade::studentView)
                .toList();
    }

    public StudentView getStudent(Long id) {
        return studentView(studentService.getStudent(id));
    }

    public void renameStudent(Long id, String newName) {
        studentService.renameStudent(id, newName);
    }

    public void changeStudentGroup(Long id, String newGroup) {
        studentService.changeStudentGroup(id, newGroup);
    }

    public void deleteStudent(Long id) {
        studentService.deleteStudent(id);
    }

    public void startRubricCreation() {
        rubricService.startRubricCreation();
    }

    public void cancelDraft() {
        rubricService.cancelDraft();
    }

    public List<CriterionView> getDraftCriteria() {
        return rubricService.getDraftCriteria().stream().map(PosFacade::criterionView).toList();
    }

    public void addDraftCriterion(String name, int maxScore, int weight) {
        rubricService.addDraftCriterion(name, maxScore, weight);
    }

    public void renameDraftCriterion(Long id, String name) {
        rubricService.renameDraftCriterion(id, name);
    }

    public void changeDraftCriterionWeight(Long id, int weight) {
        rubricService.changeDraftCriterionWeight(id, weight);
    }

    public void removeDraftCriterion(Long id) {
        rubricService.removeDraftCriterion(id);
    }

    public RubricView publishRubric() {
        return rubricView(rubricService.publishRubric());
    }

    public RubricView getRubric() {
        return rubricView(rubricService.getRubric());
    }

    public void addCriterion(String name, int maxScore, int weight) {
        rubricService.addCriterion(name, maxScore, weight);
    }

    public void renameCriterion(Long id, String name) {
        rubricService.renameCriterion(id, name);
    }

    public void changeCriterionWeight(Long id, int weight) {
        rubricService.changeCriterionWeight(id, weight);
    }

    public void removeCriterion(Long id) {
        rubricService.removeCriterion(id);
    }

    public EvaluationView evaluateStudent(Long studentId, Long criterionId, int score, String comment) {
        return evaluationView(evaluationService.createEvaluation(studentId, criterionId, score, comment));
    }

    public List<EvaluationView> getStudentEvaluations(Long studentId) {
        return evaluationService.getStudentEvaluations(studentId).stream()
                .sorted(Comparator.comparing(Evaluation::getCriterionId))
                .map(PosFacade::evaluationView)
                .toList();
    }

    public void reviseEvaluation(Long id, int score) {
        evaluationService.reviseEvaluation(id, score);
    }

    public void changeEvaluationComment(Long id, String comment) {
        evaluationService.changeEvaluationComment(id, comment);
    }

    public void cancelEvaluation(Long id) {
        evaluationService.cancelEvaluation(id);
    }

    public Set<String> getAvailableStrategies() {
        return resultService.getAvailableStrategies();
    }

    public double getStudentResult(Long studentId, String strategyName) {
        return resultService.calculateResult(studentId, strategyName);
    }

    public List<ResultRow> getAllStudentResults(String strategyName) {
        return getStudents().stream()
                .map(student -> new ResultRow(
                        student, resultService.calculateResult(student.id(), strategyName)))
                .toList();
    }

    public void exportResults(String strategyName, String destination) {
        resultExporter.export(getAllStudentResults(strategyName), destination);
    }

    private static StudentView studentView(Student student) {
        return new StudentView(student.getId(), student.getFullName(), student.getGroup());
    }

    private static CriterionView criterionView(Criterion criterion) {
        return new CriterionView(
                criterion.getId(), criterion.getName(), criterion.getMaxScore(), criterion.getWeight());
    }

    private static RubricView rubricView(Rubric rubric) {
        return new RubricView(
                rubric.getId(), rubric.getCriterionList().stream().map(PosFacade::criterionView).toList());
    }

    private static EvaluationView evaluationView(Evaluation evaluation) {
        return new EvaluationView(
                evaluation.getId(), evaluation.getStudentId(), evaluation.getCriterionId(),
                evaluation.getScore().value(), evaluation.getComment());
    }
}
