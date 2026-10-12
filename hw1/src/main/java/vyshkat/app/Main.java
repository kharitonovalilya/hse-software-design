package vyshkat.app;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {
    public static void main(String[] args) {
        try (var context = new AnnotationConfigApplicationContext("vyshkat")) {
            ConsoleApplication app =
                    context.getBean(ConsoleApplication.class);
            app.run();
        }
    }
}
