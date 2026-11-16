package pos.domain.entity;

import pos.domain.value.Score;

public class Criterion {
    private final Long id;
    private String name;
    private final int maxScore;
    private int weight;

    public Criterion(Long id, String name, int maxScore, int weight) {
        if (id == null) throw new IllegalArgumentException("ID не может быть пустым.");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Название не может быть пустым.");
        if (maxScore <= 0) throw new IllegalArgumentException("Максимальный балл должен быть положительным.");
        if (weight <= 0) throw new IllegalArgumentException("Вес должен быть положительным.");
        this.id = id;
        this.name = name.trim();
        this.maxScore = maxScore;
        this.weight = weight;
    }

    public void rename(String newName) {
        if (newName == null || newName.isBlank()) throw new IllegalArgumentException("Название не может быть пустым.");
        this.name = newName.trim();
    }

    public void changeWeight(int newWeight) {
        if (newWeight <= 0) throw new IllegalArgumentException("Вес должен быть положительным.");
        this.weight = newWeight;
    }

    public void validateScore(Score score) {
        if (score == null) throw new IllegalArgumentException("Балл не может быть пустым.");
        if (score.value() > maxScore) throw new IllegalArgumentException("Балл превышает максимум критерия.");
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getMaxScore() {
        return maxScore;
    }

    public int getWeight() {
        return weight;
    }
}
