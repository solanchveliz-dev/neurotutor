export const achievementImageMap = {
  DIAGNOSTIC_COMPLETED: "/assets/primer_paso.png",
  FIRST_THEORY_COMPLETED: "/assets/mente_curiosa.png",
  POINTS_100: "/assets/centena_dorada.png",
};

export const getAchievementImage = (code) => achievementImageMap[code] ?? null;

export const sortAchievementsByUnlockedAt = (items = []) =>
  [...items].sort((left, right) => {
    const rightTime = right?.unlocked_at ? new Date(right.unlocked_at).getTime() : 0;
    const leftTime = left?.unlocked_at ? new Date(left.unlocked_at).getTime() : 0;
    return rightTime - leftTime;
  });
