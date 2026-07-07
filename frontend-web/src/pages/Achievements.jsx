import { useEffect, useState } from "react";
import { Award, BookOpenCheck, Check, ClipboardCheck, LockKeyhole, Medal, PencilRuler, Star, Trophy } from "lucide-react";
import { useNavigate } from "react-router-dom";
import AppSidebar from "../components/layout/AppSidebar";
import StudentLayout from "../components/layout/StudentLayout";
import BackButton from "../components/student/BackButton";
import PrimaryButton from "../components/student/PrimaryButton";
import { getStudentAchievements } from "../services/achievementService";
import { logApiError } from "../services/api";
import { getStudentProgress } from "../services/progressService";
import { getStoredUser, getStudentId } from "../utils/auth";
import { getAchievementImage, sortAchievementsByUnlockedAt } from "../utils/achievementVisuals";
import { getCachedStudentData, setCachedStudentData } from "../utils/studentDataCache";

const iconMap = {
  "clipboard-check": ClipboardCheck,
  "book-open-check": BookOpenCheck,
  "pencil-ruler": PencilRuler,
  trophy: Trophy,
  medal: Medal,
  star: Star,
};

const levelAchievementVisuals = [
  { key: "basic", name: "Básico", description: "Progreso registrado en el nivel básico.", image: "/assets/nivel_basico.png", accent: "text-emerald-700", progress: "bg-gradient-to-r from-emerald-400 to-emerald-600", surface: "from-green-50 to-emerald-100/80", border: "border-emerald-200" },
  { key: "intermediate", name: "Intermedio", description: "Progreso registrado en el nivel intermedio.", image: "/assets/nivel_intermedio.png", accent: "text-blue-700", progress: "bg-gradient-to-r from-sky-400 to-blue-600", surface: "from-blue-50 to-sky-100/80", border: "border-blue-200" },
  { key: "advanced", name: "Avanzado", description: "Progreso registrado en el nivel avanzado.", image: "/assets/nivel_avanzado.png", accent: "text-violet-700", progress: "bg-gradient-to-r from-violet-400 to-purple-600", surface: "from-purple-50 to-violet-100/80", border: "border-violet-200" },
];

function normalizeLevelKey(value = "") {
  const normalized = value.toLowerCase().normalize("NFD").replace(/[\u0300-\u036f]/g, "");
  if (normalized.includes("intermedio") || normalized.includes("intermediate")) return "intermediate";
  if (normalized.includes("avanzado") || normalized.includes("advanced")) return "advanced";
  if (normalized.includes("basico") || normalized.includes("basic")) return "basic";
  return null;
}

function getLevelProgressMetrics(levelKey, modules) {
  const matchingModules = modules.filter((module) => normalizeLevelKey(module.level) === levelKey);
  if (!matchingModules.length) return { hasProgress: false, percentage: 0, status: "Sin datos" };

  const percentage = Math.round(
    matchingModules.reduce((sum, module) => sum + Math.min(100, Math.max(0, Number(module.progress_percentage) || 0)), 0)
      / matchingModules.length
  );

  return {
    hasProgress: true,
    percentage,
    status: percentage >= 100 ? "Completado" : percentage > 0 ? "En progreso" : "Sin datos",
  };
}

function getAchievementVisualImage(achievement) {
  const mappedImage = getAchievementImage(achievement);
  if (mappedImage) return mappedImage;

  const levelKey = normalizeLevelKey([
    achievement?.code,
    achievement?.title,
    achievement?.nombre,
    achievement?.name,
    achievement?.description,
  ].filter(Boolean).join(" "));

  if (levelKey === "basic") return "/assets/nivel_basico.png";
  if (levelKey === "intermediate") return "/assets/nivel_intermedio.png";
  if (levelKey === "advanced") return "/assets/nivel_avanzado.png";
  return null;
}

const formatDate = (value) => {
  if (!value) return "";
  return new Intl.DateTimeFormat("es-PE", { day: "numeric", month: "short", year: "numeric" })
    .format(new Date(value));
};

