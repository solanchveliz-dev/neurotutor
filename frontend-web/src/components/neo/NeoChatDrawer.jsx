import { useEffect, useMemo, useRef, useState } from "react";
import { Bot, Lightbulb, Send, Sparkles, X } from "lucide-react";
import { matchPath, useLocation } from "react-router-dom";

import { Button } from "@/components/ui/button";
import { askNeoTutor } from "@/services/aiService";
import { getStudentId } from "@/utils/auth";
import NeoFloatingButton from "./NeoFloatingButton";

const HISTORY_KEY = "neotutor_neo_session_messages";
const dashboardActions = [["PROGRESS", "¿Cómo voy?"], ["NEXT_STEP", "¿Qué debo hacer ahora?"]];
const theoryActions = [["EXPLAIN", "Explícame este tema"], ["EXAMPLE", "Dame un ejemplo"], ["SUMMARY", "Resúmelo fácil"]];
const practiceActions = [["HINT", "Dame una pista"], ["PROCEDURE", "Explícame el procedimiento"], ["SIMILAR_EXERCISE", "Ejercicio parecido"]];

const screens = [
  ["/module/:moduleId/level/:levelId/theory/lesson/:lessonId", "THEORY", theoryActions],
  ["/module/:moduleId/level/:levelId/theory/:lessonId", "THEORY", theoryActions],
  ["/module/:moduleId/level/:levelId/theory", "THEORY", theoryActions],
  ["/module/:moduleId/level/:levelId/practice", "PRACTICE", practiceActions],
  ["/practice/:moduleId", "PRACTICE", practiceActions],
  ["/module/:moduleId/level/:levelId", "LEVEL_ACTIVITIES", [["EXPLAIN_LEVEL", "Explícame este nivel"], ["NEXT_STEP", "¿Qué debo estudiar primero?"]]],
  ["/module/:moduleId", "MODULE_DETAIL", [["EXPLAIN_MODULE", "Explícame este módulo"], ["NEXT_STEP", "¿Por dónde empiezo?"]]],
  ["/learning-path", "LEARNING_PATH", dashboardActions],
  ["/student-dashboard", "DASHBOARD", dashboardActions],
];

const readHistory = () => {
  try {
    const history = JSON.parse(sessionStorage.getItem(HISTORY_KEY));
    return Array.isArray(history) ? history : [];
  } catch {
    return [];
  }
};

