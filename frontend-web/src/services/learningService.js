import api from "./api";

export const getLearningContent = async (moduloId) => {
  const response = await api.get(`/api/learning/content/${moduloId}`);
  return response.data;
};

export const getFinalExam = async (moduloId) => {
  const response = await api.get(`/api/learning/exam/${moduloId}`);
  return response.data;
};

export const getExamPassed = async (studentId, moduloId) => {
  const response = await api.get("/api/learning/exam-passed", {
    params: { studentId, moduloId },
  });
  return response.data;
};

export const submitExamV2 = async ({ studentId, moduloId, level, score }) => {
  const response = await api.post("/api/learning/submit-exam-v2", {
    studentId,
    moduloId,
    level,
    score,
  });
  return response.data;
};
