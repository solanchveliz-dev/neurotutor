import api from "./api";

export const getStudentProgress = async (studentId) => {
  const response = await api.get(`/api/students/${studentId}/progress`);
  return response.data;
};

export const getModuleProgress = async (studentId, moduloId) => {
  const response = await api.get(`/api/students/${studentId}/modules/${moduloId}/progress`);
  return response.data;
};

export const markTheoryCompleted = async (studentId, moduloId, payload = {}) => {
  const response = await api.post(`/api/students/${studentId}/modules/${moduloId}/theory/complete`, payload);
  return response.data;
};

export const submitPracticeAttempt = async (payload) => {
  const response = await api.post("/api/practice/attempts", payload);
  return response.data;
};
