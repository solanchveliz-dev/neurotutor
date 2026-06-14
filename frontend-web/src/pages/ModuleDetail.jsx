import { useEffect, useState } from "react";
import { useLocation, useNavigate, useParams } from "react-router-dom";
import { modulesData } from "../data/modulesData";
import { getLearningContent } from "../services/learningService";
import styles from "../styles/ModuleDetail.module.css";

function ModuleDetail() {
  const navigate = useNavigate();
  const location = useLocation();
  const { moduleId } = useParams();
  const [content, setContent] = useState(null);
  const [isUsingFallback, setIsUsingFallback] = useState(false);
  const dashboardModule = location.state?.module;
  const numericFallbackMap = {
    1: "fracciones",
    2: "decimales",
    3: "porcentajes",
  };
  const fallbackId = numericFallbackMap[moduleId] ?? moduleId;

  const fallbackModule =
    modulesData.find((item) => String(item.id) === String(fallbackId)) ??
    modulesData[0];
  const fallbackLevel =
    fallbackModule?.levels.find((item) => item.unlocked) ??
    fallbackModule?.levels[0];

  useEffect(() => {
    setIsUsingFallback(false);

    getLearningContent(moduleId)
      .then((data) => {
        setContent(data);
        setIsUsingFallback(false);
      })
      .catch(() => {
        setContent(null);
        setIsUsingFallback(true);
      });
  }, [moduleId]);

  const module = fallbackModule
    ? {
        ...fallbackModule,
        id: moduleId,
        title: content?.titulo ?? dashboardModule?.title ?? fallbackModule.title,
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
            {isUsingFallback && (
              <p>Contenido temporal disponible mientras se sincroniza con el servidor.</p>
            )}
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
