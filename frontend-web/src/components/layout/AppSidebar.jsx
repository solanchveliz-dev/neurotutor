import { ArrowUpRight, BookOpen, GraduationCap, Home, LogOut, Sparkles, Trophy, UserRound } from "lucide-react";
import { useState } from "react";
import { useNavigate, useParams } from "react-router-dom";

import { Button } from "@/components/ui/button";
import { cn } from "@/lib/utils";
import { clearAuthData, getStudentId } from "@/utils/auth";
import { getCachedStudentData } from "@/utils/studentDataCache";
import {
  getRememberedModuleId,
  resolveCurrentModuleId,
} from "@/utils/moduleNavigation";

const defaultItems = [
  { label: "Inicio", icon: Home, active: true },
  { label: "Modulos", icon: BookOpen },
  { label: "Mis logros", icon: Trophy },
  { label: "Perfil", icon: UserRound },
];

function AppSidebar({
  items = defaultItems,
  footer,
  className,
}) {
  const navigate = useNavigate();
  const { moduleId: routeModuleId } = useParams();
  const visibleItems = items;
  const [moduleDestinationId] = useState(() => {
    const studentId = getStudentId();
    const cachedModules = getCachedStudentData(studentId, "modules") ?? [];
    return routeModuleId
      ?? getCachedStudentData(studentId, "resolvedModuleId")
      ?? getRememberedModuleId()
      ?? resolveCurrentModuleId(cachedModules);
  });
  const [moduleNavigationMessage, setModuleNavigationMessage] = useState("");
  const isResolvingModule = false;
  const moduleLoadError = null;
  const pendingModuleNavigation = { current: false };
  /* Disabled legacy network resolution: the shared sidebar must not fetch.
  useEffect(() => {
    let isMounted = true;
    const studentId = getStudentId();

    if (!studentId) {
      const error = new Error("No se encontró el estudiante conectado.");
      setModuleLoadError(error);
      setIsResolvingModule(false);
      console.log("loading modules", false);
      console.log("error modules", error);
      return undefined;
    }

    console.log("loading modules", true);

    Promise.allSettled([
      getStudentDashboard(studentId),
      getStudentProgress(studentId),
    ]).then(([dashboardResult, progressResult]) => {
      if (!isMounted) return;

      const dashboard = dashboardResult.status === "fulfilled" ? dashboardResult.value : null;
      const progress = progressResult.status === "fulfilled" ? progressResult.value : null;
      const dashboardModules = Array.isArray(dashboard?.modulos) ? dashboard.modulos.filter(Boolean) : [];
      const progressModules = Array.isArray(progress?.modules) ? progress.modules.filter(Boolean) : [];
      const progressById = new Map(progressModules.map((module) => [String(getModuleId(module)), module]));
      const availableModules = dashboardModules.length
        ? dashboardModules.map((module) => ({
            ...module,
            progress_percentage: progressById.get(String(getModuleId(module)))?.progress_percentage,
            unlocked: module.estado !== "BLOQUEADO",
          }))
        : progressModules.map((module) => ({
            ...module,
            unlocked: true,
            status: Number(module.progress_percentage) >= 100
              ? "COMPLETADO"
              : Number(module.progress_percentage) > 0
                ? "EN_CURSO"
                : "DISPONIBLE",
          }));

      const resolvedModuleId = resolveCurrentModuleId(availableModules);
      const requestError = dashboardResult.status === "rejected" && progressResult.status === "rejected"
        ? dashboardResult.reason ?? progressResult.reason
        : null;
      const cachedModuleId = routeModuleId ?? getRememberedModuleId();
      const destinationId = resolvedModuleId ?? cachedModuleId ?? null;

      console.log("modules sidebar", availableModules);
      console.log("resolved module id", resolvedModuleId);
      console.log("loading modules", false);
      console.log("error modules", requestError);

      if (resolvedModuleId !== null && resolvedModuleId !== undefined) {
        rememberCurrentModuleId(resolvedModuleId);
      }
      setModuleLoadError(requestError);
      setModuleDestinationId(destinationId);
      setIsResolvingModule(false);

      if (pendingModuleNavigation.current) {
        pendingModuleNavigation.current = false;
        if (destinationId !== null && destinationId !== undefined) {
          setModuleNavigationMessage("");
          navigate(`/module/${destinationId}`);
        } else {
          setModuleNavigationMessage("Aún no tienes módulos disponibles.");
        }
      }
    }).catch((error) => {
      if (!isMounted) return;
      console.log("modules sidebar", []);
      console.log("resolved module id", null);
      console.log("loading modules", false);
      console.log("error modules", error);
      setModuleLoadError(error);
      setIsResolvingModule(false);
      if (pendingModuleNavigation.current) {
        pendingModuleNavigation.current = false;
        setModuleNavigationMessage("Aún no tienes módulos disponibles.");
      }
    });

    return () => {
      isMounted = false;
    };
  }, [navigate, routeModuleId]); */

  const isModulesItem = (label) => label.toLowerCase().includes("mod");

  const handleModulesClick = () => {
    if (moduleDestinationId !== null && moduleDestinationId !== undefined) {
      setModuleNavigationMessage("");
      navigate(`/module/${moduleDestinationId}`);
      return;
    }
    if (isResolvingModule) {
      pendingModuleNavigation.current = true;
      setModuleNavigationMessage("Estamos buscando tu módulo disponible...");
      return;
    }
    console.log("error modules", moduleLoadError);
    setModuleNavigationMessage("Aún no tienes módulos disponibles.");
  };

  const getDefaultAction = (label) => {
    const normalized = label.toLowerCase();
    if (normalized.includes("inicio")) return () => navigate("/student-dashboard");
    if (normalized.includes("mod")) return handleModulesClick;
    if (normalized.includes("logro")) return () => navigate("/achievements");
    if (normalized.includes("perfil")) return () => navigate("/profile");
    return undefined;
  };

  const handleLogout = () => {
    clearAuthData();
    navigate("/login", { replace: true });
  };

  const defaultFooter = (
    <button
      type="button"
      className="flex w-full items-center gap-3 rounded-nt-button px-3 py-3 text-sm font-extrabold text-nt-text-secondary transition hover:bg-nt-sky hover:text-nt-blue"
      onClick={handleLogout}
    >
      <LogOut className="size-5" aria-hidden="true" />
      <span>Cerrar sesion</span>
    </button>
  );

  return (
    <nav
      className={cn(
        "relative z-50 flex h-fit w-full flex-col rounded-nt-card border border-white/80 bg-white/95 p-4 text-nt-text-primary shadow-nt-soft backdrop-blur lg:min-h-[calc(100vh-4rem)]",
        className
      )}
      aria-label="Navegacion principal"
      onClickCapture={(event) => console.log("CLICK CAPTURE SIDEBAR", event.target)}
    >
      <button
        type="button"
        className="mb-5 flex w-full cursor-pointer items-center justify-center rounded-[24px] p-1 transition hover:scale-[1.03] hover:opacity-90 focus:outline-none focus:ring-4 focus:ring-nt-blue-light/30"
        onClick={() => navigate("/student-dashboard")}
        aria-label="Ir al panel principal"
      >
        <img
          src="/assets/neo3.png"
          alt="NeuroTutor"
          className="h-auto w-[190px] object-contain"
        />
      </button>

      <div className="grid gap-2">
        {visibleItems.map((item) => {
          const Icon = item.icon || GraduationCap;
          const modulesItem = isModulesItem(item.label);

          if (modulesItem) {
            return (
              <button
                key={item.label}
                type="button"
                onClick={(event) => {
                  event.preventDefault();
                  event.stopPropagation();
                  console.log("CLICK MODULOS SIDEBAR REAL");
                  handleModulesClick();
                }}
                onMouseDown={() => console.log("MOUSEDOWN MODULOS")}
                className={cn(
                  "relative z-10 flex h-12 w-full cursor-pointer items-center justify-start gap-3 rounded-nt-button border-0 bg-transparent px-4 text-sm font-extrabold text-nt-text-secondary transition-all outline-none hover:bg-nt-sky hover:text-nt-blue focus-visible:ring-4 focus-visible:ring-nt-blue-light/30",
                  item.active && "bg-nt-blue text-white shadow-lg shadow-nt-blue/25 hover:bg-nt-blue hover:text-white"
                )}
                aria-current={item.active ? "page" : undefined}
              >
                <Icon className="pointer-events-none size-5 shrink-0" aria-hidden="true" />
                <span className="pointer-events-none truncate">
                  {isResolvingModule ? "Cargando módulo..." : item.label}
                </span>
              </button>
            );
          }

          return (
            <Button
              key={item.label}
              type="button"
              variant="ghost"
              className={cn(
                "h-12 justify-start gap-3 rounded-nt-button px-4 text-sm font-extrabold text-nt-text-secondary hover:bg-nt-sky hover:text-nt-blue",
                item.active &&
                  "bg-nt-blue text-white shadow-lg shadow-nt-blue/25 hover:bg-nt-blue hover:text-white"
              )}
              onClick={item.onClick ?? getDefaultAction(item.label)}
              aria-current={item.active ? "page" : undefined}
            >
              <Icon className="size-5" aria-hidden="true" />
              <span className="truncate">{item.label}</span>
            </Button>
          );
        })}
      </div>

      {moduleNavigationMessage && (
        <p className="mt-2 rounded-[14px] bg-amber-50 px-3 py-2 text-xs font-bold text-amber-800" role="status">
          {moduleNavigationMessage}
        </p>
      )}

      <div className="group relative mt-5 min-h-[168px] overflow-hidden rounded-[30px] border border-white/85 bg-gradient-to-br from-[#dbeafe] via-[#c4d7ff] to-[#ddd6fe] p-4 shadow-[inset_0_1px_0_rgba(255,255,255,0.92),0_18px_38px_rgba(37,99,235,0.16)] transition hover:-translate-y-0.5 hover:shadow-[inset_0_1px_0_rgba(255,255,255,0.92),0_22px_44px_rgba(37,99,235,0.2)] lg:mt-auto">
        <div className="pointer-events-none absolute left-4 top-4 text-white/80">
          <Sparkles className="size-4 drop-shadow-sm" aria-hidden="true" />
        </div>
        <div className="pointer-events-none absolute right-5 top-7 text-white/75">
          <Sparkles className="size-3 drop-shadow-sm" aria-hidden="true" />
        </div>
        <div className="pointer-events-none absolute -bottom-10 -right-8 size-36 rounded-full bg-white/35 blur-2xl" />

        <div className="relative z-10 max-w-[10.25rem]">
          <h2 className="text-xl font-black leading-tight text-nt-text-primary">
            NEO IA
          </h2>
          <p className="mt-1 text-xs font-extrabold leading-4 text-nt-blue">
            Tu asistente de matemáticas
          </p>

          <div className="mt-4 max-w-[8rem] rounded-[20px] border border-white/70 bg-white/55 px-3 py-2 shadow-sm backdrop-blur">
            <p className="text-xs font-black leading-4 text-nt-text-primary">
              ¿Tienes dudas?
            </p>
            <p className="text-xs font-extrabold leading-4 text-nt-text-secondary">
              Pregúntale a NEO
            </p>
          </div>
        </div>

        <div className="absolute right-3 top-3 flex size-8 items-center justify-center rounded-full border border-white/80 bg-white/70 text-nt-blue shadow-sm transition group-hover:translate-x-0.5 group-hover:-translate-y-0.5">
          <ArrowUpRight className="size-4" aria-hidden="true" />
        </div>

        <img
          src="/assets/neo_IA.png"
          alt="NEO IA"
          className="pointer-events-none absolute -bottom-4 -right-9 z-10 h-auto w-[128px] object-contain drop-shadow-[0_16px_24px_rgba(37,99,235,0.24)] transition group-hover:scale-[1.03]"
        />
      </div>

      <div className="mt-3 border-t border-nt-border pt-2">{footer ?? defaultFooter}</div>
    </nav>
  );
}

export default AppSidebar;
