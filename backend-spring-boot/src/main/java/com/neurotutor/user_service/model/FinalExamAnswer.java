package com.neurotutor.user_service.model;

import jakarta.persistence.*;

@Entity
@Table(name = "final_exam_answers")
public class FinalExamAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "attempt_id", nullable = false)
    private FinalExamAttempt attempt;

    @ManyToOne(optional = false)
    @JoinColumn(name = "exercise_id", nullable = false)
    private Ejercicio exercise;

    private int selectedAnswerIndex;
    private boolean correct;

    public Long getId() { return id; }
    public FinalExamAttempt getAttempt() { return attempt; }
    public void setAttempt(FinalExamAttempt attempt) { this.attempt = attempt; }
    public Ejercicio getExercise() { return exercise; }
    public void setExercise(Ejercicio exercise) { this.exercise = exercise; }
    public int getSelectedAnswerIndex() { return selectedAnswerIndex; }
    public void setSelectedAnswerIndex(int selectedAnswerIndex) { this.selectedAnswerIndex = selectedAnswerIndex; }
    public boolean isCorrect() { return correct; }
    public void setCorrect(boolean correct) { this.correct = correct; }
}
