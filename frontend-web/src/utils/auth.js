export const getStoredUser = () => {
  try {
    const user = localStorage.getItem("user");
    return user ? JSON.parse(user) : null;
  } catch {
    return null;
  }
};

export const getStudentId = () => {
  const user = getStoredUser();
  return user?.id ?? null;
};

export const saveAuthData = (data) => {
  if (!data) return;

  if (data.token) {
    localStorage.setItem("token", data.token);
  }

  localStorage.setItem("user", JSON.stringify(data));
};

export const clearAuthData = () => {
  localStorage.removeItem("token");
  localStorage.removeItem("user");
  localStorage.removeItem("admin_access_token");
  localStorage.removeItem("admin_refresh_token");
  localStorage.removeItem("admin_user");
  localStorage.removeItem("access_token");
  localStorage.removeItem("refresh_token");
  localStorage.removeItem("adminToken");
  localStorage.removeItem("diagnosticResult");
  localStorage.removeItem("neurotutor.currentModuleId");
};
