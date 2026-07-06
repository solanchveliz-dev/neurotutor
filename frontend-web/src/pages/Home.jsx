import { Link } from "react-router-dom";
import { ArrowRight, Award, BookOpen, ClipboardCheck, GraduationCap, LogIn, Medal, Pencil, Sparkles, Trophy, Users } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";

const HOME_CONTENT = {
  features: [
    { title: "Aprende jugando", text: "Actividades interactivas y divertidas.", image: "/assets/practica.png", tone: "from-blue-50 to-sky-50" },
    { title: "Con ayuda de NEO", text: "Tu compañero que te guía en cada paso.", image: "/assets/neo_chat.png", tone: "from-violet-50 to-blue-50" },
    { title: "Desbloquea logros", text: "Gana insignias y celebra tus avances.", image: "/assets/copa.png", tone: "from-amber-50 to-orange-50" },
  ],
  learningPath: [
    { title: "Nivel Básico", text: "Aprende los conceptos fundamentales de las fracciones.", image: "/assets/logro_basico.png", tone: "border-emerald-100 bg-emerald-50/80", titleTone: "text-green-700" },
    { title: "Nivel Intermedio", text: "Refuerza tus habilidades y aprende nuevas operaciones.", image: "/assets/logro_intermedio.png", tone: "border-violet-100 bg-violet-50/80", titleTone: "text-violet-700" },
    { title: "Nivel Avanzado", text: "Domina conceptos complejos y resuelve problemas reales.", image: "/assets/logro_avanzado.png", tone: "border-red-100 bg-red-50/80", titleTone: "text-red-700" },
  ],
  howItWorks: [
    { title: "Diagnóstico", text: "Descubrimos tu punto de partida.", icon: ClipboardCheck, tone: "bg-emerald-100 text-emerald-700" },
    { title: "Teoría", text: "Aprenderás con explicaciones claras.", icon: BookOpen, tone: "bg-blue-100 text-blue-700" },
    { title: "Práctica", text: "Resolverás ejercicios y reforzarás.", icon: Pencil, tone: "bg-amber-100 text-amber-700" },
    { title: "Examen final", text: "Demostrarás lo aprendido.", icon: GraduationCap, tone: "bg-violet-100 text-violet-700" },
    { title: "Logros", text: "Obtendrás insignias y seguirás avanzando.", icon: Medal, tone: "bg-rose-100 text-rose-700" },
  ],
  learn: [
    { title: "Fracciones", text: "Comprende qué son, sus partes y tipos.", image: "/assets/pizza_animada.png" },
    { title: "Operaciones", text: "Suma, resta, multiplicación y división de fracciones.", image: "/assets/apuntes.png" },
    { title: "Problemas reales", text: "Aplica lo aprendido en situaciones del día a día.", image: "/assets/cerebrito.png" },
  ],
  stats: [
    { label: "Estudiantes registrados", value: "135", icon: Users, tone: "text-green-600" },
    { label: "Lecciones", value: "18", icon: BookOpen, tone: "text-blue-600" },
    { label: "Ejercicios", value: "120", icon: Pencil, tone: "text-orange-500" },
    { label: "Logros desbloqueados", value: "12", icon: Trophy, tone: "text-violet-600" },
  ],
};

function SectionTitle({ children }) {
  return <h2 className="text-2xl font-black leading-tight text-nt-text-primary sm:text-3xl">{children}<span className="mt-2 block h-1 w-12 rounded-full bg-nt-blue" /></h2>;
}