function NeoChatDrawer() {
  const location = useLocation();
  const context = useMemo(() => {
    for (const [pattern, currentScreen, actions] of screens) {
      const match = matchPath({ path: pattern, end: true }, location.pathname);
      if (match) return { currentScreen, actions, params: match.params };
    }
    return null;
  }, [location.pathname]);
  const [isOpen, setIsOpen] = useState(false);
  const [messages, setMessages] = useState(readHistory);
  const [input, setInput] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState("");
  const messagesEndRef = useRef(null);

  useEffect(() => {
    sessionStorage.setItem(HISTORY_KEY, JSON.stringify(messages.slice(-30)));
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages, isLoading]);

  useEffect(() => setIsOpen(false), [location.pathname]);

  if (!context || location.pathname.startsWith("/final-exam/")) return null;

  const sendMessage = async (text, action = "QUESTION") => {
    const cleanMessage = text.trim();
    if (!cleanMessage || isLoading) return;

    setMessages((current) => [...current, { id: `${Date.now()}-user`, role: "user", text: cleanMessage }]);
    setInput("");
    setError("");
    setIsLoading(true);

    try {
      const { moduleId, levelId, lessonId } = context.params;
      const studentId = Number(getStudentId());
      const response = await askNeoTutor({
        studentId: Number.isFinite(studentId) && studentId > 0 ? studentId : null,
        moduleId: moduleId ? Number(moduleId) : null,
        levelId: levelId ? Number(levelId) : null,
        lessonId: lessonId && /^\d+$/.test(lessonId) ? Number(lessonId) : null,
        currentScreen: context.currentScreen,
        action,
        message: cleanMessage,
        context: `Ruta actual: ${location.pathname}`,
      });
      setMessages((current) => [...current, { id: `${Date.now()}-neo`, role: "assistant", text: response.answer }]);
    } catch {
      setError("NEO no pudo responder ahora. Intenta otra vez en unos segundos.");
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <>
      <NeoFloatingButton onClick={() => setIsOpen((value) => !value)} isOpen={isOpen} />
      <div className={`fixed inset-0 z-[60] bg-[#1E3A8A]/25 backdrop-blur-[2px] transition-opacity duration-300 ${isOpen ? "opacity-100" : "pointer-events-none opacity-0"}`} onClick={() => setIsOpen(false)} />
      <section
        aria-label="Asistente NEO IA"
        aria-hidden={!isOpen}
        className={`fixed inset-x-0 bottom-0 z-[70] flex max-h-[88vh] flex-col overflow-hidden rounded-t-[28px] border border-white/80 bg-white shadow-[0_-24px_70px_rgba(30,58,138,0.25)] transition-transform duration-300 sm:inset-y-0 sm:left-auto sm:right-0 sm:max-h-none sm:w-[430px] sm:rounded-none sm:rounded-l-[28px] ${isOpen ? "translate-y-0 sm:translate-x-0" : "translate-y-full sm:translate-x-full sm:translate-y-0"}`}
      >
        <header className="bg-gradient-to-br from-[#DFF4FF] via-white to-[#EDE9FE] px-5 pb-5 pt-4">
          <div className="flex items-center justify-between gap-4">
            <div className="flex items-center gap-3">
              <div className="grid size-12 place-items-center overflow-hidden rounded-full bg-white shadow-md"><img src="/assets/neo_IA.png" alt="NEO" className="h-14 w-14 object-contain object-bottom" /></div>
              <div><h2 className="font-black text-[#1E3A8A]">NEO IA</h2><p className="text-xs font-semibold text-[#64748B]">Tu asistente de matemáticas</p></div>
            </div>
            <button type="button" onClick={() => setIsOpen(false)} className="grid size-10 place-items-center rounded-full bg-white/85 text-[#64748B] shadow-sm hover:text-[#2563EB]" aria-label="Cerrar NEO"><X className="size-5" /></button>
          </div>
          <p className="mt-4 rounded-2xl border border-white bg-white/70 px-4 py-3 text-sm font-semibold leading-6 text-[#1E3A8A]">¡Hola! Estoy aquí para ayudarte a entender, practicar y avanzar paso a paso.</p>
        </header>

        <div className="border-y border-[#E5E7EB] bg-white px-4 py-3">
          <div className="flex gap-2 overflow-x-auto pb-1">
            {context.actions.map(([action, label]) => <button key={action} type="button" onClick={() => sendMessage(label, action)} disabled={isLoading} className="shrink-0 rounded-full border border-[#BFDBFE] bg-[#EFF6FF] px-3 py-2 text-xs font-bold text-[#2563EB] transition hover:border-[#A78BFA] hover:bg-[#F5F3FF] disabled:opacity-50">{label}</button>)}
          </div>
        </div>

        <div className="min-h-0 flex-1 space-y-4 overflow-y-auto bg-[#F8FBFF] p-4">
          {messages.length === 0 && <div className="mx-auto mt-8 max-w-xs text-center"><Sparkles className="mx-auto size-7 text-[#7C3AED]" /><p className="mt-3 text-sm font-semibold text-[#52617C]">Elige una opción rápida o escribe tu pregunta.</p></div>}
          {messages.map((item) => <div key={item.id} className={`flex ${item.role === "user" ? "justify-end" : "justify-start"}`}><div className={`max-w-[86%] whitespace-pre-line rounded-[20px] px-4 py-3 text-sm font-semibold leading-6 ${item.role === "user" ? "rounded-br-md bg-[#2563EB] text-white" : "rounded-bl-md border border-[#E5E7EB] bg-white text-[#1E3A8A] shadow-sm"}`}>{item.text}</div></div>)}
          {isLoading && <div className="flex items-center gap-2 text-sm font-semibold text-[#64748B]"><Bot className="size-4 text-[#7C3AED]" /> NEO está pensando...</div>}
          {error && <div className="rounded-2xl border border-red-200 bg-red-50 px-4 py-3 text-sm font-semibold text-red-700">{error}</div>}
          <div ref={messagesEndRef} />
        </div>

        <form className="border-t border-[#E5E7EB] bg-white p-4" onSubmit={(event) => { event.preventDefault(); sendMessage(input); }}>
          <div className="flex items-end gap-2 rounded-[22px] border border-[#BFDBFE] bg-[#F8FBFF] p-2 focus-within:border-[#7C3AED] focus-within:ring-4 focus-within:ring-[#A78BFA]/15">
            <Lightbulb className="mb-2.5 ml-2 size-4 shrink-0 text-[#7C3AED]" />
            <textarea value={input} onChange={(event) => setInput(event.target.value)} rows={1} maxLength={900} placeholder="Pregúntale a NEO..." className="max-h-28 min-h-10 flex-1 resize-none bg-transparent px-1 py-2 text-sm font-semibold text-[#1E3A8A] outline-none placeholder:text-[#94A3B8]" />
            <Button type="submit" size="icon" disabled={!input.trim() || isLoading} className="size-10 shrink-0 rounded-full bg-gradient-to-br from-[#2563EB] to-[#7C3AED] text-white"><Send className="size-4" /></Button>
          </div>
        </form>
      </section>
    </>
  );
}

export default NeoChatDrawer;
