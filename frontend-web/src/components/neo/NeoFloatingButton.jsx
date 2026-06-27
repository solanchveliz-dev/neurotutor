import { MessageCircle } from "lucide-react";

function NeoFloatingButton({ onClick, isOpen }) {
  return (
    <button
      type="button"
      onClick={onClick}
      aria-label={isOpen ? "Cerrar asistente NEO" : "Abrir asistente NEO"}
      aria-expanded={isOpen}
      className="fixed bottom-5 right-5 z-50 flex h-16 items-center gap-2 rounded-full border border-white/90 bg-gradient-to-br from-[#2563EB] to-[#7C3AED] px-2.5 pr-5 text-white shadow-[0_20px_50px_rgba(37,99,235,0.38)] transition duration-300 hover:-translate-y-1 focus:outline-none focus:ring-4 focus:ring-[#60A5FA]/35 sm:bottom-7 sm:right-7"
    >
      <span className="grid size-11 shrink-0 place-items-center overflow-hidden rounded-full bg-white/95 shadow-inner">
        <img src="/assets/neo_IA.png" alt="" className="h-14 w-14 object-contain object-bottom" />
      </span>
      <span className="text-left leading-tight">
        <span className="block text-sm font-black">NEO IA</span>
        <span className="flex items-center gap-1 text-[11px] font-semibold text-blue-100"><MessageCircle className="size-3" /> Pregúntame</span>
      </span>
    </button>
  );
}

export default NeoFloatingButton;
