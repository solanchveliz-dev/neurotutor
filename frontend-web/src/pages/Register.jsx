import { useState } from "react"
import styles from "./Register.module.css"

function Register() {
  const [role, setRole] = useState("Estudiante")
  const [mode, setMode] = useState("Registrarse")
  const [formData, setFormData] = useState({
    nombre: "",
    email: "",
    grado: "",
    seccion: "",
    password: "",
    confirmPassword: ""
  })

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    })
  }

  const isFormValid =
    formData.nombre.trim() &&
    formData.email.trim() &&
    formData.grado &&
    formData.seccion &&
    formData.password &&
    formData.confirmPassword &&
    formData.password === formData.confirmPassword

  const buttonLabel = mode === "Registrarse" ? "Registrarse" : "Ingresar"

  return (
    <div className={styles.container}>
      <div className={styles.wrapper}>
        <div className={styles.brand}>
          <div className={styles.brandIcon}>
            <span aria-label="cerebro" role="img">
              🧠
            </span>
          </div>
          <h1 className={styles.title}>NeuroTutor</h1>
          <p className={styles.subtitle}>Aprende matemáticas de forma inteligente</p>
        </div>

        <div className={styles.card}>
          <div className={styles.pillGroup}>
            <button
              type="button"
              className={`${styles.tabButton} ${role === "Estudiante" ? styles.active : ""}`}
              onClick={() => setRole("Estudiante")}
            >
              <span aria-label="birrete" role="img">
                🎓
              </span>
              Estudiante
            </button>
            <button
              type="button"
              className={`${styles.tabButton} ${role === "Docente" ? styles.active : ""}`}
              onClick={() => setRole("Docente")}
            >
              <span aria-label="docente" role="img">
                👤
              </span>
              Docente
            </button>
            <button
              type="button"
              className={`${styles.tabButton} ${role === "Admin" ? styles.active : ""}`}
              onClick={() => setRole("Admin")}
            >
              <span aria-label="configuracion" role="img">
                ⚙️
              </span>
              Admin
            </button>
          </div>

          <div className={`${styles.pillGroup} ${styles.modeGroup}`}>
            <button
              type="button"
              className={`${styles.tabButton} ${styles.smallTab} ${mode === "Iniciar sesión" ? styles.active : ""}`}
              onClick={() => setMode("Iniciar sesión")}
            >
              Iniciar sesión
            </button>
            <button
              type="button"
              className={`${styles.tabButton} ${styles.smallTab} ${mode === "Registrarse" ? styles.active : ""}`}
              onClick={() => setMode("Registrarse")}
            >
              Registrarse
            </button>
          </div>

          <div className={styles.formField}>
            <label htmlFor="nombre" className={styles.label}>
              Nombre Completo
            </label>
            <input
              id="nombre"
              name="nombre"
              type="text"
              placeholder="Ej: Ana García"
              value={formData.nombre}
              onChange={handleChange}
              className={styles.input}
            />
          </div>

          <div className={styles.formField}>
            <label htmlFor="email" className={styles.label}>
              Correo Electrónico
            </label>
            <input
              id="email"
              name="email"
              type="email"
              placeholder="ejemplo@correo.com"
              value={formData.email}
              onChange={handleChange}
              className={styles.input}
            />
          </div>

          <div className={styles.formRow}>
            <div className={styles.formFieldHalf}>
              <label htmlFor="grado" className={styles.label}>
                Grado
              </label>
              <select
                id="grado"
                name="grado"
                value={formData.grado}
                onChange={handleChange}
                className={styles.select}
              >
                <option value="" disabled>Selecciona tu grado</option>
                <option value="1ro">1ro</option>
                <option value="2do">2do</option>
                <option value="3ro">3ro</option>
                <option value="4to">4to</option>
                <option value="5to">5to</option>
                <option value="6to">6to</option>
              </select>
            </div>

            <div className={styles.formFieldHalf}>
              <label htmlFor="seccion" className={styles.label}>
                Sección
              </label>
              <select
                id="seccion"
                name="seccion"
                value={formData.seccion}
                onChange={handleChange}
                className={styles.select}
              >
                <option value="" disabled>Selecciona tu sección</option>
                <option value="A">A</option>
                <option value="B">B</option>
                <option value="C">C</option>
                <option value="D">D</option>
              </select>
            </div>
          </div>

          <div className={styles.formField}>
            <label htmlFor="password" className={styles.label}>
              Contraseña
            </label>
            <input
              id="password"
              name="password"
              type="password"
              placeholder="********"
              value={formData.password}
              onChange={handleChange}
              className={styles.input}
            />
          </div>

          <div className={styles.formField}>
            <label htmlFor="confirmPassword" className={styles.label}>
              Confirmar Contraseña
            </label>
            <input
              id="confirmPassword"
              name="confirmPassword"
              type="password"
              placeholder="********"
              value={formData.confirmPassword}
              onChange={handleChange}
              className={styles.input}
            />
          </div>

          <button
            type="button"
            className={styles.primaryButton}
            disabled={!isFormValid}
          >
            {buttonLabel}
          </button>
        </div>

        <p className={styles.footerText}>
          Sistema adaptativo para estudiantes de primaria
        </p>
      </div>
    </div>
  )
}

export default Register