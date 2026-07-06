import api from "./api";

export const askNeoTutor = async ({ studentId, moduleId, levelId, lessonId, exerciseId, currentScreen, action, message, question, context }) => {
  const promptText = (message || question || "").trim();
  const response = await api.post("/api/ai/tutor", {
    studentId,
    moduleId,
    levelId,
    lessonId,
    exerciseId,
    currentScreen,
    action,
    message: promptText,
    question: promptText,
    context,
  });

  return response.data;
};
