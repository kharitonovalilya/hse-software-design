package pos.console;

import pos.application.facade.PosFacade;
import pos.application.view.CriterionView;
import pos.application.view.EvaluationView;
import pos.application.view.ResultRow;
import pos.application.view.RubricView;
import pos.application.view.StudentView;

import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ConsoleApplication {
    private final PosFacade facade;
    private final ConsoleInput input;
    private String strategy = "weighted";

    public ConsoleApplication(PosFacade facade) {
        this(facade, new Scanner(System.in));
    }

    public ConsoleApplication(PosFacade facade, Scanner scanner) {
        this.facade = facade;
        this.input = new ConsoleInput(scanner);
    }

    public void run() {
        System.out.println("ПОС — Преподавательская оценка студентов");
        try {
            while (true) {
                System.out.println("\n=== ПОС ===\n1. Студенты\n2. Рубрикатор\n3. Оценки\n4. Итоги и CSV\n5. Демонстрация\n0. Выход");
                try {
                    switch (input.readInt("Раздел: ")) {
                        case 1 -> students();
                        case 2 -> rubric();
                        case 3 -> evaluations();
                        case 4 -> results();
                        case 5 -> runDemo();
                        case 0 -> {
                            System.out.println("До свидания!");
                            return;
                        }
                        default -> System.out.println("Неизвестная команда.");
                    }
                } catch (IllegalArgumentException | IllegalStateException exception) {
                    System.out.println("Ошибка: " + exception.getMessage());
                }
            }
        } catch (NoSuchElementException e) {
            System.out.println("\nВвод завершён. До свидания!");
        }
    }

    private void runDemo() {
        if (!facade.getStudents().isEmpty()) {
            System.out.println("Для демонстрации запустите приложение заново с пустыми данными.");
            return;
        }
        try {
            facade.getRubric();
            System.out.println("Для демонстрации запустите приложение заново с пустыми данными.");
            return;
        } catch (IllegalStateException e) {}
        Path destination = Path.of(input.readRequired("Куда сохранить демонстрационный CSV: "));
        if (Files.exists(destination) && !input.confirm("Файл уже существует. Перезаписать его?")) {
            System.out.println("Демонстрация отменена.");
            return;
        }
        try {
            facade.startRubricCreation();
            System.out.println("\n=== Демонстрация ПОС ===");
            System.out.println("1. Создаём студентов и критерии.");
            StudentView first = facade.addStudent("Иван Иванов", "БПИ-1");
            StudentView second = facade.addStudent("Анна Петрова", "БПИ-1");
            printStudents(facade.getStudents());
            facade.addDraftCriterion("Работа", 10, 3);
            facade.addDraftCriterion("Проект", 20, 1);
            RubricView rubric = facade.publishRubric();
            printRubric(rubric);
            long workId = rubric.criteria().get(0).id();
            long projectId = rubric.criteria().get(1).id();

            System.out.println("\n2. Выставляем оценки.");
            facade.evaluateStudent(first.id(), workId, 8, "Работа выполнена");
            facade.evaluateStudent(first.id(), projectId, 20, "Проект защищён");
            facade.evaluateStudent(second.id(), workId, 10, "Отличная работа");
            printEvaluations(first.id());
            printEvaluations(second.id());

            System.out.println("\n3. Считаем итог по весам. Непоставленная оценка считается нулём.");
            for (ResultRow row : facade.getAllStudentResults("weighted")) {
                System.out.printf(Locale.ROOT, "%s: %.2f%%%n", row.student().fullName(), row.resultPercent());
            }
            System.out.println("Ожидаемый итог: Иван — 85%, Анна — 75%.");

            System.out.println("\n4. Экспортируем итог в CSV.");
            facade.exportResults("weighted", destination.toString());
            System.out.println("Экспортировано: " + destination.toAbsolutePath());
            System.out.println("Демонстрация завершена. Данные доступны в обычном меню.");
        } catch (UncheckedIOException exception) {
            System.out.println("Ошибка записи CSV: " + exception.getMessage());
        }
    }

    private void students() {
        while (true) {
            System.out.println("\n=== Студенты ===\n1. Создать  2. Список  3. Посмотреть\n4. Переименовать  5. Сменить группу  6. Удалить\n0. Назад");
            try {
                switch (input.readInt("Команда: ")) {
                    case 1 -> {
                        StudentView student = facade.addStudent(input.readRequired("ФИО: "), input.readRequired("Группа: "));
                        System.out.println("Создан студент #" + student.id());
                    }
                    case 2 -> printStudents(facade.getStudents());
                    case 3 -> printStudent(facade.getStudent(input.readId("ID студента: ")));
                    case 4 -> {
                        facade.renameStudent(input.readId("ID студента: "), input.readRequired("Новое ФИО: "));
                        System.out.println("ФИО изменено.");
                    }
                    case 5 -> {
                        facade.changeStudentGroup(input.readId("ID студента: "), input.readRequired("Новая группа: "));
                        System.out.println("Группа изменена.");
                    }
                    case 6 -> {
                        long id = input.readId("ID студента: ");
                        StudentView student = facade.getStudent(id);
                        int count = facade.getStudentEvaluations(id).size();
                        if (input.confirm("Удалить " + student.fullName() + " и его оценки (" + count + ")?")) {
                            facade.deleteStudent(id);
                            System.out.println("Студент удалён.");
                        } else {
                            System.out.println("Удаление отменено.");
                        }
                    }
                    case 0 -> {
                        return;
                    }
                    default -> System.out.println("Неизвестная команда.");
                }
            } catch (IllegalArgumentException | IllegalStateException exception) {
                System.out.println("Ошибка: " + exception.getMessage());
            }
        }
    }

    private void rubric() {
        while (true) {
            System.out.println("\n=== Рубрикатор ===\n1. Начать черновик  2. Добавить в черновик  3. Опубликовать\n4. Показать  5. Добавить критерий  6. Переименовать\n7. Сменить вес  8. Удалить критерий\n9. Показать черновик  10. Переименовать в черновике\n11. Изменить вес в черновике  12. Удалить из черновика\n13. Отменить черновик  0. Назад");
            try {
                switch (input.readInt("Команда: ")) {
                    case 1 -> {
                        facade.startRubricCreation();
                        System.out.println("Черновик создан. Добавьте критерий перед публикацией.");
                    }
                    case 2 -> {
                        facade.addDraftCriterion(input.readRequired("Название: "), input.readPositiveInt("Максимальный балл: "), input.readPositiveInt("Вес: "));
                        System.out.println("Критерий добавлен в черновик.");
                    }
                    case 3 -> {
                        printRubric(facade.publishRubric());
                        System.out.println("Рубрикатор опубликован.");
                    }
                    case 4 -> printRubric(facade.getRubric());
                    case 5 -> {
                        facade.addCriterion(input.readRequired("Название: "), input.readPositiveInt("Максимальный балл: "), input.readPositiveInt("Вес: "));
                        printRubric(facade.getRubric());
                    }
                    case 6 -> {
                        facade.renameCriterion(input.readId("ID критерия: "), input.readRequired("Новое название: "));
                        System.out.println("Название изменено.");
                    }
                    case 7 -> {
                        facade.changeCriterionWeight(input.readId("ID критерия: "), input.readPositiveInt("Новый вес: "));
                        System.out.println("Вес изменён.");
                    }
                    case 8 -> {
                        long id = input.readId("ID критерия: ");
                        CriterionView criterion = facade.getRubric().getCriterion(id);
                        if (input.confirm("Удалить критерий «" + criterion.name() + "» и все оценки по нему?")) {
                            facade.removeCriterion(id);
                            System.out.println("Критерий удалён.");
                        } else {
                            System.out.println("Удаление отменено.");
                        }
                    }
                    case 9 -> printDraftCriteria();
                    case 10 -> {
                        printDraftCriteria();
                        facade.renameDraftCriterion(input.readId("ID критерия: "), input.readRequired("Новое название: "));
                        System.out.println("Название изменено.");
                    }
                    case 11 -> {
                        printDraftCriteria();
                        facade.changeDraftCriterionWeight(input.readId("ID критерия: "), input.readPositiveInt("Новый вес: "));
                        System.out.println("Вес изменён.");
                    }
                    case 12 -> {
                        printDraftCriteria();
                        facade.removeDraftCriterion(input.readId("ID критерия: "));
                        System.out.println("Критерий удалён из черновика.");
                    }
                    case 13 -> {
                        if (input.confirm("Отменить черновик рубрикатора?")) {
                            facade.cancelDraft();
                            System.out.println("Черновик отменён.");
                        }
                    }
                    case 0 -> {
                        return;
                    }
                    default -> System.out.println("Неизвестная команда.");
                }
            } catch (IllegalArgumentException | IllegalStateException exception) {
                System.out.println("Ошибка: " + exception.getMessage());
            }
        }
    }

    private void evaluations() {
        while (true) {
            System.out.println("\n=== Оценки ===\n1. Выставить  2. Оценки студента  3. Исправить балл\n4. Изменить комментарий  5. Отменить оценку  0. Назад");
            try {
                switch (input.readInt("Команда: ")) {
                    case 1 -> {
                        printStudents(facade.getStudents());
                        long studentId = input.readId("ID студента: ");
                        facade.getStudent(studentId);
                        printRubric(facade.getRubric());
                        long criterionId = input.readId("ID критерия: ");
                        CriterionView criterion = facade.getRubric().getCriterion(criterionId);
                        EvaluationView evaluation = facade.evaluateStudent(studentId, criterionId,
                                input.readIntInRange("Балл (0–" + criterion.maxScore() + "): ", 0, criterion.maxScore()),
                                input.readLine("Комментарий (можно пустой): "));
                        System.out.println("Оценка #" + evaluation.id() + " выставлена.");
                    }
                    case 2 -> printEvaluations(input.readId("ID студента: "));
                    case 3 -> {
                        EvaluationView evaluation = readEvaluation();
                        CriterionView criterion = facade.getRubric().getCriterion(evaluation.criterionId());
                        facade.reviseEvaluation(evaluation.id(),
                                input.readIntInRange("Новый балл (0–" + criterion.maxScore() + "): ", 0, criterion.maxScore()));
                        System.out.println("Балл исправлен.");
                    }
                    case 4 -> {
                        facade.changeEvaluationComment(readEvaluation().id(), input.readLine("Новый комментарий (можно пустой): "));
                        System.out.println("Комментарий изменён.");
                    }
                    case 5 -> {
                        facade.cancelEvaluation(readEvaluation().id());
                        System.out.println("Оценка отменена.");
                    }
                    case 0 -> {
                        return;
                    }
                    default -> System.out.println("Неизвестная команда.");
                }
            } catch (IllegalArgumentException | IllegalStateException exception) {
                System.out.println("Ошибка: " + exception.getMessage());
            }
        }
    }

    private void results() {
        while (true) {
            System.out.println("\n=== Итоги (стратегия: " + strategy + ") ===\n1. Итог студента  2. Ведомость\n3. Выбрать стратегию  4. Экспорт CSV  0. Назад");
            try {
                switch (input.readInt("Команда: ")) {
                    case 1 -> {
                        long id = input.readId("ID студента: ");
                        StudentView student = facade.getStudent(id);
                        System.out.printf(Locale.ROOT, "%s: %.2f%%%n", student.fullName(), facade.getStudentResult(id, strategy));
                    }
                    case 2 -> {
                        List<ResultRow> all = facade.getAllStudentResults(strategy);
                        if (all.isEmpty()) System.out.println("Студентов нет.");
                        all.forEach(row -> System.out.printf(Locale.ROOT, "#%d %s (%s): %.2f%%%n",
                                row.student().id(), row.student().fullName(),
                                row.student().group(), row.resultPercent()));
                    }
                    case 3 -> {
                        System.out.println("Доступны: " + facade.getAvailableStrategies() + " (weighted — по весам; equal — равные веса)");
                        String selected = input.readRequired("Стратегия: ");
                        if (!facade.getAvailableStrategies().contains(selected)) {
                            System.out.println("Неизвестная стратегия.");
                        } else {
                            strategy = selected;
                            System.out.println("Выбрана: " + strategy);
                        }
                    }
                    case 4 -> {
                        Path destination = Path.of(input.readRequired("Путь к CSV: "));
                        if (!Files.exists(destination) || input.confirm("Файл уже существует. Перезаписать его?")) {
                            facade.exportResults(strategy, destination.toString());
                            System.out.println("Экспортировано: " + destination.toAbsolutePath());
                        } else {
                            System.out.println("Экспорт отменён.");
                        }
                    }
                    case 0 -> {
                        return;
                    }
                    default -> System.out.println("Неизвестная команда.");
                }
            } catch (IllegalArgumentException | IllegalStateException exception) {
                System.out.println("Ошибка: " + exception.getMessage());
            } catch (UncheckedIOException exception) {
                System.out.println("Ошибка записи CSV: " + exception.getMessage());
            }
        }
    }

    private EvaluationView readEvaluation() {
        printStudents(facade.getStudents());
        long studentId = input.readId("ID студента: ");
        printEvaluations(studentId);
        long evaluationId = input.readId("ID оценки: ");
        return facade.getStudentEvaluations(studentId).stream()
                .filter(evaluation -> evaluation.id().equals(evaluationId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Оценка не найдена у выбранного студента."));
    }

    private void printDraftCriteria() {
        List<CriterionView> criteria = facade.getDraftCriteria();
        System.out.println("Черновик рубрикатора:");
        if (criteria.isEmpty()) {
            System.out.println("Критериев пока нет.");
        }
        for (CriterionView criterion : criteria) {
            printCriterion(criterion);
        }
    }

    private void printCriterion(CriterionView criterion) {
        System.out.println("#" + criterion.id() + " | " + criterion.name()
                + " | максимум " + criterion.maxScore() + " | вес " + criterion.weight());
    }

    private void printStudents(List<StudentView> students) {
        if (students.isEmpty()) System.out.println("Студентов нет.");
        else students.forEach(this::printStudent);
    }

    private void printStudent(StudentView student) {
        System.out.println("#" + student.id() + " | " + student.fullName() + " | " + student.group());
    }

    private void printRubric(RubricView rubric) {
        System.out.println("Рубрикатор #" + rubric.id());
        for (CriterionView criterion : rubric.criteria()) {
            printCriterion(criterion);
        }
        if (rubric.criteria().isEmpty()) System.out.println("Критериев нет.");
    }

    private void printEvaluations(long studentId) {
        StudentView student = facade.getStudent(studentId);
        List<EvaluationView> evaluations = facade.getStudentEvaluations(studentId);
        System.out.println("Оценки: " + student.fullName());
        if (evaluations.isEmpty()) {
            System.out.println("Оценок нет.");
            return;
        }
        RubricView rubric = facade.getRubric();
        for (EvaluationView evaluation : evaluations) {
            CriterionView criterion = rubric.getCriterion(evaluation.criterionId());
            System.out.println("#" + evaluation.id() + " | " + criterion.name() + " | " + evaluation.score() + "/" + criterion.maxScore() + " | " + (evaluation.comment() == null ? "" : evaluation.comment()));
        }
    }

}
