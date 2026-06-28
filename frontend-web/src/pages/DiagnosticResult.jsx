import { useLocation, useNavigate } from "react-router-dom";
import { ClipboardCheck } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";

function getStoredDiagnosticResult() {
  try {
    const result = localStorage.getItem("diagnosticResult");
    return result ? JSON.parse(result) : null;
  } catch {
    return null;
  }
}

function DiagnosticResult() {
  const navigate = useNavigate();
  const location = useLocation();
  const storedResult = getStoredDiagnosticResult();

  const attemptId = location.state?.attemptId;
  const score = location.state?.correctAnswers ?? location.state?.score ?? 0;
  const total = location.state?.totalQuestions ?? location.state?.total ?? 10;
  const levelMap = {
    BASICO: "Básico",
    INTERMEDIO: "Intermedio",
    AVANZADO: "Avanzado",
  };

  const level =
    levelMap[location.state?.assignedLevel] ??
    levelMap[storedResult?.nivel] ??
    location.state?.level ??
    "Básico";

  const levelConfig = {
    Basico: {
      label: "BÁSICO",
      image: "/assets/img_basico.png",
      tone: "from-green-600 to-emerald-500",
      cardTint: "bg-green-50 border-green-200 shadow-green-100",
      levelText: "text-green-600",
      message:
        "¡Buen inicio! He preparado una ruta especial para ayudarte a fortalecer tus bases en matemática. 🌱",
    },
    Intermedio: {
      label: "INTERMEDIO",
      image: "/assets/img_intermedio.webp",
      tone: "from-blue-600 to-violet-600",
      cardTint: "bg-blue-50 border-blue-200 shadow-blue-100",
      levelText: "text-blue-600",
      message:
        "¡Muy bien! Tu nivel es Intermedio. He preparado una ruta especial para seguir fortaleciendo tus conocimientos en matemática. 🔥",
    },
    Avanzado: {
      label: "AVANZADO",
      image: "/assets/img_avanzado.webp",
      tone: "from-violet-600 to-fuchsia-500",
      cardTint: "bg-violet-50 border-violet-200 shadow-violet-100",
      levelText: "text-violet-600",
      message:
        "¡Excelente esfuerzo! Estás listo para resolver retos más avanzados y seguir potenciando tus habilidades matemáticas. 🚀",
    },
  };

  const config = levelConfig[level] || levelConfig.Basico;
  const reviewState = {
    attemptId,
    score,
    total,
    level,
    answers: location.state?.answers ?? {},
    isFallback: location.state?.isFallback === true,
  };

  return (
    <main className="relative min-h-screen overflow-hidden bg-[url('/assets/fondo_diagnostic.png')] bg-cover bg-center bg-no-repeat px-4 py-8 text-nt-text-primary">
      <div className="pointer-events-none absolute inset-0 bg-white/5" />
      <div className="pointer-events-none absolute left-8 top-10 hidden h-28 w-28 rounded-full bg-nt-yellow/35 blur-3xl md:block" />
      <div className="pointer-events-none absolute bottom-8 right-10 hidden h-36 w-36 rounded-full bg-nt-purple-light/30 blur-3xl md:block" />

      <section className="relative mx-auto flex min-h-[calc(100vh-4rem)] w-full max-w-3xl items-center justify-center">
        <Card className="w-full rounded-[34px] border border-white/85 bg-white/90 p-0 shadow-[0_24px_70px_rgba(37,99,235,0.18)] backdrop-blur-xl">
          <CardContent className="p-6 text-center sm:p-9">
            <h1 className="text-3xl font-black leading-tight text-nt-text-primary sm:text-4xl">
              ¡Diagnóstico completado! 🎉
            </h1>
            <p className="mt-2 text-sm font-bold text-nt-text-secondary sm:text-base">
              Aquí tienes tu resultado inicial
            </p>

            <div className={`mx-auto mt-7 max-w-xl rounded-[32px] border p-6 shadow-lg ${config.cardTint}`}>
              <p className="text-xs font-black uppercase tracking-wide text-nt-text-secondary">
                Tu nivel detectado
              </p>
              <div className={`mx-auto mt-2 text-3xl font-black tracking-wide sm:text-4xl ${config.levelText}`}>
                {config.label}
              </div>

              <img
                src={config.image}
                alt={`Nivel ${config.label}`}
                className="mx-auto mt-5 h-44 w-44 object-contain drop-shadow-[0_18px_28px_rgba(30,58,138,0.18)] sm:h-56 sm:w-56"
              />

              <div className="mt-5 rounded-[24px] border border-white/90 bg-white/86 px-5 py-4">
                <p className="text-lg font-black text-nt-text-primary">
                  {score} / {total} aciertos
                </p>
              </div>
            </div>

            <div className="mx-auto mt-5 flex max-w-2xl items-center gap-4 rounded-[28px] border border-white/80 bg-gradient-to-r from-white/88 to-nt-sky/72 px-4 py-4 text-left shadow-[0_18px_42px_rgba(37,99,235,0.14)] backdrop-blur-xl sm:px-5">
              <img
                src="/assets/neo_diagnostic_result.webp"
                alt=""
                aria-hidden="true"
                className="w-16 shrink-0 object-contain drop-shadow-[0_14px_22px_rgba(30,58,138,0.18)] sm:w-24"
              />
              <p className="text-sm font-bold leading-6 text-nt-text-secondary sm:text-base">
                {config.message}
              </p>
            </div>

            <Button
              type="button"
              className="mt-7 h-12 w-full max-w-sm rounded-[18px] bg-gradient-to-r from-nt-blue to-nt-purple text-sm font-black text-white shadow-[0_16px_30px_rgba(37,99,235,0.24)] hover:from-nt-blue/90 hover:to-nt-purple/90"
              onClick={() => navigate("/diagnostic-review", { state: reviewState })}
            >
              <ClipboardCheck className="size-4" aria-hidden="true" />
              Ver mis respuestas
            </Button>
          </CardContent>
        </Card>
      </section>
    </main>
  );
}

export default DiagnosticResult;
