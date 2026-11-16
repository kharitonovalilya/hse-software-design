package pos.application.service;

import pos.domain.entity.Evaluation;
import pos.domain.entity.Rubric;
import pos.domain.repository.EvaluationRepository;
import pos.domain.repository.RubricRepository;
import pos.domain.repository.StudentRepository;
import pos.domain.service.ResultCalculationService;
import pos.domain.strategy.ResultCalculationStrategy;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ResultService {

    private final Map<String, ResultCalculationStrategy> strategies;
    private final ResultCalculationService resultCalculationService;

    private final RubricRepository rubricRepository;
    private final StudentRepository studentRepository;
    private final EvaluationRepository evaluationRepository;

    public ResultService(RubricRepository rubricRepository, StudentRepository studentRepository, EvaluationRepository evaluationRepository,
                         Map<String, ResultCalculationStrategy> strategies, ResultCalculationService resultCalculationService) {
        this.rubricRepository = rubricRepository;
        this.studentRepository = studentRepository;
        this.evaluationRepository = evaluationRepository;
        this.resultCalculationService = resultCalculationService;
        this.strategies = strategies;
    }

    public double calculateResult(Long studentId, String strategyName) {
        ResultCalculationStrategy strategy = strategies.get(strategyName);
        if (strategy == null) {
            throw new IllegalArgumentException("Неизвестная стратегия расчёта: " + strategyName);
        }
        if (!studentRepository.existsById(studentId)) {
            throw new IllegalArgumentException("Студент не найден.");
        }
        Rubric rubric = rubricRepository.get()
                .orElseThrow(() -> new IllegalStateException("Рубрикатор не найден."));
        List<Evaluation> evaluations = evaluationRepository.findByStudentId(studentId);
        return resultCalculationService.calculate(studentId, rubric, evaluations, strategy);
    }

    public Set<String> getAvailableStrategies() {
        return Set.copyOf(strategies.keySet());
    }

}
