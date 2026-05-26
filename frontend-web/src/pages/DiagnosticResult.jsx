import { useLocation, useNavigate } from "react-router-dom";
import styles from "../styles/DiagnosticResult.module.css";

function DiagnosticResult() {
  const navigate = useNavigate();
  const location = useLocation();

  const score = location.state?.score ?? 0;
  const total = location.state?.total ?? 10;
  const level = location.state?.level ?? "Básico";
  const answers = location.state?.answers ?? {};

  const percentage = Math.round((score / total) * 100);

  const levelConfig = {
    Básico: {
      icon: "🌱",
      emoji: "🎯",
      message: "¡Buen inicio! Vamos a reforzar juntos estos temas.",
      className: styles.basic,
    },

    Intermedio: {
      icon: "🔥",
      emoji: "🚀",
      message:
        "¡Muy bien! Tienes una base sólida para seguir aprendiendo.",
      className: styles.intermediate,
    },

    Avanzado: {
      icon: "⭐",
      emoji: "🏆",
      message:
        "¡Excelente! Dominas muy bien los conceptos matemáticos.",
      className: styles.advanced,
    },
  };

  const config = levelConfig[level] || levelConfig.Básico;

  return (
    <div className={styles.container}>
      <div className={`${styles.iconCircle} ${config.className}`}>
        {config.icon}
      </div>

      <h1>¡Evaluación Completada!</h1>

      <p className={styles.subtitle}>
        Has terminado tu evaluación inicial
      </p>

      <div className={styles.card}>
        <p className={styles.label}>Tu nivel detectado es</p>

        <div className={`${styles.levelBadge} ${config.className}`}>
          <span>{config.emoji}</span>
          {level.toUpperCase()}
        </div>

        <div className={styles.score}>
          <strong>{score}</strong>
          <span>/ {total}</span>
        </div>

        <p className={styles.scoreText}>
          respuestas correctas ({percentage}%)
        </p>

        <div className={styles.progressBar}>
          <div
            className={`${styles.progressFill} ${config.className}`}
            style={{ width: `${percentage}%` }}
          />
        </div>

        <div className={styles.messageBox}>
          {config.message}
        </div>
      </div>

      <button
        className={styles.startButton}
        onClick={() =>
          navigate("/diagnostic-review", {
            state: {
              score,
              total,
              level,
              answers,
            },
          })
        }
      >
        Ver mis resultados 📋
      </button>
    </div>
  );
}

export default DiagnosticResult;