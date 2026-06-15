import { BookOpen, GraduationCap, Home, Trophy, UserRound } from "lucide-react";
import { useNavigate } from "react-router-dom";

import { Button } from "@/components/ui/button";
import { cn } from "@/lib/utils";

const defaultItems = [
  { label: "Inicio", icon: Home, active: true },
  { label: "Modulos", icon: BookOpen },
  { label: "Mis logros", icon: Trophy },
  { label: "Perfil", icon: UserRound },
];

function AppSidebar({
  items = defaultItems,
  title = "NeuroTutor",
  subtitle = "Panel estudiante",
  footer,
  className,
}) {
  const navigate = useNavigate();

  return (
    <nav
      className={cn(
        "h-fit w-full rounded-nt-card border border-white/80 bg-white/95 p-4 text-nt-text-primary shadow-nt-soft backdrop-blur",
        className
      )}
      aria-label="Navegacion principal"
    >
      <button
        type="button"
        className="mb-7 flex w-full cursor-pointer items-center justify-center rounded-[24px] p-1 transition hover:scale-[1.03] hover:opacity-90 focus:outline-none focus:ring-4 focus:ring-nt-blue-light/30"
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
        {items.map((item) => {
          const Icon = item.icon || GraduationCap;

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
              onClick={item.onClick}
              aria-current={item.active ? "page" : undefined}
            >
              <Icon className="size-5" aria-hidden="true" />
              <span className="truncate">{item.label}</span>
            </Button>
          );
        })}
      </div>

      <div className="mt-7 rounded-[30px] border border-white/80 bg-gradient-to-b from-nt-sky via-white to-white p-4 text-center shadow-[inset_0_1px_0_rgba(255,255,255,0.9),0_16px_35px_rgba(37,99,235,0.12)]">
        <img
          src="/assets/neo2.png"
          alt="NEO"
          className="mx-auto h-36 w-auto object-contain drop-shadow-[0_16px_26px_rgba(37,99,235,0.22)]"
        />
        <h2 className="mt-1 text-lg font-black leading-tight text-nt-text-primary">
          Hola! Soy NEO
        </h2>
        <p className="mx-auto mt-2 max-w-[13rem] text-xs font-semibold leading-5 text-nt-text-secondary">
          Estoy aqui para acompanarte en tu aprendizaje
        </p>
        <Button
          type="button"
          className="mt-4 h-10 w-full rounded-nt-button bg-nt-blue text-sm font-black text-white shadow-lg shadow-nt-blue/20 hover:bg-blue-700"
        >
          Chatear conmigo
        </Button>
      </div>

      {footer && <div className="mt-4 border-t border-nt-border pt-3">{footer}</div>}
    </nav>
  );
}

export default AppSidebar;
