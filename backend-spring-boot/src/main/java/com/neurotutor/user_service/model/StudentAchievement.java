package com.neurotutor.user_service.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "student_achievements",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_student_achievement_student_achievement",
                columnNames = {"student_id", "achievement_id"}
        )
)
public class StudentAchievement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Estudiante student;

    @ManyToOne(optional = false)
    @JoinColumn(name = "achievement_id", nullable = false)
    private Achievement achievement;

    @Column(name = "unlocked_at", nullable = false, updatable = false)
    private LocalDateTime unlockedAt;

    @PrePersist
    public void prePersist() {
        if (unlockedAt == null) unlockedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Estudiante getStudent() { return student; }
    public void setStudent(Estudiante student) { this.student = student; }
    public Achievement getAchievement() { return achievement; }
    public void setAchievement(Achievement achievement) { this.achievement = achievement; }
    public LocalDateTime getUnlockedAt() { return unlockedAt; }
    public void setUnlockedAt(LocalDateTime unlockedAt) { this.unlockedAt = unlockedAt; }
}
