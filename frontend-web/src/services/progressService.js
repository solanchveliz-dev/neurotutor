import api from "./api";

const pendingProgressRequests = new Map();

export const getStudentProgress = async (studentId) => {
  const key = String(studentId);
  if (pendingProgressRequests.has(key)) return pendingProgressRequests.get(key);

  const request = api.get(`/api/students/${studentId}/progress`, { timeout: 9000 })
    .then((response) => response.data)
    .finally(() => pendingProgressRequests.delete(key));
  pendingProgressRequests.set(key, request);
  return request;
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
