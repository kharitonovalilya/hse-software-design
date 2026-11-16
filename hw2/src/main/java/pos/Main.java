package pos;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import pos.application.facade.PosFacade;
import pos.console.ConsoleApplication;
import pos.infrastructure.config.ApplicationConfig;

public class Main {
    public static void main(String[] args) {
        try (var context = new AnnotationConfigApplicationContext(ApplicationConfig.class)) {
            PosFacade facade = context.getBean(PosFacade.class);
            new ConsoleApplication(facade).run();
        }
    }
}
