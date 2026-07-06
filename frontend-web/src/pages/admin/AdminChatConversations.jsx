import { useCallback, useEffect, useMemo, useState } from "react";
import {
  BarChart3,
  Bot,
  CalendarDays,
  Download,
  Eye,
  MessageCircle,
  RefreshCw,
  Search,
  Trash2,
  UserRound,
  Users,
  X,
} from "lucide-react";

import AdminLayout from "@/components/admin/AdminLayout";
import AdminStatusBadge from "@/components/admin/AdminStatusBadge";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import {
  deleteAdminChatConversation,
  getAdminChatConversations,
  getAdminChatStatistics,
  getAdminChatStudent,
} from "@/services/adminService";

const levelLabels = {
  BASICO: "Básico",
  INTERMEDIO: "Intermedio",
  AVANZADO: "Avanzado",
};

const formatDateTime = (value) => {
  if (!value) return "Sin registro";
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return value;
  return new Intl.DateTimeFormat("es-PE", {
    dateStyle: "short",
    timeStyle: "short",
  }).format(date);
};

function MetricCard({ label, value, helper, icon: Icon }) {
  return (
    <Card className="border-[#D8E5F8]/80 bg-white/92 shadow-[0_18px_46px_rgba(37,99,255,0.08)]">
      <CardContent className="flex items-start justify-between gap-4 p-5">
        <div>
          <p className="text-sm font-semibold text-[#52617C]">{label}</p>
          <p className="mt-3 text-3xl font-semibold text-[#1E2A4A]">{value}</p>
          <p className="mt-2 text-xs text-[#7C8CAB]">{helper}</p>
        </div>
        <div className="grid size-11 place-items-center rounded-2xl bg-[#F4F8FF] text-[#2563FF] ring-1 ring-[#2563FF]/10">
          <Icon className="size-5" aria-hidden="true" />
        </div>
      </CardContent>
    </Card>
  );
}

function MiniChart({ title, items = [] }) {
  const maxValue = Math.max(1, ...items.map((item) => Number(item.value) || 0));
  return (
    <Card className="border-[#D8E5F8]/80 bg-white/92 shadow-[0_18px_46px_rgba(37,99,255,0.08)]">
      <CardHeader className="border-b border-[#E5EEFC] px-5 py-4">
        <CardTitle className="flex items-center gap-2 text-base font-semibold text-[#1E2A4A]">
          <BarChart3 className="size-4 text-[#7C3AED]" />
          {title}
        </CardTitle>
      </CardHeader>
      <CardContent className="space-y-3 p-5">
        {items.slice(0, 8).map((item) => (
          <div key={item.label}>
            <div className="mb-1.5 flex items-center justify-between gap-3 text-xs">
              <span className="truncate font-medium text-[#52617C]">{item.label}</span>
              <span className="shrink-0 text-[#7C8CAB]">{item.value}</span>
            </div>
            <div className="h-2 overflow-hidden rounded-full bg-[#E5EEFC]">
              <div
                className="h-full rounded-full bg-gradient-to-r from-[#2563FF] to-[#7C3AED]"
                style={{ width: `${Math.max(5, (Number(item.value) / maxValue) * 100)}%` }}
              />
            </div>
          </div>
        ))}
        {!items.length && <p className="py-8 text-center text-sm text-[#7C8CAB]">Sin datos registrados.</p>}
      </CardContent>
    </Card>
  );
}

