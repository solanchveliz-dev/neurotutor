import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { ArrowRight, Bell, LogOut, Search, Star } from "lucide-react";
import AppSidebar from "../components/layout/AppSidebar";
import StudentLayout from "../components/layout/StudentLayout";
import NeoCard from "../components/student/NeoCard";
import PrimaryButton from "../components/student/PrimaryButton";
import ProgressCard from "../components/student/ProgressCard";
import { getStudentDashboard } from "../services/dashboardService";
import { clearAuthData, getStudentId } from "../utils/auth";

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
      description: "Operaciones, numeros y situaciones de conteo.",
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
      title: "Forma, movimiento y localizacion",
      description: "Figuras, medidas y ubicacion espacial.",
      progress: 0,
      total: 10,
      unlocked: false,
      active: false,
    },
    {
      id: 4,
      title: "Gestion de datos e incertidumbre",
      description: "Tablas, graficos y probabilidades.",
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
          gender: profile.gender || profile.genero,
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

    navigate(`/module/${module.id}`, { state: { module } });
  };

  const getModulePercentage = (module) => {
    if (!module.total) return 0;

    return Math.round((module.progress / module.total) * 100);
  };

  const totalExercises = modules.reduce((sum, module) => sum + (module.total || 0), 0);
  const completedExercises = modules.reduce(
    (sum, module) => sum + (module.progress || 0),
    0
  );
  const overallProgress = totalExercises
    ? Math.round((completedExercises / totalExercises) * 100)
    : 0;

  const sidebarItems = [
    {
      label: "Inicio",
      active: true,
      onClick: () => navigate("/student-dashboard"),
    },
    {
      label: "Módulos",
      onClick: () => navigate("/learning-path"),
    },
    {
      label: "Mis Logros",
      onClick: () => navigate("/learning-path"),
    },
    {
      label: "Perfil",
      onClick: () => setShowDeleteModal(true),
    },
  ];

  const sidebarFooter = (
    <button
      type="button"
      className="flex w-full items-center gap-3 rounded-nt-button px-3 py-3 text-sm font-extrabold text-nt-text-secondary transition hover:bg-nt-sky hover:text-nt-blue"
      onClick={handleLogout}
    >
      <LogOut className="size-5" aria-hidden="true" />
      <span>Cerrar sesion</span>
    </button>
  );

  const islandCards = [
    {
      label: "Fracciones",
      image: "/assets/island-fracciones.png",
      module: modules[0] || fallbackModules[0],
      progressTone: "bg-nt-green",
    },
    {
      label: "Decimales",
      image: "/assets/island-decimales.png",
      module: modules[1] || fallbackModules[1],
      progressTone: "bg-nt-yellow",
    },
    {
      label: "Porcentajes",
      image: "/assets/island-porcentajes.png",
      module: modules[2] || fallbackModules[2],
      progressTone: "bg-nt-red",
    },
  ];

  const learningCards = [
    {
      title: "Fracciones - Basico",
      subtitle: "Que es una fraccion?",
      image: "/assets/card-fracciones-basic.png",
      module: modules[0] || fallbackModules[0],
      progressTone: "bg-nt-green",
    },
    {
      title: "Decimales - Basico",
      subtitle: "Introduccion a los decimales",
      image: "/assets/card-decimales-basic.png",
      module: modules[1] || fallbackModules[1],
      progressTone: "bg-nt-yellow",
    },
    {
      title: "Porcentajes - Basico",
      subtitle: "Porcentajes en la vida diaria",
      image: "/assets/card-porcentajes-basic.png",
      module: modules[2] || fallbackModules[2],
      progressTone: "bg-nt-red",
    },
  ].map((card) => ({
    ...card,
    percentage: getModulePercentage(card.module),
  }));

  const recentActivities = [
    {
      title: "Fracciones basicas",
      detail: "Completaste una practica",
      image: "/assets/card-fracciones-basic.png",
      points: "+25 pts",
    },
    {
      title: "Decimales",
      detail: "Repasaste ejercicios guiados",
      image: "/assets/card-decimales-basic.png",
      points: "+15 pts",
    },
  ];

  const upcomingChallenges = [
    {
      title: "Resolver 5 fracciones",
      progress: 60,
      points: "30 pts",
    },
    {
      title: "Practicar decimales",
      progress: 35,
      points: "20 pts",
    },
    {
      title: "Revisar porcentajes",
      progress: 15,
      points: "25 pts",
    },
  ];

  const studentGender = (student.gender || student.genero || "").toLowerCase();
  const avatarSrc =
    studentGender === "male" || studentGender === "masculino"
      ? "/assets/avatar-boy.png"
      : "/assets/avatar-girl.png";

  const subjectProgress = learningCards.map((card) => ({
    label: card.title.split(" - ")[0],
    percentage: card.percentage,
    tone: card.progressTone,
  }));

  return (
    <StudentLayout
      sidebar={<AppSidebar items={sidebarItems} footer={sidebarFooter} />}
      topbar={
        <header className="flex w-full flex-col gap-3 rounded-[28px] bg-white/35 px-3 py-2 backdrop-blur-sm md:flex-row md:items-center md:justify-between">
          <label className="relative min-w-0 flex-1 md:max-w-lg">
            <Search
              className="pointer-events-none absolute left-4 top-1/2 size-4 -translate-y-1/2 text-nt-text-secondary"
              aria-hidden="true"
            />
            <span className="sr-only">Buscar</span>
            <input
              type="search"
              placeholder="Buscar modulos o retos"
              className="h-12 w-full rounded-nt-button border border-white/80 bg-white/90 pl-11 pr-4 text-sm font-semibold text-nt-text-primary shadow-sm outline-none transition placeholder:text-nt-text-secondary focus:border-nt-blue focus:ring-4 focus:ring-nt-blue-light/25"
            />
          </label>

          <div className="flex items-center justify-end gap-2">
            <button
              type="button"
              className="grid size-12 place-items-center rounded-nt-button bg-white/90 text-nt-blue shadow-sm transition hover:bg-nt-blue hover:text-white"
              aria-label="Notificaciones"
            >
              <Bell className="size-5" aria-hidden="true" />
            </button>
            <div className="relative">
              <button
                type="button"
                onClick={() => setShowUserMenu(!showUserMenu)}
                className="flex h-12 items-center gap-2 rounded-nt-button bg-white/90 p-1.5 pr-3 text-left shadow-sm transition hover:bg-white"
              >
                <img
                  src={avatarSrc}
                  alt=""
                  className="size-9 rounded-full object-cover shadow-sm"
                />
                <span className="hidden max-w-32 truncate text-sm font-extrabold text-nt-text-primary sm:block">
                  {student.name}
                </span>
              </button>

              {showUserMenu && (
                <div className="absolute right-0 top-[calc(100%+12px)] z-50 w-56 overflow-hidden rounded-[22px] border border-nt-border bg-white shadow-nt-soft">
                  <button
                    type="button"
                    className="w-full px-4 py-3 text-left text-sm font-extrabold text-nt-text-primary transition hover:bg-nt-sky"
                    onClick={handleLogout}
                  >
                    Cerrar sesion
                  </button>

                  <button
                    type="button"
                    className="w-full px-4 py-3 text-left text-sm font-extrabold text-nt-red transition hover:bg-red-50"
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
        </header>
      }
      rightPanel={
        <div className="rounded-nt-card border border-white/80 bg-white/95 p-5 shadow-nt-card">
          <div className="text-center">
            <img
              src={avatarSrc}
              alt=""
              className="mx-auto size-24 rounded-full object-cover shadow-nt-card"
            />
            <h2 className="mt-3 text-xl font-black text-nt-text-primary">
              {student.name}
            </h2>
            <p className="text-sm font-extrabold text-nt-text-secondary">
              {student.grade} - Seccion {student.section}
            </p>
            <div className="mt-4 rounded-[22px] bg-nt-sky/70 p-3 text-left">
              <div className="flex items-center justify-between text-sm font-black text-nt-text-primary">
                <span>Nivel {student.level}</span>
                <span>{overallProgress}%</span>
              </div>
              <div className="mt-2 h-2.5 overflow-hidden rounded-full bg-white">
                <div
                  className="h-full rounded-full bg-nt-blue"
                  style={{ width: `${overallProgress}%` }}
                />
              </div>
            </div>
          </div>

          <div className="mt-5 border-t border-nt-border pt-5">
            <h2 className="text-lg font-black text-nt-text-primary">
              Progreso
            </h2>
            <div className="mt-4 grid gap-3">
              {subjectProgress.map((item) => (
                <div key={item.label}>
                  <div className="mb-1 flex items-center justify-between text-sm font-extrabold text-nt-text-primary">
                    <span>{item.label}</span>
                    <span>{item.percentage}%</span>
                  </div>
                  <div className="h-2.5 overflow-hidden rounded-full bg-nt-border">
                    <div
                      className={`h-full rounded-full ${item.tone}`}
                      style={{ width: `${item.percentage}%` }}
                    />
                  </div>
                </div>
              ))}
            </div>
          </div>

          <div className="mt-5 border-t border-nt-border pt-5">
            <h2 className="text-lg font-black text-nt-text-primary">
              Insignias recientes
            </h2>
            <div className="mt-4 grid grid-cols-3 gap-2">
              {["Constancia", "Rapidez", "Precision"].map((badge) => (
                <div
                  key={badge}
                  className="rounded-[18px] bg-nt-sky/70 p-2 text-center"
                >
                  <Star className="mx-auto size-5 fill-nt-yellow text-nt-yellow" />
                  <p className="mt-1 truncate text-[11px] font-black text-nt-text-primary">
                    {badge}
                  </p>
                </div>
              ))}
            </div>
          </div>

          <div className="mt-5 border-t border-nt-border pt-5">
            <h2 className="text-lg font-black text-nt-text-primary">
              Reto semanal
            </h2>
            <p className="mt-1 text-sm font-semibold text-nt-text-secondary">
              Completa 10 ejercicios esta semana
            </p>
            <div className="mt-4 h-3 overflow-hidden rounded-full bg-nt-border">
              <div
                className="h-full rounded-full bg-nt-purple"
                style={{ width: `${overallProgress}%` }}
              />
            </div>
            <p className="mt-2 text-xs font-black text-nt-text-secondary">
              {completedExercises}/{totalExercises} ejercicios completados
            </p>
          </div>
        </div>
      }
    >
      <section className="relative isolate min-h-[560px] overflow-visible" aria-label="Islas principales de aprendizaje">
        <div className="relative z-10 flex min-h-[520px] flex-col gap-1 px-1 py-3 sm:px-3 sm:py-4 lg:min-h-[540px] xl:min-h-[560px]">
          <div className="flex items-start justify-between gap-4">
            <div className="max-w-lg pt-1 text-nt-text-primary drop-shadow-[0_2px_0_rgba(255,255,255,0.75)]">
              <span className="inline-flex rounded-full bg-nt-purple px-3 py-1 text-xs font-black text-white shadow-lg shadow-nt-purple/25">
                Panel de aprendizaje
              </span>
              <h1 className="mt-2 text-2xl font-black leading-tight text-nt-text-primary md:text-3xl">
                Hola, {student.name}
              </h1>
              <p className="mt-1 text-base font-black text-nt-blue md:text-lg">
                Continua tu aventura matematica
              </p>
            </div>

            <img
              src="/assets/neo.png"
              alt="NEO"
              className="hidden h-36 w-auto shrink-0 translate-y-2 drop-shadow-[0_18px_30px_rgba(30,58,138,0.25)] md:block lg:h-48 xl:h-56"
            />
          </div>

          <div className="-mt-10 grid gap-2 md:-mt-16 md:grid-cols-3 md:items-start lg:-mt-24">
            {islandCards.map((island) => {
              return (
                <div
                  key={island.label}
                  className="rounded-[34px] bg-transparent p-0 text-left"
                >
                  <div className="relative">
                    <button
                      type="button"
                      className="group block w-full rounded-[34px] bg-transparent focus:outline-none focus:ring-4 focus:ring-nt-blue-light/40"
                      onClick={() => handleOpenModule(island.module)}
                    >
                      <img
                        src={island.image}
                        alt=""
                        className="mx-auto h-[304px] w-full object-contain drop-shadow-[0_34px_46px_rgba(30,58,138,0.34)] transition duration-300 group-hover:scale-110 group-hover:drop-shadow-[0_42px_64px_rgba(37,99,235,0.48)] sm:h-[336px] md:h-[336px] lg:h-[400px] 2xl:h-[450px]"
                      />
                    </button>
                  </div>
                </div>
              );
            })}
          </div>
        </div>
      </section>

      <section className="space-y-4 rounded-nt-card border border-white/80 bg-white/80 p-5 shadow-nt-card backdrop-blur">
        <div className="flex items-center justify-between gap-3">
          <h2 className="text-xl font-black text-nt-text-primary">
            Continua aprendiendo
          </h2>
          <button
            type="button"
            className="inline-flex items-center gap-1 text-sm font-black text-nt-blue transition hover:text-nt-purple"
            onClick={() => navigate("/learning-path")}
          >
            Ver todo
            <ArrowRight className="size-4" aria-hidden="true" />
          </button>
        </div>

        <div className="grid gap-3 xl:grid-cols-3">
          {learningCards.map((card) => (
            <button
              key={card.title}
              type="button"
              className="flex min-w-0 items-center gap-3 rounded-[24px] border border-white/80 bg-white p-3 text-left shadow-sm transition hover:-translate-y-0.5 hover:shadow-nt-card focus:outline-none focus:ring-4 focus:ring-nt-blue-light/30"
              onClick={() => handleOpenModule(card.module)}
            >
              <img
                src={card.image}
                alt=""
                className="h-20 w-24 shrink-0 rounded-[18px] object-cover drop-shadow-[0_10px_16px_rgba(30,58,138,0.16)]"
              />
              <div className="min-w-0 flex-1">
                <div className="flex items-center justify-between gap-2">
                  <div className="min-w-0">
                    <h3 className="truncate text-base font-black text-nt-text-primary">
                      {card.title}
                    </h3>
                    <p className="text-xs font-extrabold text-nt-text-secondary">
                      {card.subtitle}
                    </p>
                  </div>
                  <ArrowRight className="size-5 shrink-0 text-nt-blue" aria-hidden="true" />
                </div>
                <div className="mt-3 h-2.5 overflow-hidden rounded-full bg-nt-border">
                  <div
                    className={`h-full rounded-full ${card.progressTone}`}
                    style={{ width: `${card.percentage}%` }}
                  />
                </div>
                <p className="mt-1 text-xs font-black text-nt-text-secondary">
                  {card.percentage}% completado
                </p>
              </div>
            </button>
          ))}
        </div>
      </section>

      <section className="grid gap-4 xl:grid-cols-2">
        <article className="rounded-nt-card border border-white/80 bg-white/85 p-5 shadow-nt-card backdrop-blur">
          <h2 className="text-lg font-black text-nt-text-primary">
            Actividad reciente
          </h2>
          <p className="mb-4 text-sm font-semibold text-nt-text-secondary">
            Ultimo avance registrado
          </p>
          <div className="grid gap-2">
            {recentActivities.map((activity) => (
              <div
                key={activity.title}
                className="flex items-center gap-3 rounded-[18px] bg-white px-3 py-2 shadow-sm"
              >
                <img
                  src={activity.image}
                  alt=""
                  className="size-11 rounded-[14px] object-cover"
                />
                <div className="min-w-0 flex-1">
                  <p className="truncate text-sm font-black text-nt-text-primary">
                    {activity.title}
                  </p>
                  <p className="truncate text-xs font-semibold text-nt-text-secondary">
                    {activity.detail}
                  </p>
                </div>
                <span className="shrink-0 rounded-full bg-nt-green/15 px-2.5 py-1 text-xs font-black text-green-700">
                  {activity.points}
                </span>
              </div>
            ))}
          </div>
        </article>

        <article className="rounded-nt-card border border-white/80 bg-white/85 p-5 shadow-nt-card backdrop-blur">
          <h2 className="text-lg font-black text-nt-text-primary">
            Proximos desafios
          </h2>
          <p className="mb-4 text-sm font-semibold text-nt-text-secondary">
            Retos sugeridos para hoy
          </p>
          <div className="grid gap-2">
            {upcomingChallenges.map((challenge) => (
              <div
                key={challenge.title}
                className="rounded-[18px] bg-white px-3 py-2 shadow-sm"
              >
                <div className="flex items-center justify-between gap-3">
                  <span className="text-sm font-extrabold text-nt-text-primary">
                    {challenge.title}
                  </span>
                  <span className="shrink-0 rounded-full bg-nt-purple/10 px-2.5 py-1 text-xs font-black text-nt-purple">
                    {challenge.points}
                  </span>
                </div>
                <div className="mt-2 h-2 overflow-hidden rounded-full bg-nt-border">
                  <div
                    className="h-full rounded-full bg-nt-blue"
                    style={{ width: `${challenge.progress}%` }}
                  />
                </div>
              </div>
            ))}
          </div>
        </article>
      </section>

      {showDeleteModal && (
        <div className="fixed inset-0 z-50 grid place-items-center bg-slate-950/55 p-5">
          <div className="w-full max-w-md rounded-nt-card border border-white/80 bg-white p-6 shadow-nt-soft">
            <h2 className="text-2xl font-black text-nt-text-primary">
              Estas seguro de eliminar tu cuenta?
            </h2>

            <p className="mt-3 text-sm font-semibold leading-6 text-nt-text-secondary">
              Esta accion eliminara tu acceso al sistema. Ingresa tu contrasena
              para confirmar.
            </p>

            <input
              type="password"
              placeholder="Ingresa tu contrasena"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="mt-5 h-12 w-full rounded-nt-button border border-nt-border bg-white px-4 text-sm font-semibold text-nt-text-primary outline-none transition placeholder:text-nt-text-secondary focus:border-nt-blue focus:ring-4 focus:ring-nt-blue-light/25"
            />

            <div className="mt-5 grid gap-3 sm:grid-cols-2">
              <PrimaryButton
                type="button"
                tone="blue"
                className="bg-slate-100 text-nt-text-primary shadow-none hover:bg-slate-200"
                onClick={() => setShowDeleteModal(false)}
              >
                Cancelar
              </PrimaryButton>

              <PrimaryButton type="button" tone="purple" onClick={handleDeleteAccount}>
                Confirmar eliminacion
              </PrimaryButton>
            </div>
          </div>
        </div>
      )}
    </StudentLayout>
  );
}

export default StudentDashboard;
