package pos.console;

import java.util.Scanner;

class ConsoleInput {
    private final Scanner scanner;

    ConsoleInput(Scanner scanner) {
        this.scanner = scanner;
    }

    String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    String readRequired(String prompt) {
        while (true) {
            String value = readLine(prompt);
            if (!value.isBlank()) return value;
            System.out.println("Значение не может быть пустым.");
        }
    }

    int readInt(String prompt) {
        while (true) {
            try {
                return Integer.parseInt(readLine(prompt));
            } catch (NumberFormatException exception) {
                System.out.println("Введите целое число.");
            }
        }
    }

    long readId(String prompt) {
        while (true) {
            try {
                long id = Long.parseLong(readLine(prompt));
                if (id > 0) return id;
            } catch (NumberFormatException ignored) {
            }
            System.out.println("Введите положительный ID.");
        }
    }

    int readPositiveInt(String prompt) {
        return readIntInRange(prompt, 1, Integer.MAX_VALUE);
    }

    int readIntInRange(String prompt, int min, int max) {
        while (true) {
            int value = readInt(prompt);
            if (value >= min && value <= max) return value;
            System.out.println("Допустимый диапазон: " + min + "–" + max + ".");
        }
    }

    boolean confirm(String prompt) {
        while (true) {
            String answer = readLine(prompt + " (да/нет): ");
            if (answer.equalsIgnoreCase("да") || answer.equalsIgnoreCase("д")) return true;
            if (answer.equalsIgnoreCase("нет") || answer.equalsIgnoreCase("н")) return false;
            System.out.println("Ответьте да или нет.");
        }
    }

}
