package pos.domain.repository;

import pos.domain.entity.Evaluation;

import java.util.List;
import java.util.Optional;

public interface EvaluationRepository {
    void save(Evaluation evaluation);

    Optional<Evaluation> findById(Long id);

    List<Evaluation> findByStudentId(Long studentId);

    Optional<Evaluation> findByStudentIdAndCriterionId(
            Long studentId, Long criterionId
    );

    List<Evaluation> findByCriterionId(Long criterionId);

    void deleteById(Long id);
}