function Home() {
  return (
    <main className="min-h-screen bg-[linear-gradient(180deg,rgba(223,244,255,0.18),rgba(255,255,255,0.14)),url('/assets/fondo_home.png')] bg-cover bg-top bg-fixed text-nt-text-primary">
      <header className="sticky top-0 z-50 px-3 py-1 sm:px-8"><div className="mx-auto flex max-w-7xl flex-wrap items-center justify-between gap-1"><Link to="/" aria-label="Inicio de NeuroTutor" className="w-full shrink-0 sm:w-auto"><img src="/assets/neo3.png" alt="NeuroTutor" className="mx-auto h-20 w-48 object-contain sm:h-24 sm:w-64 sm:object-left" /></Link><div className="flex w-full items-center justify-center gap-1 sm:ml-auto sm:w-auto sm:gap-3"><nav><Link to="/" className="hidden px-3 py-2 text-sm font-black text-nt-blue transition-colors duration-300 hover:text-nt-purple sm:block">Inicio</Link></nav><Button asChild variant="ghost" className="font-black text-nt-blue transition-colors duration-300 hover:bg-transparent hover:text-nt-purple"><Link to="/login"><LogIn className="hidden size-4 sm:block" />Iniciar sesión</Link></Button><Button asChild variant="ghost" className="font-black text-nt-purple transition-colors duration-300 hover:bg-transparent hover:text-nt-blue"><Link to="/register">Registrarse</Link></Button></div></div></header>

      <section className="mx-auto grid max-w-7xl items-center gap-5 px-5 py-8 lg:min-h-[68vh] lg:grid-cols-[65%_35%] lg:px-8 lg:py-6"><div><span className="inline-flex items-center gap-2 rounded-full bg-white/80 px-4 py-2 text-sm font-black text-nt-purple shadow-sm"><Sparkles className="size-4 text-amber-400" />Una aventura para aprender</span><h1 className="mt-4 text-4xl font-black leading-[1.05] text-nt-text-primary sm:text-5xl">Aprende matemáticas<br /><span className="bg-gradient-to-r from-nt-blue to-nt-purple bg-clip-text text-transparent">de forma divertida</span></h1><p className="mt-4 max-w-xl text-base font-bold leading-7 text-slate-700 sm:text-lg">Con NeuroTutor aprenderás fracciones mediante una aventura por niveles.</p><div className="mt-6"><Button asChild className="h-12 rounded-[18px] bg-gradient-to-r from-nt-blue to-nt-purple px-7 text-sm font-black text-white shadow-lg shadow-blue-200 transition-all duration-300 hover:-translate-y-1 hover:shadow-xl"><Link to="/register">Comenzar ahora<ArrowRight className="size-5" /></Link></Button></div></div><div className="relative min-h-[300px] lg:min-h-[360px]"><div className="absolute inset-10 rounded-full bg-blue-200/45 blur-3xl" /><img src="/assets/neo_logros.png" alt="NEO celebrando tus logros" className="relative mx-auto h-[320px] w-full object-contain drop-shadow-[0_20px_34px_rgba(37,99,235,0.22)] motion-safe:animate-[bounce_4s_ease-in-out_infinite] lg:h-[380px]" /></div></section>

      <section className="mx-auto max-w-[1500px] px-3 pb-16 sm:px-5"><Card className="overflow-hidden rounded-[34px] border border-white/90 bg-white/92 p-0 shadow-[0_24px_70px_rgba(37,99,235,0.16)] backdrop-blur-xl"><CardContent className="p-6 sm:p-10">
        <div className="grid gap-5 md:grid-cols-3">{HOME_CONTENT.features.map((item) => <article key={item.title} className={`group grid min-h-40 grid-cols-[120px_minmax(0,1fr)] items-center gap-5 rounded-[30px] border border-white/80 bg-gradient-to-br p-6 shadow-[0_12px_32px_rgba(37,99,235,0.11)] transition-all duration-300 hover:-translate-y-1 hover:scale-[1.02] hover:shadow-[0_20px_42px_rgba(37,99,235,0.18)] ${item.tone}`}><img src={item.image} alt="" className="h-28 w-full object-contain transition-transform duration-300 group-hover:scale-105" /><div><h3 className="text-lg font-black text-nt-blue">{item.title}</h3><p className="mt-2 text-sm font-semibold leading-6 text-slate-600">{item.text}</p></div></article>)}</div>

        <div className="mt-12 grid gap-10 xl:grid-cols-[minmax(0,1fr)_35%]"><div className="grid gap-12">
          <section><SectionTitle>Nuestra ruta de aprendizaje</SectionTitle><div className="mt-7 grid gap-5 md:grid-cols-3">{HOME_CONTENT.learningPath.map((item, index) => <article key={item.title} className={`relative flex min-h-72 flex-col items-center justify-center rounded-[32px] border p-7 text-center shadow-[0_14px_34px_rgba(30,58,138,0.11)] transition-all duration-300 hover:-translate-y-1.5 hover:scale-[1.02] hover:shadow-[0_22px_44px_rgba(30,58,138,0.18)] ${item.tone}`}><img src={item.image} alt={`Insignia de ${item.title}`} className="h-40 w-full object-contain drop-shadow-md transition-transform duration-300 hover:scale-105" /><div><h3 className={`mt-4 text-xl font-black ${item.titleTone}`}>{item.title}</h3><p className="mt-2 text-sm font-semibold leading-5 text-slate-600">{item.text}</p></div>{index < 2 && <ArrowRight className="absolute -right-8 top-1/2 z-10 hidden size-7 -translate-y-1/2 text-nt-purple md:block" />}</article>)}</div></section>

          <section><SectionTitle>¿Cómo aprenderás?</SectionTitle><div className="mt-6 grid gap-4 sm:grid-cols-2 lg:grid-cols-5">{HOME_CONTENT.howItWorks.map((item, index) => { const Icon = item.icon; return <article key={item.title} className="relative rounded-[26px] border border-slate-100/80 bg-gradient-to-br from-white to-sky-50/60 p-5 shadow-[0_10px_26px_rgba(37,99,235,0.09)] transition-all duration-300 hover:-translate-y-1 hover:scale-[1.02] hover:shadow-[0_17px_34px_rgba(37,99,235,0.16)]"><span className="absolute right-4 top-4 grid size-7 place-items-center rounded-full bg-nt-purple text-xs font-black text-white">{index + 1}</span><span className={`grid size-14 place-items-center rounded-full ${item.tone}`}><Icon className="size-7" /></span><h3 className="mt-4 text-base font-black">{item.title}</h3><p className="mt-2 text-xs font-semibold leading-5 text-slate-600">{item.text}</p></article>; })}</div></section>

          <section><SectionTitle>¿Qué aprenderás?</SectionTitle><div className="mt-6 grid gap-4 md:grid-cols-3">{HOME_CONTENT.learn.map((item) => <article key={item.title} className="grid min-h-36 grid-cols-[110px_minmax(0,1fr)] items-center gap-4 rounded-[28px] border border-violet-100/80 bg-gradient-to-br from-white to-violet-50/70 p-5 shadow-[0_10px_28px_rgba(124,58,237,0.09)] transition-all duration-300 hover:-translate-y-1 hover:scale-[1.02] hover:shadow-[0_18px_38px_rgba(124,58,237,0.16)]"><img src={item.image} alt="" className="h-28 w-full object-contain transition-transform duration-300 hover:scale-105" /><div><h3 className="text-lg font-black">{item.title}</h3><p className="mt-2 text-sm font-semibold leading-5 text-slate-600">{item.text}</p></div></article>)}</div></section>

          <section><SectionTitle>Estadísticas de nuestra comunidad</SectionTitle><div className="mt-6 grid grid-cols-2 gap-4 lg:grid-cols-4">{HOME_CONTENT.stats.map(({ label, value, icon: Icon, tone }) => <article key={label} className="rounded-[26px] border border-slate-100/80 bg-gradient-to-br from-white to-blue-50/50 p-5 text-center shadow-[0_10px_26px_rgba(37,99,235,0.09)] transition-all duration-300 hover:-translate-y-1 hover:scale-[1.02] hover:shadow-[0_17px_34px_rgba(37,99,235,0.16)]"><Icon className={`mx-auto size-9 ${tone}`} /><p className="mt-3 text-xs font-bold text-slate-500">{label}</p><strong className="text-3xl font-black">{value}</strong></article>)}</div></section>
        </div>

        <aside className="flex self-stretch flex-col justify-center rounded-[30px] border border-blue-100 bg-gradient-to-b from-sky-50 to-blue-100/70 p-7 text-center shadow-[0_14px_34px_rgba(37,99,235,0.1)] transition-all duration-300 hover:-translate-y-1 hover:scale-[1.02] hover:shadow-[0_22px_44px_rgba(37,99,235,0.17)]"><img src="/assets/neo_diagnostic_result.webp" alt="NEO te invita" className="mx-auto h-72 w-full object-contain drop-shadow-md transition-transform duration-300 hover:scale-105 xl:h-80" /><h2 className="mt-5 text-3xl font-black text-nt-purple">NEO te invita</h2><h3 className="mt-4 text-lg font-black">¿Listo para comenzar tu aventura?</h3><p className="mt-4 text-sm font-semibold leading-7 text-slate-600">Cada pequeño paso te acercará a convertirte en un experto en fracciones.</p><Award className="mx-auto mt-6 size-9 text-nt-purple" /></aside></div>

        <section className="mt-10 grid items-center gap-6 overflow-hidden rounded-[30px] border border-blue-100/80 bg-gradient-to-r from-sky-50 via-white to-violet-50 p-7 shadow-[0_12px_30px_rgba(37,99,235,0.1)] transition-all duration-300 hover:-translate-y-1 hover:scale-[1.01] hover:shadow-[0_20px_40px_rgba(37,99,235,0.16)] md:grid-cols-[90px_minmax(0,1fr)_auto]"><img src="/assets/proximo_objetivo.png" alt="" className="h-20 w-full object-contain" /><div><h2 className="text-2xl font-black text-nt-blue">¿Listo para comenzar?</h2><p className="mt-2 text-sm font-semibold text-slate-600">Únete a NeuroTutor y descubre una nueva forma de aprender fracciones.</p></div><div className="flex flex-col gap-2 sm:flex-row"><Button asChild className="rounded-[16px] bg-nt-blue font-black text-white"><Link to="/register">Registrarse</Link></Button><Button asChild variant="outline" className="rounded-[16px] bg-white font-black text-nt-blue"><Link to="/login">Iniciar sesión</Link></Button></div></section>
      </CardContent></Card></section>

      <footer className="bg-gradient-to-r from-[#173b88] to-[#34228f] px-5 py-6 text-white"><div className="mx-auto flex max-w-7xl flex-col items-center justify-between gap-4 text-center sm:flex-row sm:text-left"><div><p className="font-black">NeuroTutor</p><p className="mt-1 text-xs text-blue-100">© 2026 NeuroTutor. Todos los derechos reservados.</p></div><div className="flex gap-5 text-sm font-bold"><a href="#about" className="hover:text-blue-200">Sobre nosotros</a><a href="#privacy" className="hover:text-blue-200">Política de privacidad</a></div></div></footer>
    </main>
  );
}

export default Home;
