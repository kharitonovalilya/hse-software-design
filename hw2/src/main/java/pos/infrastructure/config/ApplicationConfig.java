package pos.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import pos.application.facade.PosFacade;
import pos.application.port.ResultExporter;
import pos.application.service.EvaluationService;
import pos.application.service.ResultService;
import pos.application.service.RubricService;
import pos.application.service.StudentService;
import pos.domain.port.IdGenerator;
import pos.domain.repository.EvaluationRepository;
import pos.domain.repository.RubricRepository;
import pos.domain.repository.StudentRepository;
import pos.domain.service.ResultCalculationService;
import pos.domain.strategy.ResultCalculationStrategy;

import java.util.Map;

@Configuration
@ComponentScan("pos.infrastructure")
public class ApplicationConfig {
    @Bean
    public StudentService studentService(
            IdGenerator ids, StudentRepository students, EvaluationRepository evaluations) {
        return new StudentService(ids, students, evaluations);
    }

    @Bean
    public RubricService rubricService(
            IdGenerator ids, RubricRepository rubrics, EvaluationRepository evaluations) {
        return new RubricService(ids, rubrics, evaluations);
    }

    @Bean
    public EvaluationService evaluationService(
            EvaluationRepository evaluations, StudentRepository students,
            RubricRepository rubrics, IdGenerator ids) {
        return new EvaluationService(evaluations, students, rubrics, ids);
    }

    @Bean
    public ResultService resultService(
            RubricRepository rubrics, StudentRepository students, EvaluationRepository evaluations,
            Map<String, ResultCalculationStrategy> strategies, ResultCalculationService calculation) {
        return new ResultService(rubrics, students, evaluations, strategies, calculation);
    }

    @Bean
    public PosFacade posFacade(
            StudentService students, RubricService rubrics, EvaluationService evaluations,
            ResultService results, ResultExporter exporter) {
        return new PosFacade(students, rubrics, evaluations, results, exporter);
    }
}
