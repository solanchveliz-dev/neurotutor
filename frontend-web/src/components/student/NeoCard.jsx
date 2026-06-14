import { Sparkles } from "lucide-react";

import { Card, CardContent } from "@/components/ui/card";
import { cn } from "@/lib/utils";
import PrimaryButton from "./PrimaryButton";

function NeoCard({
  title = "NEO te acompana",
  message = "Completa un reto corto y gana puntos para desbloquear nuevas actividades.",
  actionLabel = "Empezar reto",
  onAction,
  className,
}) {
  return (
    <Card
      className={cn(
        "overflow-hidden rounded-nt-card border-white/80 bg-[linear-gradient(135deg,#ffffff_0%,#eef6ff_52%,#fff7cc_100%)] py-0 shadow-nt-soft",
        className
      )}
    >
      <CardContent className="relative p-5">
        <div className="absolute right-4 top-4 grid size-9 place-items-center rounded-full bg-nt-yellow text-nt-text-primary shadow-lg shadow-nt-yellow/30">
          <Sparkles className="size-5" aria-hidden="true" />
        </div>

        <div className="mb-5 grid size-24 place-items-center rounded-full bg-white shadow-nt-card">
          <div className="grid size-20 place-items-center rounded-full bg-nt-blue text-3xl font-black text-white">
            NEO
          </div>
        </div>

        <h2 className="max-w-52 text-xl font-black leading-tight text-nt-text-primary">
          {title}
        </h2>
        <p className="mt-3 text-sm font-semibold leading-6 text-nt-text-secondary">
          {message}
        </p>

        {onAction && (
          <PrimaryButton type="button" tone="orange" className="mt-5 w-full" onClick={onAction}>
            {actionLabel}
          </PrimaryButton>
        )}
      </CardContent>
    </Card>
  );
}

export default NeoCard;
