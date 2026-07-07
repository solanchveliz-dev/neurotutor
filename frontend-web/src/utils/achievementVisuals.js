export const achievementImageMap = {
  DIAGNOSTIC_COMPLETED: "/assets/primer_paso.png",
  FIRST_THEORY_COMPLETED: "/assets/mente_curiosa.png",
  FIRST_PRACTICE_PASSED: "/assets/manos_a_la_practica.png",
  FIRST_EXAM_PASSED: "/assets/examen_superado.png",
  FIRST_MODULE_COMPLETED: "/assets/modulo_dominado.png",
  BASIC_LEVEL_COMPLETED: "/assets/nivel_basico.png",
  INTERMEDIATE_LEVEL_COMPLETED: "/assets/nivel_intermedio.png",
  ADVANCED_LEVEL_COMPLETED: "/assets/nivel_avanzado.png",
  POINTS_100: "/assets/centena_dorada.png",
};

const normalizeAchievementName = (value = "") =>
  value.toLowerCase().normalize("NFD").replace(/[\u0300-\u036f]/g, "");

export const getAchievementImage = (achievement) => {
  const code = typeof achievement === "string" ? achievement : achievement?.code;
  if (achievementImageMap[code]) return achievementImageMap[code];

  const normalizedTitle = normalizeAchievementName(
    typeof achievement === "string" ? "" : achievement?.title ?? achievement?.nombre
  );

  if (normalizedTitle.includes("modulo dominado")) return "/assets/modulo_dominado.png";
  if (normalizedTitle.includes("nivel basico completado")) return "/assets/nivel_basico.png";
  if (normalizedTitle.includes("nivel intermedio completado")) return "/assets/nivel_intermedio.png";
  if (normalizedTitle.includes("nivel avanzado completado")) return "/assets/nivel_avanzado.png";
  if (normalizedTitle.includes("examen superado")) return "/assets/examen_superado.png";
  if (normalizedTitle.includes("manos a la practica")) return "/assets/manos_a_la_practica.png";
  if (normalizedTitle.includes("centena")) return "/assets/centena_dorada.png";
  if (normalizedTitle.includes("mente curiosa")) return "/assets/mente_curiosa.png";
  if (normalizedTitle.includes("primer paso")) return "/assets/primer_paso.png";

  return null;
};

export const sortAchievementsByUnlockedAt = (items = []) =>
  [...items].sort((left, right) => {
    const rightTime = right?.unlocked_at ? new Date(right.unlocked_at).getTime() : 0;
    const leftTime = left?.unlocked_at ? new Date(left.unlocked_at).getTime() : 0;
    return rightTime - leftTime;
  });
