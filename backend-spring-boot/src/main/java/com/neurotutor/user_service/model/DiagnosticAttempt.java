package com.neurotutor.user_service.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "diagnostic_attempts")
public class DiagnosticAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Estudiante student;

    private int totalQuestions;
    private int correctAnswers;
    private int scorePercentage;
    private String assignedLevel;
    private LocalDateTime completedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Estudiante getStudent() {
        return student;
    }

    public void setStudent(Estudiante student) {
        this.student = student;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(int correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public int getScorePercentage() {
        return scorePercentage;
    }

    public void setScorePercentage(int scorePercentage) {
        this.scorePercentage = scorePercentage;
    }

    public String getAssignedLevel() {
        return assignedLevel;
    }

    public void setAssignedLevel(String assignedLevel) {
        this.assignedLevel = assignedLevel;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
}
