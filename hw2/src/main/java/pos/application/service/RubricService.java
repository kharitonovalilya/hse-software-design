package pos.application.service;

import pos.domain.entity.Criterion;
import pos.domain.entity.Evaluation;
import pos.domain.entity.Rubric;
import pos.domain.builder.RubricBuilder;
import pos.domain.port.IdGenerator;
import pos.domain.repository.EvaluationRepository;
import pos.domain.repository.RubricRepository;

import java.util.List;

public class RubricService {

    private final IdGenerator idGenerator;
    private final RubricRepository rubricRepository;
    private final EvaluationRepository evaluationRepository;

    private RubricBuilder draftBuilder;

    public RubricService(IdGenerator idGenerator, RubricRepository rubricRepository, EvaluationRepository evaluationRepository) {
        this.idGenerator = idGenerator;
        this.rubricRepository = rubricRepository;
        this.evaluationRepository = evaluationRepository;
    }

    public void startRubricCreation() {
        if (rubricRepository.get().isPresent()) {
            throw new IllegalStateException("Рубрикатор уже опубликован.");
        }
        if (draftBuilder != null) {
            throw new IllegalStateException("Черновик уже создан.");
        }
        draftBuilder = new RubricBuilder(idGenerator.nextId());
    }

    public void addDraftCriterion(String name, int maxScore, int weight) {
        if (draftBuilder == null) {
            throw new IllegalStateException("Сначала начните создание черновика.");
        }
        long id = idGenerator.nextId();
        draftBuilder.addCriterion(id, name, maxScore, weight);
    }

    public void cancelDraft() {
        getDraftBuilder();
        draftBuilder = null;
    }

    public List<Criterion> getDraftCriteria() {
        return getDraftBuilder().getCriteria();
    }

    public void renameDraftCriterion(Long id, String name) {
        getDraftBuilder().renameCriterion(id, name);
    }

    public void changeDraftCriterionWeight(Long id, int weight) {
        getDraftBuilder().changeCriterionWeight(id, weight);
    }

    public void removeDraftCriterion(Long id) {
        getDraftBuilder().removeCriterion(id);
    }

    private RubricBuilder getDraftBuilder() {
        if (draftBuilder == null) {
            throw new IllegalStateException("Сначала начните создание черновика.");
        }
        return draftBuilder;
    }

    public Rubric publishRubric() {
        if (rubricRepository.get().isPresent()) {
            throw new IllegalStateException("Рубрикатор уже опубликован.");
        }
        if (draftBuilder == null) {
            throw new IllegalStateException("Сначала начните создание черновика.");
        }
        Rubric rubric = draftBuilder.build();
        rubricRepository.save(rubric);
        draftBuilder = null;
        return rubric;
    }

    public Rubric getRubric() {
        return rubricRepository.get()
                .orElseThrow(() -> new IllegalStateException("Рубрикатор не создан."));
    }

    public void addCriterion(String name, int maxScore, int weight) {
        Rubric rubric = getRubric();
        long id = idGenerator.nextId();
        rubric.addCriterion(id, name, maxScore, weight);
        rubricRepository.save(rubric);
    }

    public void renameCriterion(Long id, String newName) {
        Rubric rubric = getRubric();
        rubric.renameCriterion(id, newName);
        rubricRepository.save(rubric);
    }

    public void changeCriterionWeight(Long id, int newWeight) {
        Rubric rubric = getRubric();
        rubric.changeCriterionWeight(id, newWeight);
        rubricRepository.save(rubric);
    }

    public void removeCriterion(Long id) {
        Rubric rubric = getRubric();
        rubric.getCriterion(id);
        rubric.removeCriterion(id);
        List<Evaluation> evaluations = evaluationRepository.findByCriterionId(id);
        for (Evaluation evaluation : evaluations) {
            evaluationRepository.deleteById(evaluation.getId());
        }
        rubricRepository.save(rubric);
    }

}
