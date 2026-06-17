import { ArrowLeft, BookOpen, Search } from "lucide-react";
import { useEffect, useState } from "react";
import { useLocation, useNavigate, useParams } from "react-router-dom";
import AppSidebar from "../components/layout/AppSidebar";
import StudentLayout from "../components/layout/StudentLayout";
import NeoCard from "../components/student/NeoCard";
import PrimaryButton from "../components/student/PrimaryButton";
import ProgressCard from "../components/student/ProgressCard";
import { modulesData } from "../data/modulesData";
import { getLearningContent } from "../services/learningService";

const numericFallbackMap = {
  1: "fracciones",
  2: "decimales",
  3: "porcentajes",
};

function Theory() {
  const navigate = useNavigate();
  const location = useLocation();
  const { moduleId, levelId } = useParams();
  const [theoryHtml, setTheoryHtml] = useState("");
  const [isLoading, setIsLoading] = useState(true);
  const [isUsingFallback, setIsUsingFallback] = useState(false);

  const fallbackId = numericFallbackMap[moduleId] ?? moduleId;
  const fallbackModule =
    modulesData.find((item) => String(item.id) === String(fallbackId)) ??
    modulesData[0];
  const fallbackLevel =
    fallbackModule?.levels.find((item) => String(item.id) === String(levelId)) ??
    fallbackModule?.levels.find((item) => item.unlocked) ??
    fallbackModule?.levels[0];

  const module = location.state?.module ?? {
    id: moduleId,
    title: fallbackModule?.title ?? "Modulo",
  };
  const level = location.state?.level ?? {
    id: levelId,
    name: fallbackLevel?.name ?? "Nivel",
    backendTitle: fallbackLevel?.name ?? "Teoria del nivel",
    progress: fallbackLevel?.progress ?? 0,
    status: fallbackLevel?.status ?? "Disponible",
  };

  useEffect(() => {
    setIsLoading(true);
    setIsUsingFallback(false);

    getLearningContent(levelId)
      .then((content) => {
        setTheoryHtml(content?.teoriaHtml ?? "");
        setIsUsingFallback(!content?.teoriaHtml);
      })
      .catch(() => {
        setTheoryHtml("");
        setIsUsingFallback(true);
      })
      .finally(() => setIsLoading(false));
  }, [levelId]);

  const sidebarItems = [
    { label: "Inicio", onClick: () => navigate("/student-dashboard") },
    { label: "Modulos", active: true, onClick: () => navigate(`/module/${moduleId}`, { state: { module } }) },
    { label: "Mis logros", onClick: () => navigate("/learning-path") },
    { label: "Perfil", onClick: () => navigate("/student-dashboard") },
  ];

  return (
    <StudentLayout
      sidebar={<AppSidebar items={sidebarItems} />}
      topbar={
        <header className="flex w-full flex-col gap-3 rounded-[28px] bg-white/40 px-3 py-2 backdrop-blur-sm md:flex-row md:items-center md:justify-between">
          <button
            type="button"
            className="inline-flex items-center gap-2 text-sm font-black text-nt-blue transition hover:text-nt-purple"
            onClick={() => navigate(`/module/${moduleId}/level/${levelId}`, { state: { module, level } })}
          >
            <ArrowLeft className="size-4" aria-hidden="true" />
            Volver a actividades
          </button>
          <label className="relative min-w-0 flex-1 md:max-w-lg">
            <Search
              className="pointer-events-none absolute left-4 top-1/2 size-4 -translate-y-1/2 text-nt-text-secondary"
              aria-hidden="true"
            />
            <span className="sr-only">Buscar</span>
            <input
              type="search"
              placeholder="Buscar teoria"
              className="h-12 w-full rounded-nt-button border border-white/80 bg-white/90 pl-11 pr-4 text-sm font-semibold text-nt-text-primary shadow-sm outline-none transition placeholder:text-nt-text-secondary focus:border-nt-blue focus:ring-4 focus:ring-nt-blue-light/25"
            />
          </label>
        </header>
      }
      rightPanel={
        <div className="space-y-5">
          <ProgressCard
            title="Teoria"
            subtitle={level.status}
            value={level.progress || 0}
            totalLabel={level.name}
            tone="green"
          />
          <NeoCard
            title="Tip de NEO"
            message="Lee con calma y despues pasa a practica para reforzar lo aprendido."
            actionLabel="Ir a practica"
            onAction={() => navigate(`/practice/${levelId}`, { state: { module, level } })}
          />
        </div>
      }
    >
      <section className="rounded-nt-card border border-white/80 bg-white/90 p-5 shadow-nt-card backdrop-blur">
        <span className="inline-flex rounded-full bg-nt-green px-3 py-1 text-xs font-black text-white shadow-lg shadow-nt-green/25">
          {module.title}
        </span>
        <h1 className="mt-4 text-3xl font-black leading-tight text-nt-text-primary md:text-4xl">
          Teoria - {level.name}
        </h1>
        <p className="mt-2 text-sm font-black text-nt-blue">{level.backendTitle}</p>
      </section>

      <section className="rounded-nt-card border border-white/80 bg-white/90 p-5 shadow-nt-card backdrop-blur">
        <div className="mb-4 flex items-center gap-3">
          <div className="grid size-11 place-items-center rounded-[18px] bg-nt-green/15 text-green-700">
            <BookOpen className="size-5" aria-hidden="true" />
          </div>
          <div>
            <h2 className="text-xl font-black text-nt-text-primary">Contenido de teoria</h2>
            <p className="text-sm font-semibold text-nt-text-secondary">
              Revisa los conceptos antes de practicar.
            </p>
          </div>
        </div>

        {isLoading ? (
          <div className="h-40 animate-pulse rounded-[18px] bg-nt-sky/70" />
        ) : theoryHtml ? (
          <div
            className="prose prose-sm max-w-none text-nt-text-primary"
            dangerouslySetInnerHTML={{ __html: theoryHtml }}
          />
        ) : (
          <div className="rounded-[18px] bg-nt-sky/70 p-4 text-sm font-semibold leading-6 text-nt-text-primary">
            {isUsingFallback
              ? "No se pudo cargar la teoria del servidor. Revisa el contenido con tu docente antes de practicar."
              : "La teoria de este nivel aun no esta disponible."}
          </div>
        )}

        <div className="mt-5 flex flex-wrap gap-3">
          <PrimaryButton
            type="button"
            onClick={() => navigate(`/module/${moduleId}/level/${levelId}`, { state: { module, level } })}
            className="bg-slate-100 text-nt-text-primary shadow-none hover:bg-slate-200"
          >
            Volver a actividades
          </PrimaryButton>
          <PrimaryButton
            type="button"
            onClick={() => navigate(`/practice/${levelId}`, { state: { module, level } })}
          >
            Ir a practica
          </PrimaryButton>
        </div>
      </section>
    </StudentLayout>
  );
}

export default Theory;
