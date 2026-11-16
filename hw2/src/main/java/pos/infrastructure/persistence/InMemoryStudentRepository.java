package pos.infrastructure.persistence;

import org.springframework.stereotype.Repository;
import pos.domain.entity.Student;
import pos.domain.repository.StudentRepository;

import java.util.*;

@Repository
public class InMemoryStudentRepository implements StudentRepository {

    private final Map<Long, Student> studentMap = new LinkedHashMap<>();

    @Override
    public void save(Student student) {
        if (student == null) {
            throw new IllegalArgumentException("Student must be not null.");
        }
        studentMap.put(student.getId(), student);
    }

    @Override
    public Optional<Student> findById(Long id) {
        return Optional.ofNullable(studentMap.get(id));
    }

    @Override
    public List<Student> findAll() {
        return List.copyOf(studentMap.values());
    }

    @Override
    public boolean existsById(Long id) {
        return studentMap.containsKey(id);
    }

    @Override
    public void deleteById(Long id) {
        studentMap.remove(id);
    }
}
