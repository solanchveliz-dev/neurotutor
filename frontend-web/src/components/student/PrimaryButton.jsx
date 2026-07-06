import { Button } from "@/components/ui/button";
import { cn } from "@/lib/utils";

function PrimaryButton({ className, children, tone = "blue", ...props }) {
  const tones = {
    blue: "bg-nt-blue shadow-nt-blue/25 hover:bg-blue-700",
    purple: "bg-nt-purple shadow-nt-purple/25 hover:bg-purple-700",
    green: "bg-nt-green shadow-nt-green/25 hover:bg-green-600",
    orange: "bg-nt-orange shadow-nt-orange/25 hover:bg-orange-600",
  };

  return (
    <Button
      className={cn(
        "h-12 rounded-nt-button px-5 text-sm font-black text-white shadow-lg transition hover:-translate-y-0.5 disabled:hover:translate-y-0",
        tones[tone] || tones.blue,
        className
      )}
      {...props}
    >
      {children}
    </Button>
  );
}

export default PrimaryButton;
