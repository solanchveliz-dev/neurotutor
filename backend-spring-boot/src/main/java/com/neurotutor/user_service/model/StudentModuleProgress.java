package com.neurotutor.user_service.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "student_module_progress",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_student_module_progress_student_modulo",
                        columnNames = {"student_id", "modulo_id"}
                )
        }
)
public class StudentModuleProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Estudiante student;

    @ManyToOne(optional = false)
    @JoinColumn(name = "modulo_id", nullable = false)
    private Modulo modulo;

    private boolean theoryCompleted = false;
    private boolean practiceCompleted = false;
    private boolean examPassed = false;
    private int practiceCompletedCount = 0;
    private int practiceTotalCount = 0;
    private int examBestScore = 0;
    private int progressPercentage = 0;
    private LocalDateTime lastActivityAt;
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

    public Modulo getModulo() {
        return modulo;
    }

    public void setModulo(Modulo modulo) {
        this.modulo = modulo;
    }

    public boolean isTheoryCompleted() {
        return theoryCompleted;
    }

    public void setTheoryCompleted(boolean theoryCompleted) {
        this.theoryCompleted = theoryCompleted;
    }

    public boolean isPracticeCompleted() {
        return practiceCompleted;
    }

    public void setPracticeCompleted(boolean practiceCompleted) {
        this.practiceCompleted = practiceCompleted;
    }

    public boolean isExamPassed() {
        return examPassed;
    }

    public void setExamPassed(boolean examPassed) {
        this.examPassed = examPassed;
    }

    public int getPracticeCompletedCount() {
        return practiceCompletedCount;
    }

    public void setPracticeCompletedCount(int practiceCompletedCount) {
        this.practiceCompletedCount = practiceCompletedCount;
    }

    public int getPracticeTotalCount() {
        return practiceTotalCount;
    }

    public void setPracticeTotalCount(int practiceTotalCount) {
        this.practiceTotalCount = practiceTotalCount;
    }

    public int getExamBestScore() {
        return examBestScore;
    }

    public void setExamBestScore(int examBestScore) {
        this.examBestScore = examBestScore;
    }

    public int getProgressPercentage() {
        return progressPercentage;
    }

    public void setProgressPercentage(int progressPercentage) {
        this.progressPercentage = progressPercentage;
    }

    public LocalDateTime getLastActivityAt() {
        return lastActivityAt;
    }

    public void setLastActivityAt(LocalDateTime lastActivityAt) {
        this.lastActivityAt = lastActivityAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
}
