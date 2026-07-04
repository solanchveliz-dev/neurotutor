function NeoFloatingButton({ onClick }) {
  return (
    <button
      type="button"
      onClick={onClick}
      aria-label="Abrir asistente NEO"
      aria-expanded="false"
      className="group fixed bottom-6 right-6 z-[80] flex items-center overflow-visible bg-transparent transition duration-300 ease-out focus:outline-none focus-visible:ring-4 focus-visible:ring-[#60A5FA]/35"
    >
      <span className="relative z-10 flex h-16 w-[150px] items-center rounded-full border-[1.5px] border-violet-500/25 bg-white/95 py-2 pl-5 pr-7 text-left leading-tight shadow-[0_14px_34px_rgba(30,58,138,0.18)] backdrop-blur-md transition duration-300 group-hover:-translate-y-0.5 group-hover:shadow-[0_18px_40px_rgba(30,58,138,0.23)]">
        <span
          className="absolute right-1 top-1/2 size-4 -translate-y-1/2 rotate-45 border-r-[1.5px] border-t-[1.5px] border-violet-500/20 bg-white"
          aria-hidden="true"
        />
        <span className="relative z-10">
          <span className="block bg-gradient-to-r from-[#2563EB] via-[#7C3AED] to-[#9333EA] bg-clip-text text-lg font-black tracking-normal text-transparent">
            NEO IA
          </span>
          <span className="mt-0.5 block text-xs font-semibold tracking-normal text-slate-500">
            Pregúntame
          </span>
        </span>
      </span>

      <span className="relative z-20 -ml-3 grid size-[72px] shrink-0 place-items-center rounded-full border border-white/60 bg-[linear-gradient(135deg,#2563EB_0%,#7C3AED_52%,#9333EA_100%)] shadow-[inset_0_2px_10px_rgba(255,255,255,0.3),0_0_24px_rgba(124,58,237,0.72),0_12px_28px_rgba(76,29,149,0.34)] transition duration-250 group-hover:-translate-y-1 group-hover:scale-105 group-hover:rotate-2 group-hover:shadow-[inset_0_2px_12px_rgba(255,255,255,0.38),0_0_34px_rgba(96,165,250,0.9),0_16px_32px_rgba(76,29,149,0.42)]">
        <span
          className="absolute inset-1 rounded-full border border-white/20 bg-[radial-gradient(circle_at_35%_25%,rgba(255,255,255,0.34),transparent_46%)]"
          aria-hidden="true"
        />
        <img
          src="/assets/neo_chat.png"
          alt=""
          className="relative z-10 size-[62px] object-contain drop-shadow-[0_7px_12px_rgba(15,23,42,0.35)]"
        />
        <span
          className="absolute bottom-0.5 right-0.5 z-20 size-3.5 rounded-full border-2 border-white bg-[#22C55E] shadow-[0_0_10px_rgba(34,197,94,0.95)]"
          aria-hidden="true"
        />
      </span>
    </button>
  );
}

export default NeoFloatingButton;
