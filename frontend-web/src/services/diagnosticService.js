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
