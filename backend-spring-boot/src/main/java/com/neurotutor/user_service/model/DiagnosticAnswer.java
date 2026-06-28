package com.neurotutor.user_service.model;

import jakarta.persistence.*;

@Entity
@Table(name = "diagnostic_answers")
public class DiagnosticAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "attempt_id", nullable = false)
    private DiagnosticAttempt attempt;

    @ManyToOne(optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    private DiagnosticQuestion question;

    private int selectedAnswerIndex;
    private boolean correct;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DiagnosticAttempt getAttempt() {
        return attempt;
    }

    public void setAttempt(DiagnosticAttempt attempt) {
        this.attempt = attempt;
    }

    public DiagnosticQuestion getQuestion() {
        return question;
    }

    public void setQuestion(DiagnosticQuestion question) {
        this.question = question;
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
