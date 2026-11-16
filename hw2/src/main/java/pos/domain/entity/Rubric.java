package pos.domain.entity;

import java.util.ArrayList;
import java.util.List;

public class Rubric {
    private final Long id;
    private final List<Criterion> criterionList = new ArrayList<>();

    public Rubric(Long id) {
        if (id == null) throw new IllegalArgumentException("ID не может быть пустым.");
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public Criterion addCriterion(Long id, String name, int maxScore, int weight) {
        Criterion criterion = new Criterion(id, name, maxScore, weight);
        if (id.equals(this.id) || hasCriterion(id)) throw new IllegalArgumentException("ID критерия уже занят.");
        checkNameAvailable(name, null);
        criterionList.add(criterion);
        return copy(criterion);
    }

    private boolean hasCriterion(Long id) {
        return criterionList.stream().anyMatch(c -> c.getId().equals(id));
    }

    public void removeCriterion(Long id) {
        criterionList.remove(findById(id));
    }

    public Criterion getCriterion(Long id) {
        return copy(findById(id));
    }

    public void validateScore(Long criterionId, pos.domain.value.Score score) {
        findById(criterionId).validateScore(score);
    }

    public void renameCriterion(Long id, String newName) {
        Criterion criterion = findById(id);
        checkNameAvailable(newName, id);
        criterion.rename(newName);
    }

    public void changeCriterionWeight(Long id, int newWeight) {
        findById(id).changeWeight(newWeight);
    }

    public List<Criterion> getCriterionList() {
        return criterionList.stream().map(Rubric::copy).toList();
    }

    private Criterion findById(Long id) {
        return criterionList.stream().filter(c -> c.getId().equals(id)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Критерий не найден: " + id));
    }

    private void checkNameAvailable(String name, Long exceptId) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Название не может быть пустым.");
        String normalized = name.trim();
        if (criterionList.stream().anyMatch(c -> !c.getId().equals(exceptId)
                && c.getName().equalsIgnoreCase(normalized))) {
            throw new IllegalArgumentException("Критерий с таким названием уже есть.");
        }
    }

    private static Criterion copy(Criterion c) {
        return new Criterion(c.getId(), c.getName(), c.getMaxScore(), c.getWeight());
    }
}
