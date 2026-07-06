import { useLocation, useNavigate } from "react-router-dom";
import { ArrowRight, BookOpen, Check, CheckCircle2, ClipboardList, Crown, Map, MessageSquare } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import AchievementUnlockedModal from "../components/student/AchievementUnlockedModal";

const LEVEL_ORDER = ["Básico", "Intermedio", "Avanzado"];

const LEVEL_CONFIG = {
  Básico: {
    label: "BÁSICO",
    image: "/assets/result_basico.png",
    badge: "/assets/logro_basico.png",
    explanation: "Comenzaremos desde el Nivel Básico para reforzar las bases.",
    learn: ["Conceptos iniciales de matemáticas.", "Representación de fracciones.", "Numerador y denominador.", "Fracciones propias e impropias."],
    advice: "Cada gran aventura comienza con el primer paso. Estoy seguro de que aprenderás muchísimo.",
    text: "text-green-700",
    surface: "border-emerald-200 bg-emerald-50/70",
    button: "from-emerald-500 to-green-700",
    dot: "bg-green-600",
  },
  Intermedio: {
    label: "INTERMEDIO",
    image: "/assets/result_intermedio.png",
    badge: "/assets/logro_intermedio.png",
    explanation: "Ya conoces algunas bases y ahora resolverás retos con más estrategia.",
    learn: ["Fracciones equivalentes.", "Suma y resta de fracciones.", "Multiplicación y división.", "Problemas cotidianos."],
    advice: "Ya conoces las bases. Ahora aprenderemos a resolver problemas más interesantes. Estoy listo para acompañarte.",
    text: "text-violet-700",
    surface: "border-violet-200 bg-violet-50/70",
    button: "from-violet-600 to-purple-800",
    dot: "bg-violet-600",
  },
  Avanzado: {
    label: "AVANZADO",
    image: "/assets/result_avanzado.png",
    badge: "/assets/logro_avanzado.png",
    explanation: "Dominas varios conceptos y puedes enfrentar desafíos más complejos.",
    learn: ["Simplificación.", "Comparación.", "Operaciones combinadas.", "Resolución de problemas reales."],
    advice: "¡Excelente! Ahora enfrentarás desafíos más complejos. Estoy listo para acompañarte en cada paso.",
    text: "text-red-700",
    surface: "border-red-200 bg-red-50/70",
    button: "from-red-600 to-red-800",
    dot: "bg-red-600",
  },
};

