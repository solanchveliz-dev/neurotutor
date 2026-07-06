import { useEffect, useMemo, useState } from "react";
import { ArrowRight, BookOpen } from "lucide-react";
import { useNavigate } from "react-router-dom";
import AppSidebar from "../components/layout/AppSidebar";
import StudentLayout from "../components/layout/StudentLayout";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { getStudentDashboard } from "../services/dashboardService";
import { getStudentId } from "../utils/auth";

const levels = [
  { key: "BASICO", label: "Básico", image: "/assets/nivel_basico.png", tone: "from-emerald-500 to-green-600" },
  { key: "INTERMEDIO", label: "Intermedio", image: "/assets/nivel_intermedio.png", tone: "from-violet-500 to-purple-700" },
  { key: "AVANZADO", label: "Avanzado", image: "/assets/nivel_avanzado.png", tone: "from-red-500 to-red-700" },
];

const normalize = (value = "") => value.normalize("NFD").replace(/[\u0300-\u036f]/g, "").toUpperCase();

function Modules() {
  const navigate = useNavigate();
  const [modules, setModules] = useState([]);
  const [error, setError] = useState("");

  useEffect(() => {
    const studentId = getStudentId();
    if (!studentId) return;
    getStudentDashboard(studentId)
      .then((data) => setModules(Array.isArray(data?.modulos) ? data.modulos : []))
      .catch(() => setError("No pudimos cargar tu ruta de aprendizaje."));
  }, []);

  const cards = useMemo(() => levels.map((level) => ({
    ...level,
    module: modules.find((item) => normalize(item?.nivelRequerido ?? item?.nivel ?? item?.titulo ?? item?.nombre).includes(level.key)),
  })), [modules]);

  const sidebarItems = [{ label: "Inicio" }, { label: "Módulos", active: true }, { label: "Mis logros" }, { label: "Perfil" }];

  return <StudentLayout sidebar={<AppSidebar items={sidebarItems} />}><Card className="rounded-[32px] border border-white/90 bg-white/94 p-0 shadow-nt-soft"><CardContent className="p-6 sm:p-8"><div className="flex items-center gap-3"><div className="grid size-12 place-items-center rounded-[18px] bg-nt-blue text-white"><BookOpen /></div><div><h1 className="text-3xl font-black">Módulos</h1><p className="font-semibold text-nt-text-secondary">Elige un nivel para comenzar sus lecciones de teoría.</p></div></div>{error && <p className="mt-5 rounded-[18px] bg-red-50 p-4 font-bold text-red-700">{error}</p>}<div className="mt-7 grid gap-5 md:grid-cols-3">{cards.map(({ key, label, image, tone, module }) => <article key={key} className="rounded-[28px] border border-nt-border bg-white p-5 text-center shadow-sm"><img src={image} alt={`Nivel ${label}`} className="mx-auto h-40 w-full object-contain" /><h2 className="mt-3 text-2xl font-black">Nivel {label}</h2><Button disabled={!module} onClick={() => navigate(`/module/${module.id}/level/${module.id}/theory`, { state: { module, level: { id: module.id, name: label } } })} className={`mt-5 h-12 w-full rounded-[18px] bg-gradient-to-r ${tone} font-black text-white`}>{module ? "Ver teoría" : "No disponible"}<ArrowRight className="size-4" /></Button></article>)}</div></CardContent></Card></StudentLayout>;
}

export default Modules;
