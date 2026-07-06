import { BookOpen, Lock, Play } from "lucide-react";

import { Badge } from "@/components/ui/badge";
import { Card, CardContent } from "@/components/ui/card";
import { Progress } from "@/components/ui/progress";
import { cn } from "@/lib/utils";
import PrimaryButton from "./PrimaryButton";

function ModuleCard({
  title,
  description,
  progress = 0,
  total,
  status,
  active = false,
  locked = false,
  icon: Icon = BookOpen,
  actionLabel = "Continuar",
  onAction,
  className,
}) {
  const safeProgress = Math.min(100, Math.max(0, Number(progress) || 0));

  return (
    <Card
      className={cn(
        "rounded-nt-card border-white/80 bg-white/95 py-0 shadow-nt-card transition hover:-translate-y-1 hover:shadow-nt-soft",
        active && "border-nt-blue bg-blue-50/80",
        locked && "opacity-70 hover:translate-y-0",
        className
      )}
    >
      <CardContent className="p-5">
        <div className="mb-5 flex items-start justify-between gap-4">
          <div className="flex min-w-0 items-start gap-3">
            <div
              className={cn(
                "grid size-14 shrink-0 place-items-center rounded-[22px] bg-nt-purple/10 text-nt-purple",
                active && "bg-nt-blue text-white",
                locked && "bg-slate-100 text-slate-400"
              )}
            >
              {locked ? (
                <Lock className="size-7" aria-hidden="true" />
              ) : (
                <Icon className="size-7" aria-hidden="true" />
              )}
            </div>
            <div className="min-w-0">
              <h3 className="line-clamp-2 text-lg font-black leading-tight text-nt-text-primary">
                {title}
              </h3>
              {description && (
                <p className="mt-2 line-clamp-2 text-sm font-semibold leading-6 text-nt-text-secondary">
                  {description}
                </p>
              )}
            </div>
          </div>
          {status && (
            <Badge className="shrink-0 bg-nt-yellow/30 text-amber-700">
              {status}
            </Badge>
          )}
        </div>

        <div className="mb-3 flex justify-between gap-3 text-sm font-extrabold text-nt-text-primary">
          <span>{total || "Avance"}</span>
          <span>{safeProgress}%</span>
        </div>
        <Progress
          value={safeProgress}
          className="mb-5 h-3 bg-nt-border [&_[data-slot=progress-indicator]]:bg-nt-green"
        />

        <PrimaryButton
          type="button"
          className="w-full"
          tone={locked ? "blue" : active ? "green" : "purple"}
          disabled={locked}
          onClick={onAction}
        >
          {locked ? "Bloqueado" : actionLabel}
          {!locked && <Play className="size-4 fill-current" aria-hidden="true" />}
        </PrimaryButton>
      </CardContent>
    </Card>
  );
}

export default ModuleCard;
