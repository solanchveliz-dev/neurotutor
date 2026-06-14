import { BookOpen, GraduationCap, Home, Trophy, UserRound } from "lucide-react";

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
  return (
    <nav
      className={cn(
        "rounded-nt-card border border-white/80 bg-white/95 p-4 text-nt-text-primary shadow-nt-soft backdrop-blur",
        className
      )}
      aria-label="Navegacion principal"
    >
      <div className="mb-7 flex items-center gap-3">
        <div className="grid size-12 place-items-center rounded-[20px] bg-nt-blue text-base font-black text-white shadow-lg shadow-nt-blue/25">
          NT
        </div>
        <div className="min-w-0">
          <p className="truncate text-lg font-black leading-tight">{title}</p>
          <p className="truncate text-xs font-semibold text-nt-text-secondary">
            {subtitle}
          </p>
        </div>
      </div>

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

      {footer && <div className="mt-7">{footer}</div>}
    </nav>
  );
}

export default AppSidebar;
