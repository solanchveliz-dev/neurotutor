import { useEffect, useState } from "react";
import { Award, BookOpenCheck, Check, ClipboardCheck, LockKeyhole, Medal, PencilRuler, Star, Trophy } from "lucide-react";
import { useNavigate } from "react-router-dom";
import AppSidebar from "../components/layout/AppSidebar";
import StudentLayout from "../components/layout/StudentLayout";
import BackButton from "../components/student/BackButton";
import PrimaryButton from "../components/student/PrimaryButton";
import { getStudentAchievements } from "../services/achievementService";
import { getStudentId } from "../utils/auth";

const iconMap = {
  "clipboard-check": ClipboardCheck,
  "book-open-check": BookOpenCheck,
  "pencil-ruler": PencilRuler,
  trophy: Trophy,
  medal: Medal,
  star: Star,
};

const formatDate = (value) => {
  if (!value) return "";
  return new Intl.DateTimeFormat("es-PE", { day: "numeric", month: "short", year: "numeric" })
    .format(new Date(value));
};

function AchievementCard({ achievement, unlocked }) {
  const Icon = iconMap[achievement.icon] ?? Award;

  return (
    <article className={`relative overflow-hidden rounded-nt-card border p-5 shadow-nt-card ${unlocked ? "border-white/90 bg-white/95" : "border-slate-200 bg-white/75"}`}>
      <div className={`grid size-16 place-items-center rounded-[22px] ${unlocked ? "bg-gradient-to-br from-nt-purple to-nt-blue text-white shadow-lg shadow-nt-purple/20" : "bg-slate-100 text-slate-400"}`}>
        {unlocked ? <Icon className="size-8" /> : <LockKeyhole className="size-7" />}
      </div>
      <div className="mt-4 flex items-start justify-between gap-3">
        <div>
          <p className={`text-xs font-black uppercase ${unlocked ? "text-nt-purple" : "text-slate-500"}`}>
            {achievement.category}
          </p>
          <h3 className="mt-1 text-xl font-black text-nt-text-primary">{achievement.title}</h3>
        </div>
        {unlocked && (
          <span className="grid size-8 shrink-0 place-items-center rounded-full bg-nt-green/15 text-green-700">
            <Check className="size-5" />
          </span>
        )}
      </div>
      <p className="mt-2 text-sm font-semibold leading-6 text-nt-text-secondary">{achievement.description}</p>
      <p className="mt-4 text-xs font-black text-nt-text-secondary">
        {unlocked ? `Desbloqueado el ${formatDate(achievement.unlocked_at)}` : achievement.points_required ? `Requiere ${achievement.points_required} puntos` : "Sigue avanzando para desbloquearlo"}
      </p>
    </article>
  );
}

function Achievements() {
  const navigate = useNavigate();
  const [data, setData] = useState({ unlocked: [], locked: [] });
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState("");

  const sidebarItems = [
    { label: "Inicio", onClick: () => navigate("/student-dashboard") },
    { label: "Módulos", onClick: () => navigate("/student-dashboard") },
    { label: "Mis Logros", active: true, onClick: () => navigate("/achievements") },
    { label: "Perfil", onClick: () => navigate("/profile") },
  ];

  const loadAchievements = async () => {
    const studentId = getStudentId();
    if (!studentId) {
      setError("No pudimos identificar al estudiante conectado.");
      setIsLoading(false);
      return;
    }

    setIsLoading(true);
    setError("");
    try {
      const response = await getStudentAchievements(studentId);
      setData({
        unlocked: Array.isArray(response?.unlocked) ? response.unlocked : [],
        locked: Array.isArray(response?.locked) ? response.locked : [],
      });
    } catch {
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
    >
      <section className="rounded-nt-card border border-white/80 bg-white/90 p-6 shadow-nt-card backdrop-blur">
        <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
          <div>
            <span className="inline-flex rounded-full bg-nt-purple/10 px-3 py-1 text-xs font-black text-nt-purple">Tu colección</span>
            <h1 className="mt-3 text-3xl font-black text-nt-text-primary md:text-4xl">Mis Logros</h1>
            <p className="mt-2 text-sm font-semibold text-nt-text-secondary">Cada insignia refleja un avance guardado en NeuroTutor.</p>
          </div>
          <div className="rounded-[24px] bg-gradient-to-br from-nt-blue to-nt-purple px-6 py-4 text-center text-white shadow-lg shadow-nt-blue/20">
            <p className="text-3xl font-black">{data.unlocked.length}</p>
            <p className="text-xs font-black">desbloqueados</p>
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
          <section>
            <h2 className="text-2xl font-black text-nt-text-primary">Desbloqueados</h2>
            {data.unlocked.length ? (
              <div className="mt-4 grid gap-4 md:grid-cols-2 xl:grid-cols-3">
                {data.unlocked.map((item) => <AchievementCard key={item.id} achievement={item} unlocked />)}
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
              <div className="mt-4 grid gap-4 md:grid-cols-2 xl:grid-cols-3">
                {data.locked.map((item) => <AchievementCard key={item.id} achievement={item} unlocked={false} />)}
              </div>
            ) : (
              <div className="mt-4 rounded-nt-card border border-white/80 bg-white/85 p-6 text-center font-bold text-nt-text-secondary shadow-nt-card">
                Has desbloqueado todos los logros disponibles.
              </div>
            )}
          </section>
        </>
      )}
    </StudentLayout>
  );
}

export default Achievements;
