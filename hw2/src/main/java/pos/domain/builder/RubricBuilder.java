package pos.domain.builder;

import pos.domain.entity.Rubric;
import pos.domain.entity.Criterion;

import java.util.List;

public class RubricBuilder {
    private final Rubric draft;
    private boolean built = false;

    public RubricBuilder(Long id) {
        this.draft = new Rubric(id);
    }

    public RubricBuilder addCriterion(Long id, String name, int maxScore, int weight) {
        checkNotBuilt();
        draft.addCriterion(id, name, maxScore, weight);
        return this;
    }

    public List<Criterion> getCriteria() {
        checkNotBuilt();
        return draft.getCriterionList();
    }

    public void renameCriterion(Long id, String name) {
        checkNotBuilt();
        draft.renameCriterion(id, name);
    }

    public void changeCriterionWeight(Long id, int weight) {
        checkNotBuilt();
        draft.changeCriterionWeight(id, weight);
    }

    public void removeCriterion(Long id) {
        checkNotBuilt();
        draft.removeCriterion(id);
    }

    public Rubric build() {
        checkNotBuilt();

        if (draft.getCriterionList().isEmpty()) {
            throw new IllegalStateException("Добавьте хотя бы один критерий перед публикацией.");
        }

        built = true;
        return draft;
    }

    private void checkNotBuilt() {
        if (built) {
            throw new IllegalStateException("Рубрикатор уже собран.");
        }
    }
}
