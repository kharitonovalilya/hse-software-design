package pos.domain.entity;

import pos.domain.value.Score;

public class Evaluation {
    private final Long id;
    private final Long studentId;
    private final Long criterionId;
    private Score score;
    private String comment;

    public Evaluation(Long id, Long studentId, Long criterionId, Score score, String comment, Rubric rubric) {
        if (id == null || studentId == null || criterionId == null) {
            throw new IllegalArgumentException("ID не могут быть пустыми.");
        }
        if (rubric == null) throw new IllegalArgumentException("Рубрикатор не может быть пустым.");
        rubric.validateScore(criterionId, score);
        this.id = id;
        this.studentId = studentId;
        this.criterionId = criterionId;
        this.score = score;
        this.comment = comment;
    }

    public void reviseScore(Score newScore, Rubric rubric) {
        if (rubric == null) throw new IllegalArgumentException("Рубрикатор не может быть пустым.");
        rubric.validateScore(criterionId, newScore);
        score = newScore;
    }

    public void changeComment(String newComment) {
        comment = newComment;
    }

    public Long getId() {
        return id;
    }

    public Long getStudentId() {
        return studentId;
    }

    public Long getCriterionId() {
        return criterionId;
    }

    public Score getScore() {
        return score;
    }

    public String getComment() {
        return comment;
    }
}
