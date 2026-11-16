package pos.infrastructure.persistence;

import org.springframework.stereotype.Repository;
import pos.domain.entity.Evaluation;
import pos.domain.repository.EvaluationRepository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class InMemoryEvaluationRepository implements EvaluationRepository {

    private final Map<Long, Evaluation> evaluationMap = new LinkedHashMap<>();

    @Override
    public void save(Evaluation evaluation) {
        if (evaluation == null) {
            throw new IllegalArgumentException("Evaluation must not be null.");
        }
        evaluationMap.put(evaluation.getId(), evaluation);
    }

    @Override
    public Optional<Evaluation> findById(Long id) {
        return Optional.ofNullable(evaluationMap.get(id));
    }

    @Override
    public List<Evaluation> findByStudentId(Long studentId) {
        return evaluationMap.values().stream()
                .filter(evaluation -> evaluation.getStudentId().equals(studentId))
                .toList();
    }

    @Override
    public Optional<Evaluation> findByStudentIdAndCriterionId(Long studentId, Long criterionId) {
        return evaluationMap.values().stream()
                .filter(evaluation -> evaluation.getStudentId().equals(studentId))
                .filter(evaluation -> evaluation.getCriterionId().equals(criterionId))
                .findFirst();
    }

    @Override
    public List<Evaluation> findByCriterionId(Long criterionId) {
        return evaluationMap.values().stream()
                .filter(evaluation -> evaluation.getCriterionId().equals(criterionId))
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        evaluationMap.remove(id);
    }
}
