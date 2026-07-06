import { useEffect, useState } from "react";
import { BookOpenCheck, Clock3, Pencil, Trophy as Medal } from "lucide-react";
import { getModuleProgress } from "@/services/progressService";

const clampPercentage = (value) => Math.min(100, Math.max(0, Number(value) || 0));

const formatActivityDate = (value) => {
  if (!value) return "Aún no registras actividad";
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return "Actividad registrada";
  return new Intl.DateTimeFormat("es-PE", {
    day: "numeric",
    month: "short",
    year: "numeric",
  }).format(date);
};

function StatusRow({ complete, icon: Icon, label, weight, detail, iconTone }) {
  return (
    <div className="flex items-center gap-2.5 rounded-[17px] border border-sky-100/80 bg-sky-50/90 px-3 py-2.5">
      <div className={`grid size-9 shrink-0 place-items-center rounded-[13px] shadow-sm ${iconTone ?? (weight === 34 ? "bg-gradient-to-br from-violet-500 to-purple-700 text-amber-200" : "bg-gradient-to-br from-blue-500 to-indigo-600 text-white")}`}>
        <Icon className="size-[18px]" />
      </div>
      <div className="min-w-0 flex-1">
        <div className="flex items-center justify-between gap-2">
          <p className="text-sm font-black text-nt-text-primary">{label}</p>
          <span className="text-xs font-black text-nt-blue">{weight}%</span>
        </div>
        <p className="truncate text-xs font-semibold text-nt-text-secondary">
          {complete ? "Completada" : detail}
        </p>
      </div>
    </div>
  );
}

function LearningProgressPanel({ studentId, moduloId, progress: providedProgress, refreshKey = 0, title = "Progreso del nivel", compact = false }) {
  const [progress, setProgress] = useState(providedProgress ?? null);
  const [isLoading, setIsLoading] = useState(!providedProgress);
  const [error, setError] = useState("");

  useEffect(() => {
    if (providedProgress) {
      setProgress(providedProgress);
      setIsLoading(false);
      setError("");
      return;
    }

    if (!studentId || !moduloId) {
      setProgress(null);
      setIsLoading(false);
      setError("No pudimos identificar este nivel.");
      return;
    }

    let active = true;
    setIsLoading(true);
    setError("");
    getModuleProgress(studentId, moduloId)
      .then((data) => {
        if (active) setProgress(data);
      })
      .catch(() => {
        if (active) {
          setProgress(null);
          setError("No se pudo sincronizar tu progreso.");
        }
      })
      .finally(() => {
        if (active) setIsLoading(false);
      });

    return () => {
      active = false;
    };
  }, [studentId, moduloId, providedProgress, refreshKey]);

  const percentage = clampPercentage(progress?.progress_percentage);

  return (
    <section className={`w-full rounded-[26px] border border-white bg-white shadow-[0_14px_34px_rgba(30,58,138,0.11)] ${compact ? "p-4" : "p-5"}`}>
      <div className="flex items-start justify-between gap-3">
        <div>
          <p className="text-xs font-black uppercase text-nt-blue">Aprendizaje</p>
          <h2 className={`${compact ? "mt-0.5 text-lg" : "mt-1 text-xl"} font-black text-nt-text-primary`}>{title}</h2>
          {progress?.title && <p className="mt-1 text-sm font-bold text-nt-text-secondary">{progress.title}</p>}
        </div>
        <div className={`grid shrink-0 place-items-center rounded-full bg-gradient-to-br from-nt-blue to-nt-purple font-black text-white shadow-lg shadow-nt-blue/25 ${compact ? "size-12 text-base" : "size-14 text-lg"}`}>
          {isLoading ? "…" : `${percentage}%`}
        </div>
      </div>

      {isLoading ? (
        <div className="mt-5 grid gap-3">
          {[1, 2, 3].map((item) => <div key={item} className="h-16 animate-pulse rounded-[20px] bg-nt-sky/70" />)}
        </div>
      ) : error ? (
        <div className="mt-5 rounded-[20px] border border-amber-200 bg-amber-50 p-4 text-sm font-bold text-amber-800">
          {error} Intenta nuevamente al volver a esta pantalla.
        </div>
      ) : (
        <>
          <div className={`${compact ? "mt-3 h-2.5" : "mt-5 h-3"} overflow-hidden rounded-full bg-nt-border`}>
            <div className="h-full rounded-full bg-gradient-to-r from-nt-blue via-nt-purple to-nt-green transition-all" style={{ width: `${percentage}%` }} />
          </div>
          <p className="mt-2 text-[11px] font-semibold leading-4 text-nt-text-secondary">
            El total se forma con teoría (33%), práctica (33%) y examen final aprobado (34%).
          </p>

          <div className="mt-3 grid gap-2">
            <StatusRow complete={progress?.theory_completed} icon={BookOpenCheck} label="Teoría" weight={33} detail="Pendiente" />
            <StatusRow
              complete={progress?.practice_completed}
              icon={Pencil}
              iconTone="bg-gradient-to-br from-emerald-400 to-green-600 text-white"
              label="Práctica"
              weight={33}
              detail={progress?.practice_total_count ? `${progress.practice_completed_count}/${progress.practice_total_count} respuestas correctas` : "Pendiente"}
            />
            <StatusRow complete={progress?.exam_passed} icon={Medal} label="Examen final" weight={34} detail="Pendiente de aprobar" />
          </div>

          <div className="mt-3 grid gap-2 rounded-[17px] border border-amber-100 bg-amber-50/90 p-3 text-xs font-bold text-nt-text-secondary">
            <div className="flex items-center justify-between gap-3">
              <span className="flex items-center gap-2 text-amber-800"><Medal className="size-4 text-amber-500" />Mejor examen</span>
              <strong className="text-nt-text-primary">{progress?.exam_best_score ? `${progress.exam_best_score}%` : "Sin intento"}</strong>
            </div>
            <div className="flex items-center gap-2 border-t border-nt-border pt-2">
              <Clock3 className="size-4 text-nt-blue" />
              <span>{formatActivityDate(progress?.last_activity_at)}</span>
            </div>
          </div>
        </>
      )}
    </section>
  );
}

export default LearningProgressPanel;