function LockedAchievementItem({ achievement }) {
  const Icon = iconMap[achievement.icon] ?? Award;
  const achievementImage = getAchievementVisualImage(achievement);

  return (
    <article className="group relative flex min-w-0 flex-col items-center rounded-[22px] border border-slate-200 bg-white px-3 py-4 text-center shadow-[0_10px_24px_rgba(30,58,138,0.08)] transition hover:-translate-y-1 hover:shadow-nt-card sm:px-4">
      <div className="relative grid size-28 place-items-center sm:size-32">
        {achievementImage ? (
          <img
            src={achievementImage}
            alt=""
            className="h-24 max-h-full w-full max-w-28 object-contain grayscale opacity-45 drop-shadow-[0_12px_14px_rgba(71,85,105,0.18)] sm:h-28 sm:max-w-32"
          />
        ) : (
          <Icon className="size-20 text-slate-400 sm:size-24" aria-hidden="true" />
        )}
        <span className="absolute bottom-1 right-1 grid size-8 place-items-center rounded-full border-2 border-white bg-slate-500 text-white shadow-md">
          <LockKeyhole className="size-4" aria-hidden="true" />
        </span>
      </div>
      <h3 className="mt-2 line-clamp-2 text-sm font-black leading-5 text-nt-text-primary sm:text-base">
        {achievement.title}
      </h3>
      {achievement.description && (
        <p className="mt-1 line-clamp-2 text-xs font-semibold leading-4 text-nt-text-secondary">
          {achievement.description}
        </p>
      )}
      <p className="mt-2 rounded-full bg-slate-100 px-3 py-1 text-[11px] font-black text-slate-600">
        {achievement.points_required ? `Requiere ${achievement.points_required} puntos` : "Sigue avanzando"}
      </p>
    </article>
  );
}

function UnlockedAchievementItem({ achievement }) {
  const Icon = iconMap[achievement.icon] ?? Award;
  const achievementImage = getAchievementVisualImage(achievement);

  return (
    <article
      className="group relative flex min-w-0 flex-col items-center rounded-[22px] border border-slate-200/80 bg-white px-3 py-4 text-center shadow-[0_10px_24px_rgba(30,58,138,0.08)] transition hover:-translate-y-1 hover:border-blue-200 hover:shadow-nt-card sm:px-4"
      title={achievement.description || achievement.title}
    >
      <div className="grid size-28 place-items-center sm:size-32">
        {achievementImage ? (
          <img
            src={achievementImage}
            alt=""
            className="h-24 max-h-full w-full max-w-28 object-contain drop-shadow-[0_14px_16px_rgba(30,58,138,0.22)] transition-transform group-hover:scale-105 sm:h-28 sm:max-w-32"
          />
        ) : (
          <Icon className="size-20 text-nt-purple drop-shadow-[0_10px_12px_rgba(124,58,237,0.22)] sm:size-24" aria-hidden="true" />
        )}
      </div>
      <h3 className="mt-2 line-clamp-2 text-sm font-black leading-5 text-nt-text-primary sm:text-base">
        {achievement.title}
      </h3>
      {achievement.description && (
        <p className="mt-1 line-clamp-2 text-xs font-semibold leading-4 text-nt-text-secondary">
          {achievement.description}
        </p>
      )}
      {achievement.unlocked_at && (
        <p className="mt-2 rounded-full bg-emerald-100 px-3 py-1 text-[11px] font-black text-emerald-700">
          {formatDate(achievement.unlocked_at)}
        </p>
      )}
    </article>
  );
}

