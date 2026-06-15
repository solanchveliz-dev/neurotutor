import { Bell, Search, Sparkles } from "lucide-react";

import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Button } from "@/components/ui/button";
import { cn } from "@/lib/utils";

function AppTopbar({
  title = "Hola, estudiante",
  subtitle = "Listo para seguir aprendiendo",
  searchPlaceholder = "Buscar modulos o retos",
  studentName = "Estudiante",
  avatarUrl,
  actions,
  onProfileClick,
  className,
}) {
  const initials = studentName
    .split(" ")
    .filter(Boolean)
    .slice(0, 2)
    .map((part) => part[0])
    .join("")
    .toUpperCase();

  return (
    <header
      className={cn(
        "flex w-full flex-col gap-4 rounded-nt-card border border-white/80 bg-white/90 p-4 shadow-nt-card backdrop-blur md:flex-row md:items-center md:justify-between",
        className
      )}
    >
      <div className="min-w-0">
        <div className="mb-1 inline-flex items-center gap-2 rounded-full bg-nt-yellow/25 px-3 py-1 text-xs font-black text-nt-text-primary">
          <Sparkles className="size-3.5 text-nt-orange" aria-hidden="true" />
          NeuroTutor
        </div>
        <h1 className="truncate text-2xl font-black leading-tight text-nt-text-primary md:text-3xl">
          {title}
        </h1>
        <p className="text-sm font-semibold text-nt-text-secondary">{subtitle}</p>
      </div>

      <div className="flex min-w-0 flex-1 flex-col gap-3 md:flex-row md:items-center md:justify-end">
        <label className="relative min-w-0 flex-1 lg:max-w-md">
          <Search
            className="pointer-events-none absolute left-4 top-1/2 size-4 -translate-y-1/2 text-nt-text-secondary"
            aria-hidden="true"
          />
          <span className="sr-only">Buscar</span>
          <input
            type="search"
            placeholder={searchPlaceholder}
            className="h-12 w-full rounded-nt-button border border-nt-border bg-white pl-11 pr-4 text-sm font-semibold text-nt-text-primary outline-none transition placeholder:text-nt-text-secondary focus:border-nt-blue focus:ring-4 focus:ring-nt-blue-light/25"
          />
        </label>

        <div className="flex items-center gap-2">
          {actions}
          <Button
            type="button"
            variant="ghost"
            size="icon-lg"
            className="rounded-nt-button bg-nt-sky text-nt-blue hover:bg-nt-blue hover:text-white"
            aria-label="Notificaciones"
          >
            <Bell className="size-5" />
          </Button>
          <button
            type="button"
            onClick={onProfileClick}
            className="flex items-center gap-2 rounded-nt-button border border-nt-border bg-white p-1.5 pr-3 text-left shadow-sm transition hover:border-nt-blue-light hover:shadow-md"
          >
            <Avatar size="lg">
              <AvatarImage src={avatarUrl} alt={studentName} />
              <AvatarFallback className="bg-nt-purple text-sm font-black text-white">
                {initials || "E"}
              </AvatarFallback>
            </Avatar>
            <span className="hidden max-w-28 truncate text-sm font-extrabold text-nt-text-primary sm:block">
              {studentName}
            </span>
          </button>
        </div>
      </div>
    </header>
  );
}

export default AppTopbar;