function DiagnosticResult() {
  const navigate = useNavigate();
  const location = useLocation();
  const score = Number(location.state?.correctAnswers ?? location.state?.score ?? 0);
  const total = Number(location.state?.totalQuestions ?? location.state?.total ?? 10);
  const percentage = Number(location.state?.scorePercentage ?? location.state?.score_percentage ?? (total ? Math.round((score / total) * 100) : 0));
  const levelMap = { BASICO: "Básico", INTERMEDIO: "Intermedio", AVANZADO: "Avanzado", Basico: "Básico" };
  const rawLevel = location.state?.assignedLevel ?? location.state?.level ?? "Básico";
  const level = levelMap[rawLevel] ?? rawLevel;
  const config = LEVEL_CONFIG[level] ?? LEVEL_CONFIG.Básico;
  const activeIndex = LEVEL_ORDER.indexOf(level);
  const unlockedAchievementCode = location.state?.unlockedAchievementCodes?.[0];

  return (
    <main className="relative min-h-screen overflow-hidden bg-[url('/assets/fondo_diagnostic.png')] bg-cover bg-center bg-fixed px-3 py-6 text-nt-text-primary sm:px-5">
      <section className="relative mx-auto w-full max-w-6xl">
        <Card className="overflow-hidden rounded-[36px] border border-white/90 bg-white/94 p-0 shadow-[0_28px_80px_rgba(30,58,138,0.2)] backdrop-blur-xl"><CardContent className="p-5 sm:p-8">
          <header className="text-center"><h1 className={`text-3xl font-black leading-tight sm:text-5xl ${config.text}`}>¡Diagnóstico completado! 🎉</h1><p className="mt-2 text-base font-bold text-slate-500 sm:text-lg">Aquí tienes tu resultado inicial</p></header>

          <section className="mt-6 grid items-stretch gap-5 lg:grid-cols-[44%_56%]">
            <div className={`grid content-between rounded-[30px] border p-5 text-center shadow-sm ${config.surface}`}><div><p className="text-xs font-black uppercase tracking-wider text-slate-600">Tu nivel detectado</p><h2 className={`mt-2 text-3xl font-black sm:text-4xl ${config.text}`}>{config.label}</h2><img src={config.badge} alt={`Insignia del nivel ${level}`} className="mx-auto mt-4 h-56 w-full object-contain drop-shadow-lg" /></div><p className="mt-4 rounded-full bg-white/90 px-4 py-3 text-xl font-black">{score} / {total} aciertos</p></div>
            <div className="grid min-h-[360px] place-items-center overflow-hidden rounded-[30px] bg-gradient-to-br from-white/50 to-sky-50/50"><img src={config.image} alt={`Resultado del nivel ${level}`} className="h-full max-h-[470px] w-full object-contain object-bottom drop-shadow-[0_18px_32px_rgba(30,58,138,0.18)]" /></div>
          </section>

          <section className="mt-5 grid gap-5 rounded-[28px] border border-slate-100 bg-white/85 p-5 shadow-sm lg:grid-cols-[minmax(0,1.2fr)_repeat(3,minmax(150px,.65fr))]"><div><h2 className="flex items-center gap-2 text-xl font-black"><CheckCircle2 className={`size-7 ${config.text}`} />¿Por qué este nivel?</h2><p className="mt-3 text-sm font-bold leading-6 text-slate-700">Respondiste correctamente <strong className={config.text}>{score} de {total} preguntas.</strong></p><p className="mt-2 text-sm font-semibold leading-6 text-slate-600">{config.explanation}</p><p className="mt-2 text-sm font-bold text-slate-700">Por eso comenzaremos desde: <strong className={config.text}>Nivel {level}.</strong></p></div>{[{ icon: CheckCircle2, label: "Correctas", value: score, tone: "text-green-600" }, { icon: ClipboardList, label: "Preguntas", value: total, tone: "text-blue-600" }, { icon: Crown, label: "Nivel", value: level, tone: config.text }].map(({ icon: Icon, label, value, tone }) => <article key={label} className="grid min-h-40 place-items-center rounded-[22px] border border-slate-100 bg-white p-4 text-center shadow-sm"><Icon className={`size-10 ${tone}`} /><p className={`font-black ${tone}`}>{label}</p><strong className="text-2xl font-black">{value}</strong></article>)}</section>

          <div className="mt-5 grid gap-5 lg:grid-cols-2"><section className="rounded-[26px] border border-slate-100 bg-white/90 p-5 shadow-sm"><h2 className="flex items-center gap-2 text-xl font-black"><BookOpen className={config.text} />¿Qué aprenderás?</h2><div className="mt-4 grid gap-3">{config.learn.map((item) => <p key={item} className="flex items-start gap-3 text-sm font-bold text-slate-700"><Check className={`size-5 shrink-0 ${config.text}`} strokeWidth={3} />{item}</p>)}</div></section><section className={`rounded-[26px] border p-5 shadow-sm ${config.surface}`}><h2 className="flex items-center gap-2 text-xl font-black"><MessageSquare className={config.text} />Consejo de NEO</h2><p className="mt-5 rounded-[20px] bg-white/90 p-5 text-sm font-bold leading-7 text-slate-700 shadow-sm">{config.advice}</p></section></div>

          <section className="mt-5 rounded-[26px] border border-slate-100 bg-white/90 p-5 shadow-sm"><h2 className="flex items-center gap-2 text-xl font-black"><Map className={config.text} />Tu ruta de aprendizaje</h2><div className="mt-5 grid gap-4 sm:grid-cols-3">{LEVEL_ORDER.map((item, index) => { const active = index === activeIndex; const complete = index < activeIndex; return <div key={item} className="relative text-center"><div className={`mx-auto grid size-20 place-items-center rounded-full border-4 text-lg font-black ${active ? `${config.surface} ${config.text}` : complete ? "border-green-200 bg-green-50 text-green-700" : "border-slate-200 bg-slate-50 text-slate-400"}`}>{complete ? <CheckCircle2 className="size-9" /> : index + 1}</div><p className={`mt-2 font-black ${active ? config.text : complete ? "text-green-700" : "text-slate-400"}`}>{item}</p>{index < 2 && <span className={`absolute left-[65%] top-10 hidden h-1 w-[70%] rounded-full sm:block ${index < activeIndex ? config.dot : "bg-slate-200"}`} />}</div>; })}</div></section>

          <div className="mt-6 text-center"><p className="text-xs font-bold text-slate-500">Resultado: {percentage}%</p><Button type="button" onClick={() => navigate('/student-dashboard')} className={`mt-2 h-14 w-full max-w-xl rounded-[18px] bg-gradient-to-r text-base font-black text-white shadow-lg ${config.button}`}>Comenzar mi aventura<ArrowRight className="size-5" /></Button></div>
        </CardContent></Card>
      </section>
      <AchievementUnlockedModal code={unlockedAchievementCode} onContinue={() => navigate("/student-dashboard", { replace: true })} />
    </main>
  );
}

export default DiagnosticResult;
