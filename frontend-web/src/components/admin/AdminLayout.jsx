import { useState } from "react";
import { Link, NavLink, useLocation, useNavigate } from "react-router-dom";
import {
  BookOpen,
  ChevronRight,
  LayoutDashboard,
  LogOut,
  Search,
  Settings,
  Users,
} from "lucide-react";

const navigation = [
  { label: "Dashboard", href: "/admin/dashboard", icon: LayoutDashboard },
  { label: "Estudiantes", href: "/admin/students", icon: Users },
];

const upcomingNavigation = [
  { label: "Módulos", icon: BookOpen },
  { label: "Configuración", icon: Settings },
];

const breadcrumbs = {
  "/admin/dashboard": ["Administración", "Dashboard"],
  "/admin/students": ["Administración", "Estudiantes"],
};

function Brand() {
  return (
    <Link to="/admin/dashboard" className="flex items-center gap-3" aria-label="NeuroTutor Admin">
      <div className="grid size-12 place-items-center rounded-2xl bg-gradient-to-br from-[#DFF4FF] to-white p-1.5 shadow-[0_14px_30px_rgba(37,99,255,0.16)] ring-1 ring-[#2563FF]/10">
        <img src="/assets/neo3.png" alt="" className="size-full object-contain" />
      </div>
      <div>
        <p className="text-[12px] font-extrabold uppercase tracking-[0.18em] text-[#1E2A4A]">NEUROTUTOR</p>
        <p className="text-sm font-semibold text-[#2563FF]">Administración</p>
      </div>
    </Link>
  );
}

function Sidebar() {
  return (
    <aside className="hidden min-h-screen w-68 shrink-0 border-r border-[#D8E5F8] bg-white/95 shadow-[18px_0_50px_rgba(37,99,255,0.05)] backdrop-blur-xl lg:flex lg:flex-col">
      <div className="px-6 py-7">
        <Brand />
      </div>

      <nav className="flex-1 space-y-1 px-3" aria-label="Navegación administrativa">
        <p className="px-3 pb-2 pt-3 text-[11px] font-semibold uppercase tracking-[0.16em] text-[#7C8CAB]">General</p>
        {navigation.map(({ label, href, icon: Icon }) => (
          <NavLink
            key={href}
            to={href}
            className={({ isActive }) =>
              `group flex h-12 items-center gap-3 rounded-2xl px-3.5 text-sm font-semibold transition-all duration-200 ${
                isActive
                  ? "bg-gradient-to-r from-[#2563FF] to-[#7C3AED] text-white shadow-[0_14px_32px_rgba(37,99,255,0.24)]"
                  : "text-[#52617C] hover:bg-[#F4F8FF] hover:text-[#1E2A4A]"
              }`
            }
          >
            <Icon className="size-[18px]" strokeWidth={1.8} aria-hidden="true" />
            <span className="flex-1">{label}</span>
            <ChevronRight className="size-4 opacity-0 transition-opacity group-hover:opacity-70" aria-hidden="true" />
          </NavLink>
        ))}

        <p className="px-3 pb-2 pt-7 text-[11px] font-semibold uppercase tracking-[0.16em] text-[#7C8CAB]">Gestión</p>
        {upcomingNavigation.map(({ label, icon: Icon }) => (
          <div
            key={label}
            className="flex h-12 cursor-not-allowed items-center gap-3 rounded-2xl px-3.5 text-sm font-semibold text-[#95A3BA]"
            title="Próximamente"
          >
            <Icon className="size-[18px]" strokeWidth={1.8} aria-hidden="true" />
            <span className="flex-1">{label}</span>
            <span className="rounded-lg bg-[#F4F8FF] px-1.5 py-0.5 text-[9px] font-semibold uppercase tracking-wide text-[#7C8CAB]">
              Pronto
            </span>
          </div>
        ))}
      </nav>

      <div className="border-t border-[#E5EEFC] p-3">
        <Link
          to="/login"
          className="flex h-12 items-center gap-3 rounded-2xl px-3.5 text-sm font-semibold text-[#52617C] transition-colors hover:bg-[#F4F8FF] hover:text-[#1E2A4A]"
        >
          <LogOut className="size-[18px]" strokeWidth={1.8} aria-hidden="true" />
          Cerrar sesión
        </Link>
      </div>
    </aside>
  );
}

