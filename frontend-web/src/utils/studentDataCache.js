const CACHE_PREFIX = "neurotutor.studentData";
const DEFAULT_TTL = 5 * 60 * 1000;

const getKey = (studentId, resource) => `${CACHE_PREFIX}.${studentId}.${resource}`;

export const getCachedStudentData = (studentId, resource, ttl = DEFAULT_TTL) => {
  if (!studentId) return null;
  try {
    const cached = JSON.parse(localStorage.getItem(getKey(studentId, resource)));
    if (!cached || Date.now() - cached.savedAt > ttl) return null;
    return cached.data ?? null;
  } catch {
    return null;
  }
};

export const setCachedStudentData = (studentId, resource, data) => {
  if (!studentId || data === undefined) return;
  try {
    localStorage.setItem(getKey(studentId, resource), JSON.stringify({ data, savedAt: Date.now() }));
  } catch {
    // Cache failure must not affect real network data.
  }
};

export const removeCachedStudentData = (studentId, resource) => {
  if (studentId) localStorage.removeItem(getKey(studentId, resource));
};
