import { useNavigate } from "react-router-dom";
import { modulesData } from "../data/modulesData";
import styles from "../styles/LearningPath.module.css";

function LearningPath() {
  const navigate = useNavigate();

  const studentLevel = "Básico";
  const totalProgress = 35;

  const handleOpenLevel = (moduleId, unlocked) => {
    if (!unlocked) return;
    if (!Number.isFinite(Number(moduleId))) {
      navigate("/student-dashboard");
      return;
    }
    navigate(`/module/${moduleId}`);
  };

  return (
    <div className={styles.container}>
      <header className={styles.hero}>
        <div>
          <span className={styles.badge}>Ruta de aprendizaje</span>
          <h1>Continúa aprendiendo matemáticas</h1>
          <p>
            Los temas están organizados por niveles. Avanza paso a paso y
            desbloquea nuevos retos según tu progreso.
          </p>
        </div>

        <div className={styles.levelCard}>
          <p>Nivel actual</p>
          <strong>🌱 {studentLevel}</strong>
          <div className={styles.progressBar}>
            <div
              className={styles.progressFill}
              style={{ width: `${totalProgress}%` }}
            />
          </div>
          <span>{totalProgress}% de avance general</span>
        </div>
      </header>

      <main className={styles.modulesGrid}>
        {modulesData.map((module) => (
          <section
            key={module.id}
            className={`${styles.moduleCard} ${
              module.active ? styles.activeModule : ""
            } ${!module.unlocked ? styles.lockedModule : ""}`}
          >
            <div className={styles.moduleHeader}>
              <div className={styles.moduleIcon}>{module.icon}</div>
              <div>
                <h2>{module.title}</h2>
                <p>{module.description}</p>
              </div>
            </div>

            <div className={styles.moduleProgress}>
              <span>{module.status}</span>
              <span>{module.progress}%</span>
            </div>

            <div className={styles.progressBar}>
              <div
                className={styles.progressFill}
                style={{ width: `${module.progress}%` }}
              />
            </div>

            <div className={styles.levelsList}>
              {module.levels.map((level) => (
                <button
                  key={level.id}
                  className={`${styles.levelItem} ${styles[level.color]} ${
                    !level.unlocked ? styles.lockedLevel : ""
                  }`}
                  onClick={() =>
                    handleOpenLevel(module.id, level.unlocked)
                  }
                  disabled={!level.unlocked}
                >
                  <div>
                    <strong>
                      {level.unlocked ? level.icon : "🔒"} {level.name}
                    </strong>
                    <small>
                      {Number.isFinite(Number(module.id))
                        ? level.status
                        : "Disponible desde el panel principal"}
                    </small>
                  </div>

                  <span>{level.progress}%</span>
                </button>
              ))}
            </div>
          </section>
        ))}
      </main>
    </div>
  );
}

export default LearningPath;
