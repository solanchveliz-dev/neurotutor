import api from "./api";

export const getStudentProfile = async (studentId) => {
  const response = await api.get(`/api/students/${studentId}/profile`);
  return response.data;
};

export const updateStudentProfile = async (studentId, payload) => {
  const response = await api.put(`/api/students/${studentId}/profile`, payload);
  return response.data;
};
