import api from "./api";

export const getStudentDashboard = async (studentId) => {
  const response = await api.get(`/api/dashboard/student/${studentId}`);
  return response.data;
};
