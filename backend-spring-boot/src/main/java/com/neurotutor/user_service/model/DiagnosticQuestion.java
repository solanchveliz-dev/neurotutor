package com.neurotutor.user_service.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "diagnostic_questions")
public class DiagnosticQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String textBeforeImage;

    @Column(columnDefinition = "TEXT")
    private String textAfterImage;

    private String imageUrl;

    @ElementCollection
    @CollectionTable(
            name = "diagnostic_question_options",
            joinColumns = @JoinColumn(name = "question_id")
    )
    @Column(name = "option_text")
    private List<String> options = new ArrayList<>();

    private int correctAnswerIndex;
    private String topic;

    @Column(columnDefinition = "TEXT")
    private String explanation;

    private int orderNumber;
    private boolean active = true;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTextBeforeImage() {
        return textBeforeImage;
    }

    public void setTextBeforeImage(String textBeforeImage) {
        this.textBeforeImage = textBeforeImage;
    }

    public String getTextAfterImage() {
        return textAfterImage;
    }

    public void setTextAfterImage(String textAfterImage) {
        this.textAfterImage = textAfterImage;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public int getCorrectAnswerIndex() {
        return correctAnswerIndex;
    }

    public void setCorrectAnswerIndex(int correctAnswerIndex) {
        this.correctAnswerIndex = correctAnswerIndex;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