function Topbar() {
  const [query, setQuery] = useState("");
  const navigate = useNavigate();
  const location = useLocation();
  const currentBreadcrumb = location.pathname.startsWith("/admin/students/")
    ? ["Administración", "Estudiantes", "Detalle"]
    : breadcrumbs[location.pathname] || ["Administración"];

  const handleSearch = (event) => {
    event.preventDefault();
    const normalizedQuery = query.trim();
    navigate(normalizedQuery ? `/admin/students?query=${encodeURIComponent(normalizedQuery)}` : "/admin/students");
  };

  return (
    <header className="sticky top-0 z-20 border-b border-[#D8E5F8]/80 bg-white/82 px-4 shadow-[0_12px_30px_rgba(37,99,255,0.04)] backdrop-blur-xl sm:px-6 lg:px-8">
      <div className="mx-auto grid min-h-18 max-w-[1500px] grid-cols-[1fr_auto] items-center gap-4 py-3 lg:grid-cols-[1fr_minmax(300px,520px)_1fr]">
        <div className="lg:hidden">
          <Brand />
        </div>

        <div className="hidden lg:block">
          <p className="text-xs font-semibold uppercase tracking-[0.16em] text-[#7C8CAB]">Panel de Administración</p>
          <div className="mt-1 flex items-center gap-2 text-sm">
            {currentBreadcrumb.map((item, index) => (
              <span key={`${item}-${index}`} className={index === currentBreadcrumb.length - 1 ? "font-semibold text-[#1E2A4A]" : "text-[#7C8CAB]"}>
                {item}
                {index < currentBreadcrumb.length - 1 && <span className="ml-2 text-[#B0BED3]">/</span>}
              </span>
            ))}
          </div>
        </div>

        <form onSubmit={handleSearch} className="relative hidden sm:block">
          <Search className="pointer-events-none absolute left-4 top-1/2 size-4 -translate-y-1/2 text-[#7C8CAB]" aria-hidden="true" />
          <input
            value={query}
            onChange={(event) => setQuery(event.target.value)}
            placeholder="Buscar estudiante"
            aria-label="Buscar estudiante"
            className="h-11 w-full rounded-2xl border border-[#D8E5F8] bg-[#F4F8FF]/80 pl-11 pr-4 text-sm text-[#1E2A4A] outline-none transition focus:border-[#2563FF]/40 focus:bg-white focus:ring-4 focus:ring-[#2563FF]/10"
          />
        </form>

        <div className="ml-auto flex items-center gap-3">
          <form onSubmit={handleSearch} className="relative sm:hidden">
            <Search className="pointer-events-none absolute left-3.5 top-1/2 size-4 -translate-y-1/2 text-[#7C8CAB]" aria-hidden="true" />
            <input
              value={query}
              onChange={(event) => setQuery(event.target.value)}
              placeholder="Buscar"
              aria-label="Buscar estudiante"
              className="h-10 w-36 rounded-xl border border-[#D8E5F8] bg-[#F4F8FF]/80 pl-10 pr-3 text-sm text-[#1E2A4A] outline-none transition focus:border-[#2563FF]/40 focus:bg-white focus:ring-4 focus:ring-[#2563FF]/10"
            />
          </form>
          <div className="hidden h-7 w-px bg-[#D8E5F8] sm:block" />
          <div className="flex items-center gap-2.5">
            <div className="relative grid size-10 place-items-center rounded-full bg-gradient-to-br from-[#2563FF] to-[#7C3AED] text-xs font-semibold text-white shadow-[0_10px_24px_rgba(37,99,255,0.22)]">
              AD
              <span className="absolute -bottom-0.5 -right-0.5 size-3 rounded-full border-2 border-white bg-[#2563FF]" />
            </div>
            <div className="hidden sm:block">
              <p className="text-sm font-semibold leading-4 text-[#1E2A4A]">Administrador</p>
              <p className="mt-1 text-xs text-[#7C8CAB]">En línea · Gestión académica</p>
            </div>
          </div>
        </div>
      </div>

      <nav className="flex gap-1 overflow-x-auto pb-3 lg:hidden" aria-label="Navegación administrativa móvil">
        {navigation.map(({ label, href, icon: Icon }) => (
          <NavLink
            key={href}
            to={href}
            className={({ isActive }) =>
              `flex shrink-0 items-center gap-2 rounded-xl px-3 py-2 text-xs font-semibold ${
                isActive ? "bg-gradient-to-r from-[#2563FF] to-[#7C3AED] text-white" : "text-[#52617C]"
              }`
            }
          >
            <Icon className="size-4" aria-hidden="true" />
            {label}
          </NavLink>
        ))}
      </nav>
    </header>
  );
}

function AdminLayout({ eyebrow = "Administración", title, description, actions, children }) {
  return (
    <div className="relative min-h-screen overflow-hidden bg-[#F4F8FF] text-[#1E2A4A]">
      <div className="pointer-events-none absolute inset-0 bg-[linear-gradient(rgba(37,99,255,0.055)_1px,transparent_1px),linear-gradient(90deg,rgba(37,99,255,0.055)_1px,transparent_1px)] bg-[size:44px_44px]" />
      <div className="pointer-events-none absolute -top-36 left-[28%] size-[420px] rounded-full bg-[#DFF4FF]/55 blur-3xl" />
      <div className="pointer-events-none absolute right-[-140px] top-36 size-[360px] rounded-full bg-[#7C3AED]/10 blur-3xl" />
      <div className="relative flex min-h-screen">
        <Sidebar />
        <div className="min-w-0 flex-1">
          <Topbar />
          <main className="px-4 py-7 sm:px-6 lg:px-8 lg:py-9">
            <div className="mx-auto max-w-[1500px]">
              <div className="mb-7 flex flex-col justify-between gap-4 sm:flex-row sm:items-end">
                <div>
                  <p className="mb-2 text-xs font-bold uppercase tracking-[0.16em] text-[#2563FF]">{eyebrow}</p>
                  <h1 className="text-2xl font-semibold tracking-[-0.03em] text-[#1E2A4A] sm:text-3xl">{title}</h1>
                  {description && <p className="mt-2 max-w-2xl text-sm leading-6 text-[#52617C]">{description}</p>}
                </div>
                {actions && <div className="flex shrink-0 items-center gap-2">{actions}</div>}
              </div>
              {children}
            </div>
          </main>
        </div>
      </div>
    </div>
  );
}

export default AdminLayout;
