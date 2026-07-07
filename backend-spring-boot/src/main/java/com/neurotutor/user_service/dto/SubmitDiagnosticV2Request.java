package com.neurotutor.user_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class SubmitDiagnosticV2Request {
    private Long studentId;
    private List<DiagnosticAnswerRequest> answers = new ArrayList<>();

    public SubmitDiagnosticV2Request() {
    }

    @JsonProperty("student_id")
    public Long getStudentId() {
        return studentId;
    }

    @JsonProperty("student_id")
    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public List<DiagnosticAnswerRequest> getAnswers() {
        return answers;
    }

    public void setAnswers(List<DiagnosticAnswerRequest> answers) {
        this.answers = answers;
    }

    public static class DiagnosticAnswerRequest {
        private Long questionId;
        private int selectedAnswerIndex;

        public DiagnosticAnswerRequest() {
        }

        @JsonProperty("question_id")
        public Long getQuestionId() {
            return questionId;
        }

        @JsonProperty("question_id")
        public void setQuestionId(Long questionId) {
            this.questionId = questionId;
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
