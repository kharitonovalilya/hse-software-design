package pos.application.service;

import pos.domain.entity.Evaluation;
import pos.domain.entity.Rubric;
import pos.domain.value.Score;
import pos.domain.port.IdGenerator;
import pos.domain.repository.EvaluationRepository;
import pos.domain.repository.RubricRepository;
import pos.domain.repository.StudentRepository;

import java.util.List;

public class EvaluationService {

    private final EvaluationRepository evaluationRepository;
    private final StudentRepository studentRepository;
    private final RubricRepository rubricRepository;
    private final IdGenerator idGenerator;

    public EvaluationService(EvaluationRepository evaluationRepository, StudentRepository studentRepository, RubricRepository rubricRepository, IdGenerator idGenerator) {
        this.evaluationRepository = evaluationRepository;
        this.studentRepository = studentRepository;
        this.rubricRepository = rubricRepository;
        this.idGenerator = idGenerator;
    }

    public Evaluation createEvaluation(
            Long studentId,
            Long criterionId,
            int score,
            String comment
    ) {
        if (!studentRepository.existsById(studentId)) {
            throw new IllegalArgumentException("Студент с ID %d не найден.".formatted(studentId));
        }
        Rubric rubric = getRubric();
        if (evaluationRepository
                .findByStudentIdAndCriterionId(studentId, criterionId)
                .isPresent()) {
            throw new IllegalStateException("Оценка по этому критерию уже выставлена студенту.");
        }
        Evaluation evaluation = new Evaluation(
                idGenerator.nextId(),
                studentId,
                criterionId,
                new Score(score),
                comment,
                rubric
        );
        evaluationRepository.save(evaluation);
        return evaluation;
    }

    public Evaluation getEvaluation(Long id) {
        return evaluationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Оценка с ID %d не найдена.".formatted(id)));
    }

    public List<Evaluation> getStudentEvaluations(Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new IllegalArgumentException("Студент с ID %d не найден.".formatted(studentId));
        }
        return evaluationRepository.findByStudentId(studentId);
    }

    public void reviseEvaluation(Long id, int newScore) {
        Evaluation evaluation = getEvaluation(id);
        Rubric rubric = getRubric();
        evaluation.reviseScore(new Score(newScore), rubric);
        evaluationRepository.save(evaluation);
    }

    public void changeEvaluationComment(Long id, String comment) {
        Evaluation evaluation = getEvaluation(id);
        evaluation.changeComment(comment);
        evaluationRepository.save(evaluation);
    }

    public void cancelEvaluation(Long id) {
        getEvaluation(id);
        evaluationRepository.deleteById(id);
    }

    private Rubric getRubric() {
        return rubricRepository.get()
                .orElseThrow(() -> new IllegalStateException("Рубрикатор не создан."));
    }
}
