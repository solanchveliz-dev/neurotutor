package com.neurotutor.user_service.model;

import jakarta.persistence.*;

@Entity
@Table(name = "practice_answers")
public class PracticeAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "attempt_id", nullable = false)
    private PracticeAttempt attempt;

    @ManyToOne(optional = false)
    @JoinColumn(name = "exercise_id", nullable = false)
    private Ejercicio exercise;

    private int selectedAnswerIndex;
    private boolean correct;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PracticeAttempt getAttempt() {
        return attempt;
    }

    public void setAttempt(PracticeAttempt attempt) {
        this.attempt = attempt;
    }

    public Ejercicio getExercise() {
        return exercise;
    }

    public void setExercise(Ejercicio exercise) {
        this.exercise = exercise;
    }

    public int getSelectedAnswerIndex() {
        return selectedAnswerIndex;
    }

    public void setSelectedAnswerIndex(int selectedAnswerIndex) {
        this.selectedAnswerIndex = selectedAnswerIndex;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }
}
