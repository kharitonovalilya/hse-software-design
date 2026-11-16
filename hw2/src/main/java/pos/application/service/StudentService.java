package pos.application.service;

import pos.domain.entity.Evaluation;
import pos.domain.entity.Student;
import pos.domain.port.IdGenerator;
import pos.domain.repository.EvaluationRepository;
import pos.domain.repository.StudentRepository;

import java.util.List;

public class StudentService {

    private final IdGenerator idGenerator;
    private final StudentRepository studentRepository;
    private final EvaluationRepository evaluationRepository;

    public StudentService(IdGenerator idGenerator, StudentRepository studentRepository, EvaluationRepository evaluationRepository) {
        this.idGenerator = idGenerator;
        this.studentRepository = studentRepository;
        this.evaluationRepository = evaluationRepository;
    }

    public Student createStudent(String fullName, String group) {
        long id = idGenerator.nextId();
        Student student = new Student(id, fullName, group);
        studentRepository.save(student);
        return student;
    }

    public Student getStudent(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Студент с ID %d не найден.".formatted(id)));
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public void renameStudent(Long id, String newName) {
        Student student = getStudent(id);
        student.rename(newName);
        studentRepository.save(student);
    }

    public void changeStudentGroup(Long id, String newGroup) {
        Student student = getStudent(id);
        student.changeGroup(newGroup);
        studentRepository.save(student);
    }

    public void deleteStudent(Long id) {
        getStudent(id);
        List<Evaluation> evaluations = evaluationRepository.findByStudentId(id);
        for (Evaluation evaluation : evaluations) {
            evaluationRepository.deleteById(evaluation.getId());
        }
        studentRepository.deleteById(id);
    }

}
