package pos.domain.repository;

import pos.domain.entity.Student;

import java.util.List;
import java.util.Optional;

public interface StudentRepository {
    void save(Student student);

    Optional<Student> findById(Long id);

    List<Student> findAll();

    boolean existsById(Long id);

    void deleteById(Long id);
}