package pos.infrastructure.persistence;

import org.springframework.stereotype.Repository;
import pos.domain.entity.Rubric;
import pos.domain.repository.RubricRepository;

import java.util.Optional;

@Repository
public class InMemoryRubricRepository implements RubricRepository {

    private Rubric rubric;

    @Override
    public void save(Rubric rubric) {
        if (rubric == null) {
            throw new IllegalArgumentException("Rubric must not be null.");
        }
        this.rubric = rubric;
    }

    @Override
    public Optional<Rubric> get() {
        return Optional.ofNullable(rubric);
    }
}
