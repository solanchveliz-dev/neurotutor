import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { modulesData } from "../data/modulesData";
import { getLearningContent } from "../services/learningService";
import styles from "../styles/ModuleDetail.module.css";

function ModuleDetail() {
  const navigate = useNavigate();
  const { moduleId } = useParams();
  const [content, setContent] = useState(null);

  const fallbackModule =
    modulesData.find((item) => String(item.id) === String(moduleId)) ??
    modulesData[0];
  const fallbackLevel =
    fallbackModule?.levels.find((item) => item.unlocked) ??
    fallbackModule?.levels[0];

  useEffect(() => {
    getLearningContent(moduleId)
      .then(setContent)
      .catch(() => setContent(null));
  }, [moduleId]);

  const module = fallbackModule
    ? {
        ...fallbackModule,
        id: moduleId,
        title: content?.titulo ?? fallbackModule.title,
      }
    : null;
  const level = fallbackLevel;

  if (!module || !level) {
    return (
      <div className={styles.container}>
        <div className={styles.card}>
          <h1>Modulo no encontrado</h1>
          <button onClick={() => navigate("/learning-path")}>
            Volver a la ruta
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className={styles.container}>
      <button
        className={styles.backButton}
        onClick={() => navigate("/student-dashboard")}
      >
        Volver al panel
      </button>

      <section className={styles.card}>
        <div className={styles.header}>
          <div className={styles.icon}>{module.icon}</div>
          <div>
            <span className={`${styles.levelBadge} ${styles[level.color]}`}>
              {level.icon} Nivel {level.name}
            </span>
            <h1>{module.title}</h1>
            <p>{level.description}</p>
          </div>
        </div>

        <div className={styles.progressBlock}>
          <div>
            <strong>Progreso del nivel</strong>
            <span>{level.progress}% completado</span>
          </div>

          <div className={styles.progressBar}>
            <div
              className={styles.progressFill}
              style={{ width: `${level.progress}%` }}
            />
          </div>
        </div>

        <div className={styles.contentGrid}>
          <div className={styles.section}>
            <h2>Teoria</h2>
            {content?.teoriaHtml ? (
              <div dangerouslySetInnerHTML={{ __html: content.teoriaHtml }} />
            ) : (
              level.theory.map((item, index) => <p key={index}>{item}</p>)
            )}
          </div>

          <div className={styles.section}>
            <h2>Objetivos</h2>
            <ul>
              {level.objectives.map((objective, index) => (
                <li key={index}>{objective}</li>
              ))}
            </ul>
          </div>
        </div>

        <div className={styles.actions}>
          <button onClick={() => navigate(`/practice/${module.id}`)}>
            Comenzar ejercicios
          </button>

          <button
            className={styles.secondaryButton}
            onClick={() => navigate(`/final-exam/${module.id}`)}
          >
            Ir al examen final
          </button>
        </div>
      </section>
    </div>
  );
}

export default ModuleDetail;
