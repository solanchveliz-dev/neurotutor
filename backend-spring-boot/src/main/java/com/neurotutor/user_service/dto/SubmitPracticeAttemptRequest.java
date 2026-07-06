package com.neurotutor.user_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class SubmitPracticeAttemptRequest {
    private Long studentId;
    private Long moduloId;
    private List<PracticeAnswerRequest> answers = new ArrayList<>();

    public SubmitPracticeAttemptRequest() {
    }

    @JsonProperty("student_id")
    public Long getStudentId() {
        return studentId;
    }

    @JsonProperty("student_id")
    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    @JsonProperty("modulo_id")
    public Long getModuloId() {
        return moduloId;
    }

    @JsonProperty("modulo_id")
    public void setModuloId(Long moduloId) {
        this.moduloId = moduloId;
    }

    public List<PracticeAnswerRequest> getAnswers() {
        return answers;
    }

    public void setAnswers(List<PracticeAnswerRequest> answers) {
        this.answers = answers;
    }

    public static class PracticeAnswerRequest {
        private Long exerciseId;
        private int selectedAnswerIndex;

        public PracticeAnswerRequest() {
        }

        @JsonProperty("exercise_id")
        public Long getExerciseId() {
            return exerciseId;
        }

        @JsonProperty("exercise_id")
        public void setExerciseId(Long exerciseId) {
            this.exerciseId = exerciseId;
        }

        @JsonProperty("selected_answer_index")
        public int getSelectedAnswerIndex() {
            return selectedAnswerIndex;
        }

        @JsonProperty("selected_answer_index")
        public void setSelectedAnswerIndex(int selectedAnswerIndex) {
            this.selectedAnswerIndex = selectedAnswerIndex;
        }
    }
}
