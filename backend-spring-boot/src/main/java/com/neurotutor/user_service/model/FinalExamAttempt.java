package com.neurotutor.user_service.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "final_exam_attempts")
public class FinalExamAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Estudiante student;

    @ManyToOne(optional = false)
    @JoinColumn(name = "modulo_id", nullable = false)
    private Modulo modulo;

    private int totalQuestions;
    private int correctAnswers;
    private int scorePercentage;
    private boolean passed;
    private int pointsEarned;
    private LocalDateTime completedAt;

    public Long getId() { return id; }
    public Estudiante getStudent() { return student; }
    public void setStudent(Estudiante student) { this.student = student; }
    public Modulo getModulo() { return modulo; }
    public void setModulo(Modulo modulo) { this.modulo = modulo; }
    public int getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(int totalQuestions) { this.totalQuestions = totalQuestions; }
    public int getCorrectAnswers() { return correctAnswers; }
    public void setCorrectAnswers(int correctAnswers) { this.correctAnswers = correctAnswers; }
    public int getScorePercentage() { return scorePercentage; }
    public void setScorePercentage(int scorePercentage) { this.scorePercentage = scorePercentage; }
    public boolean isPassed() { return passed; }
    public void setPassed(boolean passed) { this.passed = passed; }
    public int getPointsEarned() { return pointsEarned; }
    public void setPointsEarned(int pointsEarned) { this.pointsEarned = pointsEarned; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
}
