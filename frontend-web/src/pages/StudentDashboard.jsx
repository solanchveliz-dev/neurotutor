import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { getStudentDashboard } from "../services/dashboardService";
import { clearAuthData, getStudentId } from "../utils/auth";
import styles from "../styles/StudentDashboard.module.css";

function StudentDashboard() {
  const navigate = useNavigate();
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [showUserMenu, setShowUserMenu] = useState(false);
  const [password, setPassword] = useState("");

  const fallbackStudent = {
    name: "Estudiante Demo",
    grade: "6to grado",
    section: "A",
    level: "Intermedio",
    points: 320,
  };

  const fallbackModules = [
    {
      id: 1,
      title: "Problemas de cantidad",
      description: "Operaciones, números y situaciones de conteo.",
      progress: 6,
      total: 10,
      unlocked: true,
      active: true,
    },
    {
      id: 2,
      title: "Regularidad, equivalencia y cambio",
      description: "Patrones, secuencias y relaciones.",
      progress: 2,
      total: 10,
      unlocked: true,
      active: false,
    },
    {
      id: 3,
      title: "Forma, movimiento y localización",
      description: "Figuras, medidas y ubicación espacial.",
      progress: 0,
      total: 10,
      unlocked: false,
      active: false,
    },
    {
      id: 4,
      title: "Gestión de datos e incertidumbre",
      description: "Tablas, gráficos y probabilidades.",
      progress: 0,
      total: 10,
      unlocked: false,
      active: false,
    },
  ];

  const [student, setStudent] = useState(fallbackStudent);
  const [modules, setModules] = useState(fallbackModules);

  useEffect(() => {
    const studentId = getStudentId();

    if (!studentId) return;

    getStudentDashboard(studentId)
      .then((profile) => {
        const [grade = "", section = ""] = (profile.gradoSeccion || "").split(" ");

        setStudent({
          name: profile.nombreCompleto || fallbackStudent.name,
          grade: grade || fallbackStudent.grade,
          section: section || fallbackStudent.section,
          level: profile.nivelActual || fallbackStudent.level,
          points: profile.puntosTotales ?? fallbackStudent.points,
        });

        if (Array.isArray(profile.modulos) && profile.modulos.length > 0) {
          setModules(
            profile.modulos.map((module, index) => ({
              id: module.id,
              title: module.titulo,
              description: "Modulo asignado segun tu nivel diagnostico.",
              progress: module.ejerciciosCompletados ?? 0,
              total: module.ejerciciosTotales ?? 0,
              unlocked: module.estado !== "BLOQUEADO",
              active: module.estado === "EN_CURSO" || index === 0,
            }))
          );
        }
      })
      .catch(() => {
        setStudent(fallbackStudent);
        setModules(fallbackModules);
      });
  }, []);

  const handleLogout = () => {
    clearAuthData();
    navigate("/login", { replace: true });
  };

  const handleDeleteAccount = () => {
    if (!password.trim()) return;

    clearAuthData();
    setShowDeleteModal(false);
    navigate("/login", { replace: true });
  };

  const handleOpenModule = (module) => {
    if (!module.unlocked) return;

    if (module.id === 1) {
      navigate("/module/fracciones/fracciones-basico");
    } else if (module.id === 2) {
      navigate("/module/decimales/decimales-basico");
    } else {
      navigate("/learning-path");
    }
  };

  return (
    <div className={styles.container}>
      <div className={styles.topBar}>
        <div className={styles.brandSection}>
          <div className={styles.logoMark}>NT</div>
          <div>
            <p className={styles.brandLabel}>NeuroTutor</p>
            <span className={styles.brandTag}>Panel de estudios</span>
          </div>
        </div>

        <nav className={styles.menuLinks}>
          <button
            className={styles.menuLink}
            onClick={() => navigate("/student-dashboard")}
          >
            Inicio
          </button>

          <button
            className={styles.menuLink}
            onClick={() => navigate("/learning-path")}
          >
            Módulos
          </button>

          <button
            className={styles.menuLink}
            onClick={() => navigate("/learning-path")}
          >
            Progreso
          </button>
        </nav>

        <div className={styles.userSection}>
          <button
            className={styles.profileButton}
            onClick={() => setShowUserMenu(!showUserMenu)}
          >
            <div className={styles.profileAvatar}>A</div>
            <div className={styles.profileInfo}>
              <span>{student.name}</span>
              <small>Estudiante</small>
            </div>
          </button>

          {showUserMenu && (
            <div className={styles.userDropdown}>
              <button className={styles.dropdownItem} onClick={handleLogout}>
                Cerrar sesión
              </button>

              <button
                className={styles.dropdownItem}
                onClick={() => {
                  setShowDeleteModal(true);
                  setShowUserMenu(false);
                }}
              >
                Eliminar cuenta
              </button>
            </div>
          )}
        </div>
      </div>

      <header className={styles.header}>
        <div>
          <h1>Panel de Aprendizaje</h1>
          <p>Bienvenido/a, {student.name}</p>
        </div>
      </header>

      <section className={styles.summaryGrid}>
        <div className={styles.profileCard}>
          <h2>{student.name}</h2>
          <p>
            {student.grade} · Sección {student.section}
          </p>
        </div>

        <div className={styles.infoCard}>
          <span>Nivel actual</span>
          <strong>{student.level}</strong>
        </div>

        <div className={styles.infoCard}>
          <span>Puntos acumulados</span>
          <strong>{student.points}</strong>
        </div>
      </section>

      <section className={styles.modulesSection}>
        <h2>Módulos disponibles</h2>

        <div className={styles.modulesGrid}>
          {modules.map((module) => {
            const percentage = Math.round(
              (module.progress / module.total) * 100
            );

            return (
              <article
                key={module.id}
                className={`${styles.moduleCard} ${
                  module.active ? styles.activeModule : ""
                } ${!module.unlocked ? styles.lockedModule : ""}`}
              >
                <div className={styles.moduleHeader}>
                  <h3>
                    {module.unlocked ? module.title : `🔒 ${module.title}`}
                  </h3>

                  {module.active && <span>En curso</span>}
                </div>

                <p>{module.description}</p>

                <div className={styles.progressInfo}>
                  <span>
                    {module.progress}/{module.total} ejercicios
                  </span>
                  <span>{percentage}%</span>
                </div>

                <div className={styles.progressBar}>
                  <div
                    className={styles.progressFill}
                    style={{ width: `${percentage}%` }}
                  />
                </div>

                <button
                  disabled={!module.unlocked}
                  onClick={() => handleOpenModule(module)}
                >
                  {module.unlocked ? "Continuar" : "Bloqueado"}
                </button>
              </article>
            );
          })}
        </div>
      </section>

      <section className={styles.profileActions}>
        <h2>Perfil del estudiante</h2>

        <button
          className={styles.deleteButton}
          onClick={() => setShowDeleteModal(true)}
        >
          Eliminar cuenta
        </button>
      </section>

      {showDeleteModal && (
        <div className={styles.modalOverlay}>
          <div className={styles.modal}>
            <h2>¿Estás seguro de eliminar tu cuenta?</h2>

            <p>
              Esta acción eliminará tu acceso al sistema. Ingresa tu contraseña
              para confirmar.
            </p>

            <input
              type="password"
              placeholder="Ingresa tu contraseña"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
            />

            <div className={styles.modalActions}>
              <button onClick={() => setShowDeleteModal(false)}>
                Cancelar
              </button>

              <button
                className={styles.confirmDelete}
                onClick={handleDeleteAccount}
              >
                Confirmar eliminación
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default StudentDashboard;
