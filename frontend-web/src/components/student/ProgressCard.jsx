import { Target } from "lucide-react";

import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Progress } from "@/components/ui/progress";
import { cn } from "@/lib/utils";

function ProgressCard({
  title = "Progreso semanal",
  subtitle,
  value = 0,
  totalLabel,
  icon: Icon = Target,
  tone = "blue",
  className,
}) {
  const safeValue = Math.min(100, Math.max(0, Number(value) || 0));
  const fillClasses = {
    blue: "[&_[data-slot=progress-indicator]]:bg-nt-blue",
    purple: "[&_[data-slot=progress-indicator]]:bg-nt-purple",
    green: "[&_[data-slot=progress-indicator]]:bg-nt-green",
    orange: "[&_[data-slot=progress-indicator]]:bg-nt-orange",
  };

  return (
    <Card
      className={cn(
        "rounded-nt-card border-white/80 bg-white/95 py-0 shadow-nt-card",
        className
      )}
    >
      <CardHeader className="flex-row items-start gap-3 p-5 pb-3">
        <div className="grid size-11 place-items-center rounded-[18px] bg-nt-sky text-nt-blue">
          <Icon className="size-5" aria-hidden="true" />
        </div>
        <div className="min-w-0">
          <CardTitle className="truncate text-lg font-black text-nt-text-primary">
            {title}
          </CardTitle>
          {subtitle && (
            <p className="mt-1 text-sm font-semibold text-nt-text-secondary">
              {subtitle}
            </p>
          )}
        </div>
      </CardHeader>
      <CardContent className="p-5 pt-0">
        <div className="mb-3 flex items-end justify-between gap-3">
          <span className="text-3xl font-black text-nt-text-primary">
            {safeValue}%
          </span>
          {totalLabel && (
            <span className="pb-1 text-sm font-extrabold text-nt-text-secondary">
              {totalLabel}
            </span>
          )}
        </div>
        <Progress
          value={safeValue}
          className={cn("h-3 bg-nt-border", fillClasses[tone] || fillClasses.blue)}
        />
      </CardContent>
    </Card>
  );
}

export default ProgressCard;
