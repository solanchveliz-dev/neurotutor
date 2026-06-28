import { BookOpenCheck, Sparkles } from "lucide-react";

function ProgressSummaryCard({ progress, isLoading = false, error = "" }) {
  const percentage = Math.min(100, Math.max(0, Number(progress?.overall_progress) || 0));
  const modules = Array.isArray(progress?.modules) ? progress.modules : [];

  return (
    <section className="rounded-nt-card border border-white/85 bg-white/95 p-5 shadow-nt-card">
      <div className="flex items-center gap-3">
        <div className="grid size-12 place-items-center rounded-[20px] bg-nt-purple/10 text-nt-purple">
          <Sparkles className="size-6" />
        </div>
        <div>
          <p className="text-xs font-black uppercase text-nt-purple">Resumen real</p>
          <h2 className="text-xl font-black text-nt-text-primary">Progreso general</h2>
        </div>
      </div>

      {isLoading ? (
        <div className="mt-5 h-28 animate-pulse rounded-[22px] bg-nt-sky/70" />
      ) : error ? (
        <p className="mt-5 rounded-[20px] border border-amber-200 bg-amber-50 p-4 text-sm font-bold text-amber-800">{error}</p>
      ) : (
        <>
          <div className="mt-5 flex items-end justify-between gap-4">
            <span className="text-5xl font-black text-nt-text-primary">{percentage}%</span>
            <span className="rounded-full bg-nt-blue/10 px-3 py-1 text-xs font-black text-nt-blue">
              {modules.length} {modules.length === 1 ? "nivel" : "niveles"} con actividad
            </span>
          </div>
          <div className="mt-3 h-3 overflow-hidden rounded-full bg-nt-border">
            <div className="h-full rounded-full bg-gradient-to-r from-nt-blue to-nt-purple" style={{ width: `${percentage}%` }} />
          </div>
          <div className="mt-4 flex gap-3 rounded-[20px] bg-nt-sky/60 p-4">
            <BookOpenCheck className="mt-0.5 size-5 shrink-0 text-nt-blue" />
            <p className="text-sm font-bold leading-6 text-nt-text-secondary">
              Tu progreso se calcula según teoría, práctica y examen final completados.
            </p>
          </div>
          {modules.length === 0 && (
            <p className="mt-4 text-sm font-semibold text-nt-text-secondary">
              Aún no tienes actividad registrada. Empieza una lección para ver tu avance aquí.
            </p>
          )}
        </>
      )}
    </section>
  );
}

export default ProgressSummaryCard;
