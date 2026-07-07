const normalizeStatus = (value) => String(value ?? "").trim().toUpperCase();

export const MODULE_DESTINATION_CACHE_KEY = "neurotutor.currentModuleId";

export const rememberCurrentModuleId = (moduleId) => {
  if (moduleId !== null && moduleId !== undefined && String(moduleId).trim()) {
    localStorage.setItem(MODULE_DESTINATION_CACHE_KEY, String(moduleId));
  }
};

export const getRememberedModuleId = () => localStorage.getItem(MODULE_DESTINATION_CACHE_KEY);

export const getModuleId = (module) =>
  module?.id
  ?? module?.module_id
  ?? module?.moduleId
  ?? module?.moduloId
  ?? module?.id_modulo
  ?? module?.topicId;

export const resolveCurrentModuleId = (modules = []) => {
  const validModules = Array.isArray(modules)
    ? modules.filter((module) => getModuleId(module) !== null && getModuleId(module) !== undefined)
    : [];

  const inProgress = validModules.find((module) => {
    const status = normalizeStatus(module.status ?? module.estado);
    const percentage = Number(module.progress_percentage ?? module.progress ?? module.progreso);
    return ["EN_CURSO", "IN_PROGRESS"].includes(status)
      || (Number.isFinite(percentage) && percentage > 0 && percentage < 100);
  });
  if (inProgress) return getModuleId(inProgress);

  const available = validModules.find((module) => {
    const status = normalizeStatus(module.status ?? module.estado);
    return ["DISPONIBLE", "AVAILABLE"].includes(status);
  });
  if (available) return getModuleId(available);

  const unlocked = validModules.find((module) => {
    const status = normalizeStatus(module.status ?? module.estado);
    return module.unlocked === true
      && !["BLOQUEADO", "LOCKED", "COMPLETADO", "COMPLETED"].includes(status);
  });
  if (unlocked) return getModuleId(unlocked);

  return getModuleId(validModules[0]);
};