function AdminChatConversations() {
  const [conversations, setConversations] = useState([]);
  const [statistics, setStatistics] = useState({});
  const [selectedConversation, setSelectedConversation] = useState(null);
  const [selectedMessages, setSelectedMessages] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isLoadingDetail, setIsLoadingDetail] = useState(false);
  const [error, setError] = useState("");
  const [filters, setFilters] = useState({
    query: "",
    level: "",
    fromDate: "",
    toDate: "",
  });

  const loadData = useCallback(async () => {
    setIsLoading(true);
    setError("");
    try {
      const [conversationData, statisticData] = await Promise.all([
        getAdminChatConversations(),
        getAdminChatStatistics(),
      ]);
      setConversations(Array.isArray(conversationData) ? conversationData : []);
      setStatistics(statisticData ?? {});
    } catch (requestError) {
      setError(requestError.message || "No se pudo cargar el historial de conversaciones.");
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => {
    loadData();
  }, [loadData]);

  const filteredConversations = useMemo(() => {
    const normalizedQuery = filters.query.trim().toLowerCase();
    return conversations.filter((conversation) => {
      const searchableText = [
        conversation.student_name,
        conversation.student_email,
        conversation.last_student_question,
      ].join(" ").toLowerCase();
      const conversationDate = conversation.date || conversation.last_message_at?.slice(0, 10) || "";
      return (!normalizedQuery || searchableText.includes(normalizedQuery))
        && (!filters.level || conversation.student_level === filters.level)
        && (!filters.fromDate || conversationDate >= filters.fromDate)
        && (!filters.toDate || conversationDate <= filters.toDate);
    });
  }, [conversations, filters]);

  const openConversation = async (conversation) => {
    setSelectedConversation(conversation);
    setSelectedMessages([]);
    setIsLoadingDetail(true);
    try {
      const studentMessages = await getAdminChatStudent(conversation.student_id);
      setSelectedMessages(
        (Array.isArray(studentMessages) ? studentMessages : [])
          .filter((message) => message.conversation_id === conversation.conversation_id)
      );
    } catch (requestError) {
      setError(requestError.message || "No se pudo cargar la conversación.");
    } finally {
      setIsLoadingDetail(false);
    }
  };

  const deleteConversation = async (conversation) => {
    const confirmed = window.confirm(`¿Eliminar la conversación de ${conversation.student_name}?`);
    if (!confirmed) return;
    try {
      await deleteAdminChatConversation(conversation.conversation_id);
      if (selectedConversation?.conversation_id === conversation.conversation_id) {
        setSelectedConversation(null);
        setSelectedMessages([]);
      }
      await loadData();
    } catch (requestError) {
      setError(requestError.message || "No se pudo eliminar la conversación.");
    }
  };

  const exportCsv = () => {
    const rows = [
      ["Estudiante", "Correo", "Nivel", "Fecha", "Mensajes", "Ultima consulta", "Estado"],
      ...filteredConversations.map((conversation) => [
        conversation.student_name,
        conversation.student_email,
        levelLabels[conversation.student_level] || conversation.student_level,
        conversation.last_message_at,
        conversation.message_count,
        conversation.last_student_question,
        conversation.student_status,
      ]),
    ];
    const csv = rows
      .map((row) => row.map((value) => `"${String(value ?? "").replaceAll('"', '""')}"`).join(","))
      .join("\n");
    const blob = new Blob([`\uFEFF${csv}`], { type: "text/csv;charset=utf-8" });
    const url = URL.createObjectURL(blob);
    const anchor = document.createElement("a");
    anchor.href = url;
    anchor.download = "conversaciones-ia.csv";
    anchor.click();
    URL.revokeObjectURL(url);
  };

  return (
    <AdminLayout
      title="Conversaciones IA"
      description="Historial y métricas de uso del Tutor IA por estudiante."
      actions={
        <>
          <Button onClick={exportCsv} variant="outline" className="rounded-xl border-[#D8E5F8] bg-white text-[#52617C]">
            <Download className="size-4" /> Exportar CSV
          </Button>
          <Button onClick={loadData} variant="outline" className="rounded-xl border-[#D8E5F8] bg-white text-[#52617C]">
            <RefreshCw className="size-4" /> Actualizar
          </Button>
        </>
      }
    >
      {error && <p className="mb-5 rounded-2xl border border-red-200 bg-red-50 px-4 py-3 text-sm font-semibold text-red-700">{error}</p>}

      <section className="grid gap-4 sm:grid-cols-2 xl:grid-cols-5">
        <MetricCard label="Total conversaciones" value={statistics.total_conversations ?? 0} helper="Sesiones registradas" icon={MessageCircle} />
        <MetricCard label="Mensajes" value={statistics.total_messages ?? 0} helper="Estudiante y Tutor IA" icon={Bot} />
        <MetricCard label="Estudiantes con IA" value={statistics.total_students_using_ai ?? 0} helper="Usuarios únicos" icon={Users} />
        <MetricCard label="Consultas hoy" value={statistics.queries_today ?? 0} helper="Mensajes registrados hoy" icon={CalendarDays} />
        <MetricCard label="Promedio" value={statistics.average_messages_per_student ?? 0} helper="Mensajes por estudiante" icon={UserRound} />
      </section>

      <section className="mt-6 grid gap-6 lg:grid-cols-2">
        <MiniChart title="Conversaciones por día" items={statistics.conversations_by_day} />
        <MiniChart title="Conversaciones por nivel" items={statistics.conversations_by_level} />
      </section>

      <Card className="mt-6 border-[#D8E5F8]/80 bg-white/92 shadow-[0_18px_48px_rgba(37,99,255,0.08)]">
        <CardContent className="grid gap-3 p-4 md:grid-cols-[minmax(220px,1fr)_180px_170px_170px]">
          <label className="relative">
            <Search className="pointer-events-none absolute left-3.5 top-1/2 size-4 -translate-y-1/2 text-[#7C8CAB]" />
            <input
              value={filters.query}
              onChange={(event) => setFilters((current) => ({ ...current, query: event.target.value }))}
              placeholder="Nombre, correo o palabra clave"
              className="h-11 w-full rounded-xl border border-[#D8E5F8] bg-[#F8FBFF] pl-10 pr-3 text-sm outline-none focus:border-[#2563FF]"
            />
          </label>
          <select
            value={filters.level}
            onChange={(event) => setFilters((current) => ({ ...current, level: event.target.value }))}
            className="h-11 rounded-xl border border-[#D8E5F8] bg-[#F8FBFF] px-3 text-sm text-[#52617C] outline-none focus:border-[#2563FF]"
          >
            <option value="">Todos los niveles</option>
            <option value="BASICO">Básico</option>
            <option value="INTERMEDIO">Intermedio</option>
            <option value="AVANZADO">Avanzado</option>
          </select>
          <input
            type="date"
            value={filters.fromDate}
            onChange={(event) => setFilters((current) => ({ ...current, fromDate: event.target.value }))}
            className="h-11 rounded-xl border border-[#D8E5F8] bg-[#F8FBFF] px-3 text-sm text-[#52617C] outline-none focus:border-[#2563FF]"
            aria-label="Fecha inicial"
          />
          <input
            type="date"
            value={filters.toDate}
            onChange={(event) => setFilters((current) => ({ ...current, toDate: event.target.value }))}
            className="h-11 rounded-xl border border-[#D8E5F8] bg-[#F8FBFF] px-3 text-sm text-[#52617C] outline-none focus:border-[#2563FF]"
            aria-label="Fecha final"
          />
        </CardContent>
      </Card>

      <Card className="mt-6 overflow-hidden border-[#D8E5F8]/80 bg-white/92 shadow-[0_18px_48px_rgba(37,99,255,0.08)]">
        <div className="overflow-x-auto">
          <table className="min-w-[1050px] w-full text-left">
            <thead className="border-b border-[#E5EEFC] bg-[#F4F8FF]/80 text-xs uppercase text-[#7C8CAB]">
              <tr>
                <th className="px-5 py-3 font-semibold">Estudiante</th>
                <th className="px-5 py-3 font-semibold">Nivel</th>
                <th className="px-5 py-3 font-semibold">Fecha y hora</th>
                <th className="px-5 py-3 font-semibold">Mensajes</th>
                <th className="px-5 py-3 font-semibold">Última consulta</th>
                <th className="px-5 py-3 font-semibold">Estado</th>
                <th className="px-5 py-3 text-right font-semibold">Acciones</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-[#E5EEFC]">
              {filteredConversations.map((conversation) => (
                <tr key={conversation.conversation_id} className="hover:bg-[#F8FBFF]">
                  <td className="px-5 py-4">
                    <p className="font-semibold text-[#1E2A4A]">{conversation.student_name}</p>
                    <p className="mt-1 text-xs text-[#7C8CAB]">{conversation.student_email}</p>
                  </td>
                  <td className="px-5 py-4 text-sm text-[#7C3AED]">{levelLabels[conversation.student_level] || conversation.student_level || "Sin nivel"}</td>
                  <td className="px-5 py-4 text-sm text-[#52617C]">{formatDateTime(conversation.last_message_at)}</td>
                  <td className="px-5 py-4 text-sm font-semibold text-[#52617C]">{conversation.message_count}</td>
                  <td className="max-w-sm px-5 py-4 text-sm text-[#52617C]">
                    <p className="line-clamp-2">{conversation.last_student_question || "Sin consulta"}</p>
                  </td>
                  <td className="px-5 py-4"><AdminStatusBadge status={conversation.student_status} /></td>
                  <td className="px-5 py-4">
                    <div className="flex justify-end gap-2">
                      <Button type="button" size="icon" variant="outline" onClick={() => openConversation(conversation)} aria-label="Ver conversación" title="Ver conversación">
                        <Eye className="size-4" />
                      </Button>
                      <Button type="button" size="icon" variant="outline" onClick={() => deleteConversation(conversation)} aria-label="Eliminar conversación" title="Eliminar conversación" className="text-red-600 hover:bg-red-50 hover:text-red-700">
                        <Trash2 className="size-4" />
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
        {!isLoading && !filteredConversations.length && <p className="px-5 py-12 text-center text-sm text-[#7C8CAB]">No hay conversaciones que coincidan con los filtros.</p>}
        {isLoading && <p className="px-5 py-12 text-center text-sm text-[#7C8CAB]">Cargando conversaciones...</p>}
      </Card>

      {selectedConversation && (
        <>
          <button type="button" className="fixed inset-0 z-40 bg-[#1E2A4A]/25" onClick={() => setSelectedConversation(null)} aria-label="Cerrar detalle" />
          <aside className="fixed inset-y-0 right-0 z-50 flex w-full max-w-xl flex-col border-l border-[#D8E5F8] bg-[#F8FBFF] shadow-2xl">
            <header className="flex items-start justify-between gap-4 border-b border-[#D8E5F8] bg-white p-5">
              <div>
                <p className="text-xs font-semibold uppercase text-[#2563FF]">Conversación IA</p>
                <h2 className="mt-1 text-xl font-semibold text-[#1E2A4A]">{selectedConversation.student_name}</h2>
                <p className="mt-1 text-sm text-[#7C8CAB]">{selectedConversation.student_email}</p>
              </div>
              <Button type="button" size="icon" variant="ghost" onClick={() => setSelectedConversation(null)} aria-label="Cerrar">
                <X className="size-5" />
              </Button>
            </header>
            <div className="min-h-0 flex-1 space-y-4 overflow-y-auto p-5">
              {selectedMessages.map((message) => {
                const isStudent = message.role === "student";
                return (
                  <div key={message.id} className={`flex ${isStudent ? "justify-end" : "justify-start"}`}>
                    <div className={`max-w-[86%] rounded-2xl px-4 py-3 ${isStudent ? "bg-[#2563FF] text-white" : "border border-[#D8E5F8] bg-white text-[#1E2A4A]"}`}>
                      <p className="mb-1 text-xs font-semibold opacity-70">{isStudent ? "Estudiante" : "Tutor IA"} · {formatDateTime(message.timestamp)}</p>
                      <p className="whitespace-pre-line text-sm leading-6">{message.message}</p>
                    </div>
                  </div>
                );
              })}
              {isLoadingDetail && <p className="py-10 text-center text-sm text-[#7C8CAB]">Cargando conversación...</p>}
              {!isLoadingDetail && !selectedMessages.length && <p className="py-10 text-center text-sm text-[#7C8CAB]">No se encontraron mensajes.</p>}
            </div>
          </aside>
        </>
      )}
    </AdminLayout>
  );
}

export default AdminChatConversations;
