package pos.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pos.domain.service.ResultCalculationService;
import pos.domain.strategy.EqualWeightStrategy;
import pos.domain.strategy.ResultCalculationStrategy;
import pos.domain.strategy.WeightedAverageStrategy;

@Configuration
public class DomainConfig {

    @Bean("weighted")
    public ResultCalculationStrategy weighted() {
        return new WeightedAverageStrategy();
    }

    @Bean("equal")
    public ResultCalculationStrategy equal() {
        return new EqualWeightStrategy();
    }

    @Bean
    public ResultCalculationService calculationService() {
        return new ResultCalculationService();
    }
}
