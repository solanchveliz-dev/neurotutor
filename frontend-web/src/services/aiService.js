import api from "./api";

export const askNeoTutor = async ({ studentId, moduleId, question, context }) => {
  const response = await api.post("/api/ai/tutor", {
    studentId,
    moduleId,
    question,
    context,
  });

  return response.data;
};
