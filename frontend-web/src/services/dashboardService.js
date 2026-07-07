import api from "./api";

const pendingDashboardRequests = new Map();

export const getStudentDashboard = async (studentId) => {
  const key = String(studentId);
  if (pendingDashboardRequests.has(key)) return pendingDashboardRequests.get(key);

  const request = api.get(`/api/dashboard/student/${studentId}`)
    .then((response) => response.data)
    .finally(() => pendingDashboardRequests.delete(key));
  pendingDashboardRequests.set(key, request);
  return request;
};
