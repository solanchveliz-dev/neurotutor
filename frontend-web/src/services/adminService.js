const ADMIN_API_BASE_URL = "http://127.0.0.1:8000/api/admin";

const requestAdminResource = async (path) => {
  const response = await fetch(`${ADMIN_API_BASE_URL}${path}`);

  if (!response.ok) {
    throw new Error("No se pudo obtener la informacion administrativa.");
  }

  return response.json();
};

export const getAdminSummary = () => requestAdminResource("/summary/");

export const getAdminStudents = () => requestAdminResource("/students/");

export const getAdminStudentById = (id) => requestAdminResource(`/students/${id}/`);
