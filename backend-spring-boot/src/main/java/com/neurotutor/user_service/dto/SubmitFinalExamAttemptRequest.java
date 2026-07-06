package com.neurotutor.user_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class SubmitFinalExamAttemptRequest {
    private Long studentId;
    private Long moduloId;
    private List<AnswerRequest> answers;

    @JsonProperty("student_id")
    public Long getStudentId() { return studentId; }
    @JsonProperty("student_id")
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    @JsonProperty("modulo_id")
    public Long getModuloId() { return moduloId; }
    @JsonProperty("modulo_id")
    public void setModuloId(Long moduloId) { this.moduloId = moduloId; }
    public List<AnswerRequest> getAnswers() { return answers; }
    public void setAnswers(List<AnswerRequest> answers) { this.answers = answers; }

    public static class AnswerRequest {
        private Long questionId;
        private Integer selectedAnswerIndex;

        @JsonProperty("question_id")
        public Long getQuestionId() { return questionId; }
        @JsonProperty("question_id")
        public void setQuestionId(Long questionId) { this.questionId = questionId; }
        @JsonProperty("selected_answer_index")
        public Integer getSelectedAnswerIndex() { return selectedAnswerIndex; }
        @JsonProperty("selected_answer_index")
        public void setSelectedAnswerIndex(Integer selectedAnswerIndex) { this.selectedAnswerIndex = selectedAnswerIndex; }
    }
}
