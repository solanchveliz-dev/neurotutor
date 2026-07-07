import { ArrowLeft } from "lucide-react";
import { Button } from "@/components/ui/button";

function BackButton({ children = "Volver", onClick, className = "" }) {
  return (
    <Button
      type="button"
      variant="ghost"
      className={`h-10 rounded-[18px] bg-white/80 px-4 text-sm font-black text-nt-blue shadow-sm hover:bg-white hover:text-nt-purple ${className}`}
      onClick={onClick}
    >
      <ArrowLeft className="size-4" aria-hidden="true" />
      {children}
    </Button>
  );
}

export default BackButton;
