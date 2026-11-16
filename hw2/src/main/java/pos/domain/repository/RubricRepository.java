package pos.domain.repository;

import pos.domain.entity.Rubric;

import java.util.Optional;

public interface RubricRepository {
    void save(Rubric rubric);

    Optional<Rubric> get();
}