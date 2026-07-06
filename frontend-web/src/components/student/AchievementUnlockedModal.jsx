import { Button } from "@/components/ui/button";
import { getAchievementImage } from "@/utils/achievementVisuals";

const details = {
  DIAGNOSTIC_COMPLETED: ["Primer paso", "Completaste tu diagnóstico inicial."],
  FIRST_THEORY_COMPLETED: ["Mente curiosa", "Completaste tu primera teoría."],
  FIRST_PRACTICE_PASSED: ["Manos a la práctica", "Aprobaste tu primera práctica."],
  FIRST_EXAM_PASSED: ["Examen superado", "Aprobaste tu primer examen final."],
  FIRST_MODULE_COMPLETED: ["Módulo dominado", "Completaste teoría, práctica y examen de un nivel."],
  BASIC_LEVEL_COMPLETED: ["Nivel Básico completado", "Completaste el Nivel Básico."],
  INTERMEDIATE_LEVEL_COMPLETED: ["Nivel Intermedio completado", "Completaste el Nivel Intermedio."],
  ADVANCED_LEVEL_COMPLETED: ["Nivel Avanzado completado", "Completaste el Nivel Avanzado."],
  POINTS_100: ["Centena brillante", "Alcanzaste 100 puntos en NeuroTutor."],
};

function AchievementUnlockedModal({ code, onClose, onContinue }) {
  if (!code || !details[code]) return null;
  const [title, message] = details[code];
  const handleClose = onClose ?? onContinue;

  return <div className="fixed inset-0 z-[100] grid place-items-center bg-slate-950/35 p-4 backdrop-blur-sm" role="dialog" aria-modal="true" aria-labelledby="achievement-modal-title"><section className="w-full max-w-md rounded-[30px] border border-amber-200 bg-white p-7 text-center shadow-[0_28px_80px_rgba(30,58,138,0.28)]"><p className="text-sm font-black uppercase tracking-wide text-nt-purple">¡Nueva insignia desbloqueada!</p><img src={getAchievementImage(code)} alt={`Insignia ${title}`} className="mx-auto mt-3 h-40 w-full object-contain" /><h2 id="achievement-modal-title" className="mt-3 text-3xl font-black text-nt-text-primary">{title}</h2><p className="mt-2 font-bold text-slate-600">{message}</p><Button type="button" onClick={handleClose} className="mt-6 h-12 w-full rounded-[16px] bg-gradient-to-r from-nt-blue to-nt-purple font-black text-white">Continuar</Button></section></div>;
}

export default AchievementUnlockedModal;
