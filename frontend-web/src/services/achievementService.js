import api from "./api";

export const getStudentAchievements = async (studentId) => {
  const response = await api.get(`/api/students/${studentId}/achievements`);
  return response.data;
};