function Achievements() {
  const navigate = useNavigate();
  const studentId = getStudentId();
  const cachedAchievements = getCachedStudentData(studentId, "achievements");
  const [data, setData] = useState(cachedAchievements ?? { unlocked: [], locked: [] });
  const [progressModules, setProgressModules] = useState([]);
  const [isLoading, setIsLoading] = useState(!cachedAchievements);
  const [error, setError] = useState("");

  const sidebarItems = [
    { label: "Inicio", onClick: () => navigate("/student-dashboard") },
    { label: "Módulos" },
    { label: "Mis Logros", active: true, onClick: () => navigate("/achievements") },
    { label: "Perfil", onClick: () => navigate("/profile") },
  ];

  const loadAchievements = async () => {
    const token = localStorage.getItem("token");
    const user = getStoredUser();
    console.log("TOKEN:", token ? "existe" : "no existe");
    console.log("USER:", user);
    console.log("STUDENT ID:", studentId);
    if (!studentId) {
      setError("No pudimos identificar al estudiante conectado.");
      setIsLoading(false);
      return;
    }

    setIsLoading(!cachedAchievements);
    setError("");
    try {
      getStudentProgress(studentId)
        .then((progress) => setProgressModules(Array.isArray(progress?.modules) ? progress.modules : []))
        .catch(logApiError);
      const response = await getStudentAchievements(studentId);
      const normalized = {
        unlocked: Array.isArray(response?.unlocked)
          ? sortAchievementsByUnlockedAt(response.unlocked)
          : [],
        locked: Array.isArray(response?.locked) ? response.locked : [],
      };
      setData(normalized);
      setCachedStudentData(studentId, "achievements", normalized);
    } catch (apiError) {
      logApiError(apiError);
      setError("No pudimos cargar tus logros en este momento.");
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    loadAchievements();
  }, []);

  if (isLoading) {
    return (
      <StudentLayout sidebar={<AppSidebar items={sidebarItems} />}>
        <div className="grid min-h-[440px] place-items-center">
          <div className="h-12 w-12 animate-spin rounded-full border-4 border-nt-blue border-t-transparent" />
        </div>
      </StudentLayout>
    );
  }

  return (
    <StudentLayout
      sidebar={<AppSidebar items={sidebarItems} />}
      topbar={<BackButton onClick={() => navigate("/student-dashboard")}>Volver al inicio</BackButton>}
      contentClassName="2xl:col-span-2"
    >
      <section className="relative px-2 py-3 sm:px-4 lg:px-6">
        <div className="relative grid grid-cols-2 items-center gap-4 lg:grid-cols-[minmax(150px,0.7fr)_minmax(360px,1.7fr)_minmax(190px,0.9fr)] lg:gap-7">
          <div className="order-2 flex justify-center lg:order-1">
            <img
              src="/assets/copa.png"
              alt=""
              className="h-40 w-full object-contain drop-shadow-[0_18px_22px_rgba(124,58,237,0.2)] sm:h-52 lg:h-64"
            />
          </div>

          <div className="order-1 col-span-2 min-w-0 lg:order-2 lg:col-span-1">
            <h1 className="text-3xl font-black tracking-tight text-nt-text-primary md:text-4xl">Mis logros</h1>
            <p className="mt-2 text-sm font-semibold leading-6 text-slate-700 sm:text-base">
              Cada logro es un paso más en tu aventura matemática.
            </p>

            <div className="mt-5 rounded-[24px] border border-violet-200/80 bg-gradient-to-br from-sky-100 via-white to-violet-100 p-4 shadow-[0_16px_34px_rgba(99,102,241,0.16)]">
              <h2 className="text-base font-black text-nt-text-primary">🏆 Resumen de colección</h2>
              <div className="mt-3 grid gap-3 sm:grid-cols-2">
                <div className="flex items-center gap-3 rounded-[18px] border border-emerald-300 bg-gradient-to-br from-emerald-100 to-green-200 px-4 py-3 shadow-[0_10px_24px_rgba(16,185,129,0.16)]">
                  <span className="grid size-14 shrink-0 place-items-center rounded-full bg-gradient-to-br from-emerald-200 to-green-300 shadow-inner">
                    <img src="/assets/candado_abierto.png" alt="" className="size-12 object-contain" />
                  </span>
                  <div>
                    <p className="text-3xl font-black text-emerald-800">{error ? "—" : data.unlocked.length}</p>
                    <p className="text-xs font-black text-slate-600">Desbloqueados</p>
                  </div>
                </div>
                <div className="flex items-center gap-3 rounded-[18px] border border-purple-300 bg-gradient-to-br from-violet-100 to-purple-200 px-4 py-3 shadow-[0_10px_24px_rgba(124,58,237,0.16)]">
                  <span className="grid size-14 shrink-0 place-items-center rounded-full bg-gradient-to-br from-purple-200 to-violet-300 shadow-inner">
                    <img src="/assets/candado_cerrado.png" alt="" className="size-12 object-contain" />
                  </span>
                  <div>
                    <p className="text-3xl font-black text-violet-800">{error ? "—" : data.locked.length}</p>
                    <p className="text-xs font-black text-slate-600">Bloqueados</p>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div className="order-3 flex justify-center">
            <img
              src="/assets/neo_logros.png"
              alt="NEO celebrando tus logros"
              className="h-44 w-full object-contain drop-shadow-[0_20px_24px_rgba(37,99,235,0.22)] sm:h-56 lg:h-72"
            />
          </div>
        </div>
      </section>

      {error ? (
        <section className="rounded-nt-card border border-amber-200 bg-amber-50 p-7 text-center shadow-nt-card">
          <p className="font-bold text-amber-900">{error}</p>
          <PrimaryButton className="mt-4" onClick={loadAchievements}>Reintentar</PrimaryButton>
        </section>
      ) : (
        <>
          <section className="rounded-nt-card border border-white/80 bg-white/80 p-4 shadow-nt-card backdrop-blur-sm sm:p-5">
            <h2 className="text-2xl font-black text-nt-text-primary">Logros por nivel</h2>
            {/* TODO: The progress API exposes progress by level, but it still needs a per-level points field. */}
            <div className="mt-4 grid gap-4 lg:grid-cols-3">
              {levelAchievementVisuals.map((level) => {
                const metrics = getLevelProgressMetrics(level.key, progressModules);

                return (
                <article
                  key={level.key}
                  className={`grid min-w-0 grid-cols-[170px_minmax(0,1fr)] items-center gap-3 overflow-hidden rounded-[24px] border bg-gradient-to-br ${level.surface} ${level.border} p-4 shadow-[0_14px_30px_rgba(30,58,138,0.11)] lg:grid-cols-1 xl:grid-cols-[180px_minmax(0,1fr)]`}
                >
                  <img
                    src={level.image}
                    alt=""
                    className="mx-auto h-40 max-h-full w-full max-w-44 object-contain drop-shadow-[0_18px_22px_rgba(30,58,138,0.22)] lg:h-44"
                  />
                  <div className="min-w-0">
                    <h3 className={`text-xl font-black ${level.accent}`}>{level.name}</h3>
                    <p className="mt-1 text-sm font-semibold leading-5 text-slate-600">{level.description}</p>
                    <div className="mt-4 h-2.5 overflow-hidden rounded-full bg-slate-200">
                      <div
                        className={`h-full rounded-full transition-[width] duration-500 ${level.progress}`}
                        style={{ width: `${metrics.percentage}%` }}
                      />
                    </div>
                    <div className="mt-3 flex flex-wrap items-center justify-between gap-2">
                      <span className="text-sm font-black text-slate-500">Puntos: Sin datos</span>
                      <span className={`rounded-full bg-white/70 px-3 py-1 text-xs font-black ${level.accent}`}>
                        {metrics.status}
                      </span>
                    </div>
                  </div>
                </article>
                );
              })}
            </div>
          </section>

          <section>
            <div className="flex items-center gap-3">
              <span className="grid size-8 place-items-center rounded-full bg-nt-green text-white shadow-lg shadow-nt-green/20">
                <Check className="size-5" aria-hidden="true" />
              </span>
              <h2 className="text-2xl font-black text-nt-text-primary">Logros desbloqueados</h2>
            </div>
            {data.unlocked.length ? (
              <div className="mt-4 grid grid-cols-2 gap-3 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5">
                {data.unlocked.map((item) => <UnlockedAchievementItem key={item.id} achievement={item} />)}
              </div>
            ) : (
              <div className="mt-4 rounded-nt-card border border-white/80 bg-white/85 p-7 text-center shadow-nt-card">
                <Award className="mx-auto size-10 text-nt-purple" />
                <p className="mt-3 font-black text-nt-text-primary">Aún no tienes logros desbloqueados</p>
                <p className="mt-1 text-sm font-semibold text-nt-text-secondary">Completa actividades para conseguir tu primera insignia.</p>
              </div>
            )}
          </section>

          <section>
            <h2 className="text-2xl font-black text-nt-text-primary">Próximos logros</h2>
            {data.locked.length ? (
              <div className="mt-4 grid grid-cols-2 gap-3 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5">
                {data.locked.map((item) => <LockedAchievementItem key={item.id} achievement={item} />)}
              </div>
            ) : (
              <div className="relative mt-4 overflow-hidden rounded-nt-card border border-sky-200 bg-gradient-to-br from-white via-sky-50 to-violet-100 p-7 text-center shadow-nt-card">
                <div className="absolute -right-8 -top-10 size-32 rounded-full bg-violet-200/35 blur-2xl" aria-hidden="true" />
                <div className="relative mx-auto grid size-16 place-items-center rounded-[22px] bg-gradient-to-br from-nt-yellow/35 to-orange-100 text-amber-600 shadow-lg shadow-nt-yellow/15">
                  <Trophy className="size-9" aria-hidden="true" />
                </div>
                <h3 className="relative mt-4 text-2xl font-black text-nt-text-primary">¡Colección completada!</h3>
                <p className="relative mt-2 font-bold text-slate-700">
                  Has desbloqueado todos los logros disponibles por ahora.
                </p>
                <p className="relative mt-2 text-sm font-semibold text-nt-text-secondary">
                  Sigue aprendiendo para descubrir nuevos retos cuando estén disponibles.
                </p>
              </div>
            )}
          </section>
        </>
      )}
    </StudentLayout>
  );
}

export default Achievements;
