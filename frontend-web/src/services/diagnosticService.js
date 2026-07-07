import api from "./api";

export const submitDiagnostic = async (studentId, respuestas) => {
  const response = await api.post("/api/diagnostic/submit", {
    studentId: String(studentId),
    respuestas,
  });

  return response.data;
};

export const getDiagnosticStudentProfile = async (studentId) => {
  const response = await api.get(`/api/diagnostic/student/${studentId}`);
  return response.data;
};

export const getDiagnosticQuestions = async () => {
  const response = await api.get("/api/diagnostic/questions");
  return response.data;
};

export const submitDiagnosticV2 = async (payload) => {
  const response = await api.post("/api/diagnostic/submit-v2", payload);
  return response.data;
};

export const getDiagnosticReview = async (attemptId) => {
  const response = await api.get(`/api/diagnostic/attempts/${attemptId}/review`);
  return response.data;
};

export const getLatestDiagnosticReview = async (studentId) => {
  const response = await api.get(`/api/students/${studentId}/diagnostic/latest-review`);
  return response.data;
};
