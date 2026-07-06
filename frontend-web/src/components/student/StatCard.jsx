import { ArrowUpRight } from "lucide-react";

import { Card, CardContent } from "@/components/ui/card";
import { cn } from "@/lib/utils";

const toneClasses = {
  blue: "bg-nt-blue/10 text-nt-blue",
  purple: "bg-nt-purple/10 text-nt-purple",
  green: "bg-nt-green/15 text-green-700",
  yellow: "bg-nt-yellow/25 text-amber-700",
  orange: "bg-nt-orange/15 text-orange-700",
};

function StatCard({
  label,
  value,
  helper,
  icon: Icon = ArrowUpRight,
  tone = "blue",
  className,
}) {
  return (
    <Card
      className={cn(
        "rounded-nt-card border-white/80 bg-white/95 py-0 shadow-nt-card",
        className
      )}
    >
      <CardContent className="flex items-center gap-4 p-5">
        <div
          className={cn(
            "grid size-12 shrink-0 place-items-center rounded-[20px]",
            toneClasses[tone] || toneClasses.blue
          )}
        >
          <Icon className="size-6" aria-hidden="true" />
        </div>
        <div className="min-w-0">
          <p className="truncate text-sm font-extrabold text-nt-text-secondary">
            {label}
          </p>
          <strong className="block truncate text-2xl font-black leading-tight text-nt-text-primary">
            {value}
          </strong>
          {helper && (
            <p className="mt-1 truncate text-xs font-semibold text-nt-text-secondary">
              {helper}
            </p>
          )}
        </div>
      </CardContent>
    </Card>
  );
}

export default StatCard;
