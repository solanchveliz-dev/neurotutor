package com.neurotutor.user_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class DiagnosticReviewResponse {
    private Long attemptId;
    private Long studentId;
    private String assignedLevel;
    private int correctAnswers;
    private int totalQuestions;
    private int scorePercentage;
    private List<QuestionReview> questions;

    public DiagnosticReviewResponse(Long attemptId, Long studentId, String assignedLevel,
                                    int correctAnswers, int totalQuestions,
                                    int scorePercentage, List<QuestionReview> questions) {
        this.attemptId = attemptId;
        this.studentId = studentId;
        this.assignedLevel = assignedLevel;
        this.correctAnswers = correctAnswers;
        this.totalQuestions = totalQuestions;
        this.scorePercentage = scorePercentage;
        this.questions = questions;
    }

    @JsonProperty("attempt_id")
    public Long getAttemptId() {
        return attemptId;
    }

    @JsonProperty("student_id")
    public Long getStudentId() {
        return studentId;
    }

    @JsonProperty("assigned_level")
    public String getAssignedLevel() {
        return assignedLevel;
    }

    @JsonProperty("correct_answers")
    public int getCorrectAnswers() {
        return correctAnswers;
    }

    @JsonProperty("total_questions")
    public int getTotalQuestions() {
        return totalQuestions;
    }

    @JsonProperty("score_percentage")
    public int getScorePercentage() {
        return scorePercentage;
    }

    public List<QuestionReview> getQuestions() {
        return questions;
    }

    public static class QuestionReview {
        private Long questionId;
        private String topic;
        private String textBeforeImage;
        private String textAfterImage;
        private String imageUrl;
        private List<String> options;
        private int selectedAnswerIndex;
        private int correctAnswerIndex;
        private boolean correct;
        private String explanation;

        public QuestionReview(Long questionId, String topic, String textBeforeImage,
                              String textAfterImage, String imageUrl, List<String> options,
                              int selectedAnswerIndex, int correctAnswerIndex,
                              boolean correct, String explanation) {
            this.questionId = questionId;
            this.topic = topic;
            this.textBeforeImage = textBeforeImage;
            this.textAfterImage = textAfterImage;
            this.imageUrl = imageUrl;
            this.options = options;
            this.selectedAnswerIndex = selectedAnswerIndex;
            this.correctAnswerIndex = correctAnswerIndex;
            this.correct = correct;
            this.explanation = explanation;
        }

        @JsonProperty("question_id")
        public Long getQuestionId() {
            return questionId;
        }

        public String getTopic() {
            return topic;
        }

        @JsonProperty("text_before_image")
        public String getTextBeforeImage() {
            return textBeforeImage;
        }

        @JsonProperty("text_after_image")
        public String getTextAfterImage() {
            return textAfterImage;
        }

        @JsonProperty("image_url")
        public String getImageUrl() {
            return imageUrl;
        }

        public List<String> getOptions() {
            return options;
        }

        @JsonProperty("selected_answer_index")
        public int getSelectedAnswerIndex() {
            return selectedAnswerIndex;
        }

        @JsonProperty("correct_answer_index")
        public int getCorrectAnswerIndex() {
            return correctAnswerIndex;
        }

        public boolean isCorrect() {
            return correct;
        }

        public String getExplanation() {
            return explanation;
        }
    }
}
