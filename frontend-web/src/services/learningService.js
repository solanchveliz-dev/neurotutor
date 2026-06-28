import api from "./api";

export const getLearningContent = async (moduloId) => {
  const response = await api.get(`/api/learning/content/${moduloId}`);
  return response.data;
};

export const getModuleDetails = async (moduleId) => {
  const response = await api.get(`/api/learning/modules/${moduleId}/details`);
  return response.data;
};

export const getLevelDetails = async (levelId) => {
  const response = await api.get(`/api/learning/levels/${levelId}`);
  return response.data;
};

export const getTheoryLessons = async (levelId) => {
  const response = await api.get(`/api/learning/modules/${levelId}/lessons`);
  return response.data;
};

export const getTheoryLesson = async (lessonId) => {
  const response = await api.get(`/api/learning/lessons/${lessonId}`);
  return response.data;
};

export const getTopicRuta = async (moduloId, studentId) => {
  const response = await api.get(`/api/learning/topic-ruta/${moduloId}`, {
    params: { studentId },
  });
  return response.data;
};

export const getFinalExam = async (moduloId) => {
  const response = await api.get(`/api/learning/exam/${moduloId}`);
  return response.data;
};

export const submitFinalExamAttempt = async (payload) => {
  const response = await api.post("/api/learning/exam-attempts", payload);
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
