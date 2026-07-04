import { useEffect, useMemo, useState } from "react";
import { useLocation, useNavigate, useParams } from "react-router-dom";
import { AlertTriangle, ArrowLeft, ArrowRight, BookOpen, Check, CheckCircle2, Divide, Eye, Lightbulb, Plus, Puzzle, Star, Target, Trophy } from "lucide-react";
import AppSidebar from "../components/layout/AppSidebar";
import StudentLayout from "../components/layout/StudentLayout";
import BackButton from "../components/student/BackButton";
import { Button } from "@/components/ui/button";
import { getTheoryLesson, getTheoryLessons } from "../services/learningService";
import { getModuleProgress, markTheoryCompleted } from "../services/progressService";
import { getStudentId } from "../utils/auth";

const parseLessonContent = (contentHtml = "", { hideTip = false } = {}) => {
  if (!contentHtml || typeof DOMParser === "undefined") {
    return { contentHtml, learningObjectives: [], tipText: null };
  }

  const document = new DOMParser().parseFromString(contentHtml, "text/html");
  const objectiveList = document.querySelector(".lesson-list");
  const learningObjectives = objectiveList
    ? Array.from(objectiveList.querySelectorAll(":scope > li"))
        .map((item) => item.textContent?.trim())
        .filter(Boolean)
    : [];
  const tipText = document.querySelector(".tip-box")?.textContent?.trim() || null;

  if (objectiveList) {
    const objectiveSection = objectiveList.closest("section");
    const heading = objectiveSection?.querySelector("h2, h3");

    if (objectiveSection && heading) objectiveSection.remove();
    else objectiveList.remove();
  }

  // Visual-only rule: keep backend content unchanged and omit tip boxes only
  // when the current lesson is the introductory welcome lesson.
  if (hideTip) {
    document.querySelectorAll(".tip-box").forEach((tip) => tip.remove());
  }

  return { contentHtml: document.body.innerHTML, learningObjectives, tipText };
};

const validPercentage = (value) => {
  if (value === null || value === undefined || value === "") return null;
  return Math.min(100, Math.max(0, Number(value) || 0));
};

const assetUrl = (asset) => {
  if (!asset) return null;
  return String(asset).startsWith("/") ? asset : `/assets/${asset}`;
};

function StructuredSection({ section }) {
  const visual = assetUrl(section.visual);
  if (section.type === "main_concept" || section.type === "example") {
    const Icon = section.type === "main_concept" ? Target : BookOpen;
    const pizzaImages = { "1/2": "/assets/pizza1.png", "1/4": "/assets/pizza2.png", "3/4": "/assets/pizza3.png" };
    return <section className="grid h-full content-between gap-3 overflow-hidden rounded-[22px] border border-blue-100 bg-gradient-to-br from-white to-sky-50 p-4 shadow-sm"><div><h2 className="flex items-center gap-2 text-lg font-black text-nt-text-primary">{section.type === "main_concept" && <Icon className="size-5 text-nt-blue" />}{section.title}</h2>{section.text && <p className="mt-1.5 text-sm font-semibold leading-5 text-slate-700">{section.text}</p>}</div>{visual && !Array.isArray(section.items) && <img src={visual} alt="" className="mx-auto h-44 w-full object-contain drop-shadow-md xl:h-48" />}{Array.isArray(section.items) && <div className="grid gap-3 sm:grid-cols-3 sm:divide-x sm:divide-violet-100">{section.items.map((item) => { const [numerator, denominator] = item.label.split("/"); return <div key={item.label} className="flex h-full flex-col items-center text-center sm:px-2.5">{pizzaImages[item.label] && <img src={pizzaImages[item.label]} alt={`Pizza que representa ${item.label}`} className="h-24 w-full object-contain drop-shadow-md lg:h-28" />}<strong className="mt-1.5 grid min-w-14 rounded-xl border border-violet-100 bg-white/90 px-3 py-1.5 text-lg font-black leading-none text-nt-purple shadow-sm"><span className="border-b-2 border-current px-1 pb-1">{numerator}</span><span className="px-1 pt-1">{denominator}</span></strong><p className="mt-1.5 text-xs font-semibold leading-4 text-slate-600">{item.description}</p></div>; })}</div>}</section>;
  }
  if (section.type === "important_idea") {
    return <aside className="h-full rounded-[22px] border border-amber-200 bg-amber-50 p-4 shadow-sm"><h2 className="flex items-center gap-2 text-base font-black text-amber-900"><Lightbulb className="size-5 text-amber-500" />{section.title}</h2><p className="mt-2 text-sm font-semibold leading-5 text-amber-900">{section.text}</p></aside>;
  }
  if (section.type === "common_mistakes" && Array.isArray(section.items)) {
    return <section className="h-full rounded-[22px] border border-rose-100 bg-gradient-to-br from-white to-rose-50 p-4 shadow-sm"><h2 className="flex items-center gap-2 text-lg font-black text-red-700"><AlertTriangle className="size-5" />{section.title}</h2><div className="mt-3 grid gap-2">{section.items.map((item) => <div key={item} className="flex gap-3 rounded-2xl border border-red-100 bg-white/80 px-4 py-3 shadow-sm"><span className="font-black text-red-500">×</span><p className="text-sm font-semibold text-slate-700">{item}</p></div>)}</div></section>;
  }
  if (section.type === "reflection") {
    return <section className="rounded-[22px] border border-violet-100 bg-gradient-to-r from-violet-50 via-white to-sky-50 p-4 shadow-sm"><h2 className="text-base font-black text-nt-purple">{section.title}</h2><p className="mt-1.5 text-sm font-semibold leading-6 text-slate-700">{section.text}</p></section>;
  }
  if (section.type === "observe") {
    return <section className="grid h-full items-center gap-4 rounded-[22px] border border-sky-100 bg-sky-50 p-4 sm:grid-cols-[minmax(0,55%)_minmax(160px,45%)]"><div><h2 className="flex items-center gap-2 text-lg font-black text-nt-blue"><Eye className="size-5" aria-hidden="true" />{section.title}</h2><p className="mt-1.5 text-sm font-semibold leading-5 text-slate-700">{section.text}</p></div><img src="/assets/niños.png" alt="Niños observando" className="mx-auto h-48 w-full object-contain drop-shadow-md lg:h-52" /></section>;
  }
  if (section.type === "summary" && Array.isArray(section.items)) {
    return <section className="rounded-[22px] border border-[#b8dca3] bg-[#f2faed] p-4 shadow-sm"><h2 className="text-xl font-black text-[#397a17]">{section.title}</h2><div className="mt-3 grid gap-2 md:grid-cols-3">{section.items.map((item) => <div key={item} className="flex items-start gap-2"><span className="mt-0.5 grid size-5 shrink-0 place-items-center rounded-full bg-[#5bb226] text-white"><Check className="size-3" strokeWidth={3} /></span><p className="text-sm font-semibold text-slate-700">{item}</p></div>)}</div></section>;
  }
  return null;
}

function StackedFraction({ numerator, denominator, className = "text-nt-purple" }) {
  return (
    <strong className={`grid min-w-10 text-center text-xl font-black leading-none ${className}`}>
      <span className="border-b-2 border-current px-1 pb-1">{numerator}</span>
      <span className="px-1 pt-1">{denominator}</span>
    </strong>
  );
}

function EquivalentVisual({ item }) {
  if (item.kind === "pizza") {
    return <div className="flex items-center justify-center gap-2"><img src={assetUrl(item.image)} alt="Pizza dividida en dos partes" className="h-20 w-20 object-contain drop-shadow-md" /><span className="font-black text-slate-500">=</span><img src={assetUrl(item.image)} alt="Pizza dividida en cuatro partes" className="h-20 w-20 object-contain drop-shadow-md" /></div>;
  }
  if (item.kind === "blocks") {
    return <div className="flex items-center justify-center gap-3"><div className="grid h-16 w-20 grid-cols-2 overflow-hidden rounded-sm border-2 border-slate-500 bg-white">{[0, 1].map((cell) => <span key={cell} className={`border-r border-slate-400 last:border-r-0 ${cell === 0 ? "bg-green-500" : ""}`} />)}</div><span className="font-black text-green-700">=</span><div className="grid h-16 w-20 grid-cols-2 grid-rows-3 overflow-hidden rounded-sm border-2 border-slate-500 bg-white">{Array.from({ length: 6 }, (_, cell) => <span key={cell} className={`border-b border-r border-slate-400 ${cell < 3 ? "bg-green-500" : ""}`} />)}</div></div>;
  }
  return <div className="flex items-center justify-center gap-3"><span className="size-20 rounded-full border-2 border-blue-600 shadow-sm" style={{ background: "repeating-conic-gradient(from -90deg, transparent 0 119deg, #2563eb 119deg 120deg), conic-gradient(from -90deg, #4cc9f0 0 240deg, white 240deg 360deg)" }} /><span className="font-black text-blue-700">=</span><span className="size-20 rounded-full border-2 border-blue-600 shadow-sm" style={{ background: "repeating-conic-gradient(from -90deg, transparent 0 59deg, #2563eb 59deg 60deg), conic-gradient(from -90deg, #4cc9f0 0 240deg, white 240deg 360deg)" }} /></div>;
}

function EquivalentFraction({ value }) {
  return <StackedFraction numerator={value?.numerator} denominator={value?.denominator} className="text-current" />;
}

function EquivalentFractionsContent({ sections }) {
  const section = (type) => sections.find((item) => item.type === type);
  const meaning = section("equivalent_meaning");
  const method = section("equivalent_method");
  const examples = section("visual_examples");
  const keyIdea = section("key_idea");
  const think = section("think");
  const mistakes = section("equivalent_mistakes");
  const summary = section("equivalent_summary");
  const tones = { orange: "border-orange-200 bg-orange-50/70 text-orange-600", green: "border-emerald-200 bg-emerald-50/70 text-emerald-700", blue: "border-blue-200 bg-sky-50/70 text-blue-600" };
  return <div className="mt-3 grid gap-3">
    <div className="grid gap-3 lg:grid-cols-[minmax(0,40%)_minmax(0,60%)]">
      {meaning && <section className="rounded-[22px] border border-emerald-200 bg-emerald-50/70 p-4 shadow-sm"><h2 className="flex items-center gap-2 text-base font-black text-emerald-700"><Target className="size-5" />{meaning.title}</h2><div className="mt-3 grid gap-1">{meaning.items?.map((item) => <p key={item} className="text-sm font-semibold text-slate-700">{item}</p>)}</div><Star className="ml-auto size-6 fill-green-500 text-green-500" /></section>}
      {method && <section className="grid items-center gap-4 rounded-[22px] border border-violet-200 bg-violet-50/70 p-4 shadow-sm md:grid-cols-[minmax(0,1fr)_auto]"><div><h2 className="flex items-center gap-2 text-base font-black text-violet-700"><Puzzle className="size-5" />{method.title}</h2><p className="mt-2 text-sm font-semibold leading-5 text-slate-700">{method.text}</p></div><div className="flex items-center justify-center gap-2 text-violet-700"><span className="grid size-10 place-items-center rounded-full bg-violet-600 text-sm font-black text-white shadow-md">{method.multiplier}</span><EquivalentFraction value={method.equation?.from} /><span className="font-black">=</span><EquivalentFraction value={method.equation?.operation} /><span className="font-black">=</span><EquivalentFraction value={method.equation?.result} /></div></section>}
    </div>
    {(examples || keyIdea) && <section><h2 className="flex items-center gap-2 text-lg font-black text-nt-blue"><BookOpen className="size-5" />{examples?.title}</h2><div className="mt-2 grid gap-3 sm:grid-cols-2 xl:grid-cols-4">{examples?.items?.map((item) => <article key={item.kind} className={`grid min-h-52 content-between rounded-[22px] border p-4 text-center shadow-sm ${tones[item.tone]}`}><div className="flex items-center justify-center gap-3"><EquivalentFraction value={item.left} /><span className="font-black">=</span><EquivalentFraction value={item.right} /></div><EquivalentVisual item={item} /><p className="text-xs font-semibold leading-5 text-slate-600">{item.text}</p></article>)}{keyIdea && <aside className="grid min-h-52 content-between rounded-[22px] border border-blue-200 bg-blue-50/70 p-4 shadow-sm"><h3 className="flex items-center gap-2 font-black text-blue-600"><Lightbulb className="size-5 text-amber-400" />{keyIdea.title}</h3><p className="text-sm font-semibold leading-6 text-slate-700">{keyIdea.text}</p><Star className="ml-auto size-7 fill-blue-500 text-blue-500" /></aside>}</div></section>}
    <div className="grid gap-3 lg:grid-cols-2">
      {think && <section className="grid items-center gap-4 rounded-[22px] border border-violet-200 bg-violet-50/60 p-4 shadow-sm sm:grid-cols-[minmax(0,1fr)_minmax(190px,44%)]"><div><h2 className="flex items-center gap-2 font-black text-violet-700"><Eye className="size-5" />{think.title}</h2><p className="mt-3 whitespace-pre-line text-sm font-semibold leading-6 text-slate-700">{think.text}</p></div><div className="flex items-center justify-center gap-2">{think.images?.map((image, index) => <div key={image} className="contents"><img src={assetUrl(image)} alt="Chocolate dividido en partes equivalentes" className="h-24 min-w-0 flex-1 object-contain drop-shadow-md" />{index === 0 && <span className="font-black text-violet-700">=</span>}</div>)}</div></section>}
      {mistakes && <section className="relative overflow-hidden rounded-[22px] border border-red-200 bg-red-50/70 p-4 shadow-sm"><div className="max-w-[75%]"><h2 className="flex items-center gap-2 font-black text-red-600"><AlertTriangle className="size-5" />{mistakes.title}</h2><div className="mt-3 grid gap-2">{mistakes.items?.map((item) => <p key={item} className="flex gap-2 text-xs font-semibold leading-5 text-slate-700"><span className="font-black text-red-500">×</span>{item}</p>)}</div></div><img src={assetUrl(mistakes.image)} alt="NEO señalando errores comunes" className="absolute bottom-0 right-1 h-32 w-[28%] object-contain drop-shadow-md" /></section>}
    </div>
    {summary && <section className="grid items-center gap-3 rounded-[22px] border border-emerald-200 bg-emerald-50/70 px-4 py-3 shadow-sm sm:grid-cols-[70px_minmax(0,1fr)]"><img src={assetUrl(summary.image)} alt="NEO celebrando lo aprendido" className="size-20 object-contain drop-shadow-md" /><div><h2 className="font-black text-emerald-700">{summary.title}</h2><div className="mt-2 grid gap-2 lg:grid-cols-3">{summary.items?.map((item) => <p key={item} className="flex gap-2 text-xs font-semibold text-slate-700"><CheckCircle2 className="size-4 shrink-0 text-emerald-500" />{item}</p>)}</div></div></section>}
  </div>;
}

function SameDenominatorContent({ sections }) {
  const section = (type) => sections.find((item) => item.type === type);
  const reminder = section("denominator_reminder");
  const example = section("addition_example");
  const keyIdea = section("addition_key_idea");
  const why = section("why_it_works");
  const mistakes = section("addition_mistakes");
  const nowYou = section("now_you");
  const tones = { blue: "border-blue-200 bg-sky-50/70 text-blue-600", green: "border-emerald-200 bg-emerald-50/70 text-emerald-700", violet: "border-violet-200 bg-violet-50/70 text-violet-700" };

  return <div className="mt-3 grid gap-3">
    {reminder && <section className="flex items-center gap-3 rounded-[18px] border border-blue-200 bg-blue-50/70 px-4 py-3 shadow-sm"><span className="grid size-9 shrink-0 place-items-center rounded-xl bg-white text-amber-400 shadow-sm"><Lightbulb className="size-5" /></span><p className="text-sm font-bold text-nt-text-primary">{reminder.text}</p></section>}
    {example && <section><h2 className="flex items-center gap-2 text-lg font-black text-nt-blue"><BookOpen className="size-5" />{example.title}</h2><div className="mt-2 grid items-stretch gap-2 sm:grid-cols-[1fr_auto_1fr_auto_1fr]">{example.items?.map((item) => <div key={`${item.fraction?.numerator}/${item.fraction?.denominator}`} className="contents"><article className={`grid min-h-48 grid-cols-[64px_minmax(0,1fr)] items-center gap-3 rounded-[22px] border p-4 shadow-sm ${tones[item.tone]}`}><div className="text-center"><EquivalentFraction value={item.fraction} /><p className="mt-3 text-xs font-bold leading-5 text-nt-text-primary">{item.text}</p></div><img src={assetUrl(item.image)} alt={item.text} className="mx-auto h-32 w-full object-contain drop-shadow-md" /></article>{item.operator && <span className="hidden self-center text-3xl font-black text-nt-text-primary sm:block">{item.operator}</span>}<span className="text-center text-2xl font-black text-nt-text-primary sm:hidden">{item.operator}</span></div>)}</div></section>}
    {keyIdea && <section className="grid items-center gap-4 rounded-[20px] border border-amber-200 bg-amber-50/80 px-4 py-3 shadow-sm lg:grid-cols-[minmax(0,1fr)_auto]"><p className="flex flex-wrap items-center gap-x-1.5 text-sm font-semibold text-slate-700"><Star className="mr-2 size-6 fill-amber-400 text-amber-400" /><strong className="text-amber-900">{keyIdea.label}</strong><span>{keyIdea.textBefore}</span><strong className="text-blue-600">{keyIdea.highlightBlue}</strong><span>{keyIdea.textMiddle}</span><strong className="text-orange-600">{keyIdea.highlightOrange}</strong></p><div className="flex items-center justify-center gap-2 rounded-2xl bg-amber-100/80 px-4 py-2 text-emerald-700"><strong className="mr-1 text-sm text-amber-900">{keyIdea.formulaLabel}</strong><EquivalentFraction value={keyIdea.formula?.left} /><Plus className="size-4" strokeWidth={3} /><EquivalentFraction value={keyIdea.formula?.right} /><span className="font-black">=</span><EquivalentFraction value={keyIdea.formula?.result} /></div></section>}
    <div className="grid gap-3 lg:grid-cols-2">
      {why && <section className="grid items-center gap-3 rounded-[22px] border border-emerald-200 bg-emerald-50/70 p-4 shadow-sm sm:grid-cols-[minmax(0,1fr)_100px]"><div><h2 className="flex items-center gap-2 font-black text-emerald-700"><CheckCircle2 className="size-5 fill-emerald-600 text-white" />{why.title}</h2><p className="mt-3 text-sm font-semibold leading-6 text-slate-700">{why.text}</p><p className="mt-3 rounded-[16px] border border-emerald-200 bg-white/60 px-3 py-2 text-xs font-semibold leading-5 text-emerald-900"><strong>{why.importantLabel}</strong> {why.importantText}</p></div><img src={assetUrl(why.image)} alt="Pizza animada" className="mx-auto h-28 w-full object-contain drop-shadow-md" /></section>}
      {mistakes && <section className="relative overflow-hidden rounded-[22px] border border-red-200 bg-red-50/70 p-4 shadow-sm"><div className="max-w-[75%]"><h2 className="flex items-center gap-2 font-black text-red-600"><AlertTriangle className="size-5" />{mistakes.title}</h2><div className="mt-3 grid gap-2">{mistakes.items?.map((item) => <p key={item} className="flex gap-2 text-xs font-semibold leading-5 text-slate-700"><span className="grid size-4 shrink-0 place-items-center rounded-full bg-red-500 font-black text-white">×</span>{item}</p>)}</div></div><img src={assetUrl(mistakes.image)} alt="NEO advirtiendo sobre errores comunes" className="absolute bottom-0 right-1 h-32 w-[28%] object-contain drop-shadow-md" /></section>}
    </div>
    {nowYou && <section className="flex items-center gap-4 rounded-[20px] border border-violet-200 bg-gradient-to-r from-violet-50 to-sky-50 px-4 py-3 shadow-sm"><span className="grid size-10 shrink-0 place-items-center rounded-full bg-white text-red-500 shadow-sm"><Target className="size-6" /></span><div><h2 className="font-black text-violet-700">{nowYou.title}</h2><p className="mt-1 text-xs font-semibold leading-5 text-slate-700">{nowYou.text}</p></div><div className="ml-auto hidden items-center gap-2 text-violet-500 sm:flex"><Star className="size-4 fill-violet-400" /><ArrowRight className="size-7" /></div></section>}
  </div>;
}

function FractionEquation({ values }) {
  return <div className="flex min-h-12 items-center justify-center gap-2 text-nt-text-primary">{values?.map((value, index) => typeof value === "string" ? <span key={`${value}-${index}`} className="text-xl font-black">{value}</span> : <EquivalentFraction key={`${value.numerator}-${value.denominator}-${index}`} value={value} />)}</div>;
}

function MiniBlocks({ total, filled, color = "bg-blue-400" }) {
  return <div className="grid h-14 w-20 overflow-hidden rounded-sm border-2 border-slate-500 bg-white" style={{ gridTemplateColumns: `repeat(${total}, minmax(0, 1fr))` }}>{Array.from({ length: total }, (_, index) => <span key={index} className={`border-r border-slate-400 last:border-r-0 ${index < filled ? color : ""}`} />)}</div>;
}

function DifferentStepVisual({ visual }) {
  if (visual === "different_circles") return <div className="flex items-center justify-center gap-3"><span className="size-16 rounded-full border-2 border-violet-700" style={{ background: "conic-gradient(#8b5cf6 0 180deg, white 180deg)" }} /><Plus className="size-4" /><span className="size-16 rounded-full border-2 border-orange-600" style={{ background: "conic-gradient(#fb923c 0 90deg, white 90deg)" }} /></div>;
  if (visual === "equivalent_blocks") return <div className="flex items-center justify-center gap-2"><MiniBlocks total={2} filled={1} color="bg-amber-300" /><ArrowRight className="size-4 text-blue-500" /><MiniBlocks total={4} filled={2} color="bg-sky-400" /></div>;
  if (visual === "sum_blocks") return <div className="flex items-center justify-center gap-2"><MiniBlocks total={4} filled={2} color="bg-blue-500" /><Plus className="size-4" /><MiniBlocks total={4} filled={1} color="bg-green-500" /><span className="font-black">=</span><MiniBlocks total={4} filled={3} color="bg-violet-500" /></div>;
  return <span className="mx-auto block size-24 rounded-full border-2 border-violet-700" style={{ background: "repeating-conic-gradient(transparent 0 89deg, #4c1d95 89deg 90deg), conic-gradient(#8b5cf6 0 270deg, white 270deg)" }} />;
}

function DifferentDenominatorContent({ sections }) {
  const section = (type) => sections.find((item) => item.type === type);
  const idea = section("different_key_idea");
  const steps = section("different_steps");
  const why = section("different_why");
  const mistakes = section("different_mistakes");
  const practice = section("practice_more");
  const tones = { violet: "border-violet-200 bg-violet-50/60 text-violet-700", blue: "border-blue-200 bg-sky-50/60 text-blue-600", green: "border-emerald-200 bg-emerald-50/60 text-emerald-700" };

  return <div className="mt-3 grid gap-3">
    {idea && <section className="grid items-center gap-3 rounded-[20px] border border-amber-200 bg-amber-50/80 px-4 py-3 shadow-sm sm:grid-cols-[32px_minmax(0,1fr)_70px]"><Star className="size-7 fill-amber-400 text-amber-400" /><p className="text-sm font-semibold leading-6 text-amber-950"><strong>{idea.label}</strong> {idea.text}</p><img src={assetUrl(idea.image)} alt="Idea clave" className="mx-auto h-14 w-full object-contain" /></section>}
    {steps && <section><h2 className="flex items-center gap-2 text-lg font-black text-nt-blue"><BookOpen className="size-5" />{steps.title}</h2><div className="mt-2 grid items-stretch gap-2 sm:grid-cols-2 xl:grid-cols-[1fr_auto_1.15fr_auto_1.25fr_auto_.85fr]">{steps.items?.map((item, index) => <div key={item.number} className="contents"><article className={`grid min-h-64 content-between rounded-[22px] border p-3 shadow-sm ${tones[item.tone]}`}><div><h3 className="flex items-start gap-2 text-sm font-black leading-5"><span className="grid size-7 shrink-0 place-items-center rounded-full bg-current text-xs text-white [color:inherit]"><span className="text-white">{item.number}</span></span><span>{item.title}{item.subtitle && <small className="mt-1 block text-xs font-bold">{item.subtitle}</small>}</span></h3><FractionEquation values={item.equation} /></div><DifferentStepVisual visual={item.visual} />{item.text && <p className="mt-3 text-center text-xs font-semibold leading-5 text-slate-700">{item.text}</p>}</article>{index < steps.items.length - 1 && <ArrowRight className="hidden size-5 self-center text-violet-500 xl:block" />}</div>)}</div></section>}
    <div className="grid gap-3 lg:grid-cols-2">
      {why && <section className="relative overflow-hidden rounded-[22px] border border-emerald-200 bg-emerald-50/70 p-4 shadow-sm"><div className="max-w-[78%]"><h2 className="flex items-center gap-2 font-black text-emerald-700"><CheckCircle2 className="size-5" />{why.title}</h2><div className="mt-3 grid gap-2">{why.items?.map((item) => <p key={item} className="flex gap-2 text-xs font-semibold leading-5 text-slate-700"><Check className="size-4 shrink-0 text-emerald-500" strokeWidth={3} />{item}</p>)}</div></div><img src={assetUrl(why.image)} alt="Pizza" className="absolute bottom-2 right-3 h-20 w-[22%] object-contain drop-shadow-md" /></section>}
      {mistakes && <section className="relative overflow-hidden rounded-[22px] border border-red-200 bg-red-50/70 p-4 shadow-sm"><div className="max-w-[75%]"><h2 className="flex items-center gap-2 font-black text-red-600"><AlertTriangle className="size-5" />{mistakes.title}</h2><div className="mt-3 grid gap-2">{mistakes.items?.map((item) => <p key={item} className="flex gap-2 text-xs font-semibold leading-5 text-slate-700"><span className="grid size-4 shrink-0 place-items-center rounded-full bg-red-500 font-black text-white">×</span>{item}</p>)}</div></div><img src={assetUrl(mistakes.image)} alt="NEO advirtiendo sobre errores" className="absolute bottom-0 right-1 h-32 w-[28%] object-contain drop-shadow-md" /></section>}
    </div>
    {practice && <section className="grid items-center gap-3 rounded-[20px] border border-violet-200 bg-gradient-to-r from-violet-50 to-sky-50 px-4 py-3 shadow-sm sm:grid-cols-[48px_minmax(0,1fr)_150px]"><Target className="size-9 text-red-500" /><div><h2 className="font-black text-violet-700">{practice.title}</h2><p className="mt-1 text-xs font-semibold leading-5 text-slate-700">{practice.text}</p></div><img src={assetUrl(practice.image)} alt="Libro de práctica" className="mx-auto h-16 w-full object-contain" /></section>}
  </div>;
}

function FractionCircle({ numerator, denominator, color }) {
  const total = Math.max(1, Number(denominator) || 1);
  const filled = Math.min(total, Math.max(0, Number(numerator) || 0));
  const segment = 360 / total;
  const fillAngle = segment * filled;
  return <span className="block size-20 shrink-0 rounded-full border-2 border-slate-500 shadow-sm" style={{ background: `repeating-conic-gradient(from -90deg, transparent 0 ${segment - 1}deg, #64748b ${segment - 1}deg ${segment}deg), conic-gradient(from -90deg, ${color} 0 ${fillAngle}deg, white ${fillAngle}deg 360deg)` }} />;
}

function SubtractionTypesCard({ item }) {
  const tones = { green: "border-emerald-200 bg-emerald-50/60 text-emerald-700", violet: "border-violet-200 bg-violet-50/60 text-violet-700" };
  return <article className={`grid min-h-[310px] content-between rounded-[22px] border p-4 shadow-sm ${tones[item.tone]}`}><div><h3 className="flex items-start gap-3 text-lg font-black"><span className="grid size-8 shrink-0 place-items-center rounded-full bg-current text-white"><span className="text-white">{item.number}</span></span><span>{item.title}<small className="mt-1 block text-xs font-semibold text-slate-700">{item.text}</small></span></h3><div className="mt-3 overflow-x-auto"><FractionEquation values={item.equation} /></div></div><div className="mt-3 flex min-w-0 items-center justify-around gap-2 overflow-x-auto py-1">{item.figures?.map((figure, index) => typeof figure === "string" ? <span key={`${figure}-${index}`} className="text-xl font-black text-nt-text-primary">{figure}</span> : <FractionCircle key={`${figure.numerator}-${figure.denominator}-${index}`} {...figure} />)}</div><div className="mt-3 grid gap-1 rounded-[16px] border border-current/10 bg-white/55 px-3 py-2 text-center">{item.notes?.map((note, index) => typeof note === "string" || note.text ? <p key={index} className="text-xs font-semibold leading-5 text-slate-700">{typeof note === "string" ? note : note.text}</p> : <div key={index} className="flex flex-wrap items-center justify-center gap-1.5 text-xs font-semibold text-slate-700"><span>{note.prefix}</span><EquivalentFraction value={note.from} /><span>{note.middle}</span><EquivalentFraction value={note.to} /><span>{note.suffix}</span></div>)}</div></article>;
}

function FractionSubtractionContent({ sections }) {
  const section = (type) => sections.find((item) => item.type === type);
  const idea = section("subtraction_key_idea");
  const types = section("subtraction_types");
  const mistakes = section("subtraction_mistakes");
  const tips = section("subtraction_tips");
  const summary = section("subtraction_summary");

  return <div className="mt-3 grid gap-3">
    {idea && <section className="grid items-center gap-3 rounded-[20px] border border-amber-200 bg-amber-50/80 px-4 py-3 shadow-sm sm:grid-cols-[32px_minmax(0,1fr)_70px]"><Star className="size-7 fill-amber-400 text-amber-400" /><div><h2 className="font-black text-amber-900">{idea.title}</h2>{idea.paragraphs?.map((paragraph) => <p key={paragraph} className="mt-1 text-sm font-semibold leading-5 text-slate-700">{paragraph}</p>)}</div><img src={assetUrl(idea.image)} alt="Idea clave" className="mx-auto h-14 w-full object-contain" /></section>}
    {types && <section><h2 className="flex items-center gap-2 text-lg font-black text-nt-blue"><BookOpen className="size-5" />{types.title}</h2><div className="mt-2 grid gap-3 lg:grid-cols-2">{types.items?.map((item) => <SubtractionTypesCard key={item.number} item={item} />)}</div></section>}
    <div className="grid gap-3 lg:grid-cols-2">
      {mistakes && <section className="relative overflow-hidden rounded-[22px] border border-red-200 bg-red-50/70 p-4 shadow-sm"><div className="max-w-[75%]"><h2 className="flex items-center gap-2 font-black text-red-600"><AlertTriangle className="size-5" />{mistakes.title}</h2><div className="mt-3 grid gap-2">{mistakes.items?.map((item) => <p key={item} className="flex gap-2 text-xs font-semibold leading-5 text-slate-700"><span className="font-black text-red-500">×</span>{item}</p>)}</div></div><img src={assetUrl(mistakes.image)} alt="NEO señalando errores" className="absolute bottom-0 right-1 h-28 w-[27%] object-contain drop-shadow-md" /></section>}
      {tips && <section className="relative overflow-hidden rounded-[22px] border border-amber-200 bg-amber-50/60 p-4 shadow-sm"><div className="max-w-[78%]"><h2 className="flex items-center gap-2 font-black text-amber-900"><Lightbulb className="size-5 text-amber-400" />{tips.title}</h2><div className="mt-3 grid gap-2">{tips.items?.map((item) => <p key={item} className="flex gap-2 text-xs font-semibold leading-5 text-slate-700"><Check className="size-4 shrink-0 text-green-500" strokeWidth={3} />{item}</p>)}</div></div><img src={assetUrl(tips.image)} alt="NEO compartiendo consejos" className="absolute bottom-0 right-1 h-28 w-[25%] object-contain drop-shadow-md" /></section>}
    </div>
    {summary && <section className="grid items-center gap-3 rounded-[20px] border border-violet-200 bg-gradient-to-r from-violet-50 to-sky-50 px-4 py-3 shadow-sm sm:grid-cols-[minmax(0,1fr)_150px]"><div><h2 className="font-black text-violet-700">{summary.title}</h2><div className="mt-2 grid gap-1">{summary.items?.map((item) => <p key={item} className="flex gap-2 text-xs font-semibold text-slate-700"><span className="text-violet-500">•</span>{item}</p>)}</div></div><img src={assetUrl(summary.image)} alt="Libros de estudio" className="mx-auto h-20 w-full object-contain" /></section>}
  </div>;
}

function LessonTwoContent({ sections, onNext }) {
  const section = (type) => sections.find((item) => item.type === type);
  const objective = section("lesson_objective");
  const definition = section("definition");
  const plainLanguage = section("plain_language");
  const examples = section("daily_examples");
  const representations = section("simple_representations");
  const importantNote = section("important_note");
  const keepLearning = section("keep_learning");
  const tones = {
    amber: "border-amber-100 bg-amber-50/70 text-amber-800",
    violet: "border-violet-100 bg-violet-50/70 text-violet-800",
    rose: "border-rose-100 bg-rose-50/70 text-rose-700",
  };

  return (
    <div className="mt-3 grid gap-3">
      {objective && (
        <section className="flex items-center gap-3 rounded-[18px] border border-emerald-200 bg-emerald-50/80 px-4 py-3 shadow-sm">
          <span className="grid size-7 shrink-0 place-items-center rounded-full bg-emerald-500 text-white"><Check className="size-4" strokeWidth={3} /></span>
          <p className="text-sm font-semibold text-slate-700"><strong className="font-black text-emerald-800">{objective.title}:</strong> {objective.text}</p>
        </section>
      )}

      {(definition || plainLanguage) && (
        <div className="grid gap-3 md:grid-cols-2">
          {definition && <section className="rounded-[22px] border border-blue-100 bg-white p-4 shadow-sm"><h2 className="flex items-center gap-2 text-lg font-black text-nt-text-primary"><span className="grid size-7 place-items-center rounded-full bg-nt-blue text-sm text-white">?</span>{definition.title}</h2><p className="mt-2 text-sm font-semibold leading-6 text-slate-700">{definition.text}</p></section>}
          {plainLanguage && <section className="grid min-h-36 items-center gap-3 overflow-hidden rounded-[22px] border border-sky-100 bg-gradient-to-br from-white to-sky-50 p-4 sm:grid-cols-[minmax(0,1fr)_130px]"><div><h2 className="text-base font-black text-nt-text-primary">{plainLanguage.title}</h2><p className="mt-2 text-sm font-semibold leading-6 text-slate-700">{plainLanguage.text}</p></div>{plainLanguage.image && <img src={assetUrl(plainLanguage.image)} alt="NEO explicando una idea" className="mx-auto h-32 w-full object-contain drop-shadow-md" />}</section>}
        </div>
      )}

      {examples && Array.isArray(examples.items) && (
        <section>
          <h2 className="flex items-center gap-2 text-lg font-black text-nt-text-primary"><span className="grid size-7 place-items-center rounded-lg bg-violet-100 text-violet-700"><BookOpen className="size-4" /></span>{examples.title}</h2>
          <div className="mt-2 grid gap-3 md:grid-cols-3">
            {examples.items.map((item) => (
              <article key={item.title} className={`grid min-h-[230px] content-between rounded-[22px] border p-4 shadow-sm ${tones[item.tone] ?? tones.violet}`}>
                <div><h3 className="text-base font-black">{item.title}</h3><p className="mt-1 text-xs font-semibold leading-5 text-slate-700">{item.text}</p></div>
                <div className="mt-2 grid grid-cols-[minmax(0,1fr)_48px] items-end gap-3">
                  <img src={assetUrl(item.image)} alt={item.title} className="mx-auto h-24 w-full object-contain drop-shadow-md" />
                  <StackedFraction numerator={item.numerator} denominator={item.denominator} className="text-current" />
                </div>
              </article>
            ))}
          </div>
        </section>
      )}

      {(representations || importantNote) && (
        <div className="grid gap-3 lg:grid-cols-[minmax(0,2fr)_minmax(220px,1fr)]">
          {representations && <section><h2 className="flex items-center gap-2 text-lg font-black text-nt-text-primary"><span className="grid size-7 place-items-center rounded-lg bg-blue-100 text-nt-blue"><BookOpen className="size-4" /></span>{representations.title}</h2>{representations.text && <p className="mt-1 text-xs font-semibold text-slate-600">{representations.text}</p>}<div className="mt-2 grid gap-3 rounded-[20px] border border-blue-100 bg-sky-50/70 p-4 sm:grid-cols-2">{representations.items?.map((item) => <div key={`${item.numerator}/${item.denominator}`} className="flex items-center justify-center gap-4"><span className={`size-16 shrink-0 rounded-full border-2 border-blue-400 ${item.visual === "half" ? "bg-[linear-gradient(90deg,white_50%,#60a5fa_50%)]" : "bg-[conic-gradient(#38bdf8_0_25%,white_25%_100%)]"}`} /><div><StackedFraction numerator={item.numerator} denominator={item.denominator} className="text-nt-blue" /><p className="mt-1 text-xs font-bold text-slate-600">{item.caption}</p></div></div>)}</div></section>}
          {importantNote && <aside className="self-end rounded-[22px] border border-amber-200 bg-amber-50 p-4 shadow-sm"><h2 className="flex items-center gap-2 text-lg font-black text-amber-700"><Star className="size-5 fill-amber-400 text-amber-400" />{importantNote.title}</h2><p className="mt-2 text-sm font-semibold leading-6 text-slate-700">{importantNote.text}</p></aside>}
        </div>
      )}

      {keepLearning && <section className="grid items-center gap-3 overflow-hidden rounded-[22px] border border-violet-100 bg-gradient-to-r from-violet-50 via-white to-sky-50 px-4 py-3 shadow-sm sm:grid-cols-[76px_minmax(0,1fr)_44px]">{keepLearning.image && <img src={assetUrl(keepLearning.image)} alt="NEO" className="mx-auto size-20 object-contain drop-shadow-md" />}<div><h2 className="text-base font-black text-nt-purple">{keepLearning.title}</h2><p className="mt-1 text-xs font-semibold leading-5 text-slate-700">{keepLearning.text}</p></div>{onNext && <button type="button" onClick={onNext} aria-label="Ir a la siguiente lección" className="grid size-10 place-items-center rounded-full bg-white text-orange-500 shadow-md transition hover:translate-x-0.5"><ArrowRight className="size-5" /></button>}</section>}
    </div>
  );
}

function LessonThreeContent({ sections }) {
  const section = (type) => sections.find((item) => item.type === type);
  const parts = section("fraction_parts");
  const examples = section("more_examples");
  const comparison = section("comparison_table");
  const remember = section("remember");
  const neoHelp = section("neo_help");
  const exampleTones = {
    violet: "border-violet-100 bg-violet-50/50 text-violet-700",
    blue: "border-blue-100 bg-sky-50/50 text-blue-600",
    green: "border-emerald-100 bg-emerald-50/40 text-emerald-600",
  };

  return (
    <div className="mt-3 grid gap-3">
      {parts && (
        <section className="grid items-stretch gap-3 md:grid-cols-2 xl:grid-cols-[minmax(0,1fr)_150px_minmax(0,1fr)_190px]">
          <article className="grid content-center rounded-[22px] border border-amber-200 bg-amber-50/70 p-4 shadow-sm">
            <div className="flex items-center justify-between gap-4"><div><h2 className="text-base font-black text-orange-600">{parts.numerator?.title}</h2><p className="mt-2 text-xs font-semibold leading-5 text-slate-700">{parts.numerator?.text}</p><p className="mt-2 text-xs font-bold leading-5 text-slate-700">{parts.numerator?.detail}</p></div><div className="rounded-2xl bg-white px-4 py-3 shadow-sm"><StackedFraction numerator={parts.fraction?.numerator} denominator={parts.fraction?.denominator} className="text-orange-500" /></div></div>
          </article>
          <div className="grid place-items-center rounded-[22px] bg-white p-2">
            <div className="relative size-32 rounded-full border-2 border-nt-text-primary bg-[conic-gradient(#f59e0b_0_75%,white_75%_100%)] shadow-md"><span className="absolute left-1/2 top-0 h-full w-px -translate-x-1/2 bg-nt-text-primary" /><span className="absolute left-0 top-1/2 h-px w-full -translate-y-1/2 bg-nt-text-primary" /></div>
          </div>
          <article className="grid content-center rounded-[22px] border border-emerald-200 bg-emerald-50/60 p-4 shadow-sm">
            <div className="flex items-center justify-between gap-4"><div><h2 className="text-base font-black text-emerald-700">{parts.denominator?.title}</h2><p className="mt-2 text-xs font-semibold leading-5 text-slate-700">{parts.denominator?.text}</p><p className="mt-2 text-xs font-bold leading-5 text-slate-700">{parts.denominator?.detail}</p></div><StackedFraction numerator={parts.fraction?.numerator} denominator={parts.fraction?.denominator} className="text-emerald-600" /></div>
          </article>
          <article className="grid content-between rounded-[22px] border border-violet-100 bg-violet-50/60 p-4 shadow-sm"><div><h2 className="text-sm font-black text-violet-700">{parts.example?.title}</h2><p className="mt-2 text-xs font-semibold leading-5 text-slate-700">{parts.example?.text}</p></div>{parts.example?.image && <img src={assetUrl(parts.example.image)} alt="Ejemplo de fracción con pizza" className="mx-auto mt-2 h-28 w-full object-contain drop-shadow-md" />}</article>
        </section>
      )}

      {examples && Array.isArray(examples.items) && <section><h2 className="flex items-center gap-2 text-lg font-black text-nt-text-primary"><BookOpen className="size-5 text-nt-blue" />{examples.title}</h2><div className="mt-2 grid gap-3 md:grid-cols-3">{examples.items.map((item) => <article key={item.title} className={`grid min-h-36 grid-cols-[minmax(0,1fr)_48px] items-center gap-3 rounded-[22px] border p-4 shadow-sm ${exampleTones[item.tone] ?? exampleTones.violet}`}><div><img src={assetUrl(item.image)} alt={item.title} className="h-20 w-full object-contain drop-shadow-sm" /><p className="mt-2 text-xs font-semibold leading-5 text-slate-700">{item.text}</p></div><StackedFraction numerator={item.numerator} denominator={item.denominator} className="text-current" /></article>)}</div></section>}

      {(comparison || remember) && <div className="grid items-stretch gap-3 lg:grid-cols-[minmax(0,2fr)_minmax(240px,1fr)]">
        {comparison && <section className="min-w-0"><h2 className="flex items-center gap-2 text-lg font-black text-nt-text-primary"><BookOpen className="size-5 text-nt-blue" />{comparison.title}</h2><div className="mt-2 overflow-x-auto rounded-[20px] border border-blue-100"><table className="w-full min-w-[620px] border-collapse bg-white text-left text-xs"><thead className="bg-blue-50 text-nt-blue"><tr><th className="px-3 py-2 font-black">{comparison.columns?.fraction}</th><th className="px-3 py-2 font-black">{comparison.columns?.numerator}</th><th className="px-3 py-2 font-black">{comparison.columns?.denominator}</th><th className="px-3 py-2 font-black">{comparison.columns?.interpretation}</th></tr></thead><tbody>{comparison.rows?.map((row) => <tr key={`${row.numerator}/${row.denominator}`} className="border-t border-blue-100"><td className="px-3 py-2"><StackedFraction numerator={row.numerator} denominator={row.denominator} className="text-nt-purple" /></td><td className="px-3 py-2 font-semibold text-slate-700"><strong className="mr-2 text-lg text-violet-600">{row.numerator}</strong>{row.numeratorText}</td><td className="px-3 py-2 font-semibold text-slate-700"><strong className="mr-2 text-lg text-blue-600">{row.denominator}</strong>{row.denominatorText}</td><td className="px-3 py-2 font-semibold leading-5 text-slate-700">{row.interpretation}</td></tr>)}</tbody></table></div></section>}
        {remember && <aside className="grid overflow-hidden rounded-[22px] border border-emerald-200 bg-emerald-50/70 p-4 shadow-sm sm:grid-cols-[minmax(0,1fr)_110px] lg:grid-cols-1 xl:grid-cols-[minmax(0,1fr)_110px]"><div><h2 className="flex items-center gap-2 text-base font-black text-emerald-700"><Star className="size-5 fill-emerald-600 text-emerald-600" />{remember.title}</h2><p className="mt-3 text-xs font-semibold leading-5 text-slate-700">{remember.text}</p><p className="mt-3 text-sm font-black leading-6 text-nt-text-primary">{remember.detail}</p></div>{remember.image && <img src={assetUrl(remember.image)} alt="NEO recordando" className="mx-auto h-32 w-full self-end object-contain drop-shadow-md" />}</aside>}
      </div>}

      {neoHelp && <section className="grid items-center gap-3 overflow-hidden rounded-[20px] border border-violet-100 bg-gradient-to-r from-violet-50 via-white to-violet-50 px-4 py-3 shadow-sm sm:grid-cols-[64px_minmax(0,1fr)]">{neoHelp.image && <img src={assetUrl(neoHelp.image)} alt="NEO ayudando" className="mx-auto size-16 object-contain drop-shadow-md" />}<div><h2 className="text-sm font-black text-nt-purple">{neoHelp.title}</h2><p className="mt-1 text-xs font-semibold leading-5 text-slate-700">{neoHelp.text}</p></div></section>}
    </div>
  );
}

function FractionFigure({ numerator, denominator, color, visual = "circle" }) {
  const taken = Number(numerator) || 0;
  const total = Math.max(1, Number(denominator) || 1);
  const wholes = Math.max(1, Math.ceil(taken / total));

  if (visual === "blocks") {
    return (
      <div className="grid gap-1" style={{ gridTemplateColumns: `repeat(${total}, 14px)` }}>
        {Array.from({ length: total * wholes }, (_, index) => (
          <span key={index} className="size-3.5 rounded-[3px] border border-slate-300" style={{ backgroundColor: index < taken ? color : "#f8fafc" }} />
        ))}
      </div>
    );
  }

  return (
    <div className="flex items-center justify-center gap-1">
      {Array.from({ length: wholes }, (_, wholeIndex) => {
        const filled = Math.min(total, Math.max(0, taken - wholeIndex * total));
        const slice = 360 / total;
        return <span key={wholeIndex} className="size-14 rounded-full border-2 border-slate-400" style={{ background: `repeating-conic-gradient(from -90deg, transparent 0deg ${slice - 1.5}deg, #64748b ${slice - 1.5}deg ${slice}deg), conic-gradient(from -90deg, ${color} 0deg ${filled * slice}deg, #f8fafc ${filled * slice}deg 360deg)` }} />;
      })}
    </div>
  );
}

function LessonFourContent({ sections }) {
  const section = (type) => sections.find((item) => item.type === type);
  const proper = section("proper_fractions");
  const improper = section("improper_fractions");
  const lifeExample = section("life_example");
  const continueLearning = section("continue_learning");

  const renderGroup = (group, properGroup) => group && (
    <section>
      <h2 className={`text-lg font-black ${properGroup ? "text-emerald-700" : "text-orange-600"}`}>{group.title}</h2>
      <p className="mt-1 text-xs font-semibold text-slate-700">{group.text}</p>
      <div className="mt-2 grid items-stretch gap-3 lg:grid-cols-[minmax(0,1fr)_220px]">
        <div className="grid gap-3 sm:grid-cols-2 xl:grid-cols-4">
          {group.items?.map((item) => <article key={`${item.numerator}/${item.denominator}`} className={`grid min-h-40 content-between place-items-center rounded-[20px] border p-3 text-center shadow-sm ${properGroup ? "border-emerald-200 bg-emerald-50/50" : "border-orange-200 bg-orange-50/50"}`}><div className="grid grid-cols-[minmax(0,1fr)_42px] items-center gap-3"><FractionFigure numerator={item.numerator} denominator={item.denominator} visual={item.visual} color={properGroup ? "#5bb226" : "#f59e0b"} /><StackedFraction numerator={item.numerator} denominator={item.denominator} className={properGroup ? "text-emerald-700" : "text-orange-600"} /></div><div><p className="text-sm font-black text-nt-text-primary">{item.comparison}</p><p className={`mt-1 text-xs font-black ${properGroup ? "text-emerald-700" : "text-orange-600"}`}>{item.result}</p></div></article>)}
        </div>
        {group.note && <aside className={`grid content-center rounded-[22px] border p-4 shadow-sm ${properGroup ? "border-emerald-200 bg-emerald-50/70" : "border-orange-200 bg-orange-50/70"}`}><h3 className={`flex items-center gap-2 text-base font-black ${properGroup ? "text-emerald-700" : "text-orange-600"}`}>{properGroup ? <CheckCircle2 className="size-5" /> : <Star className="size-5" />}{group.note.title}</h3><p className="mt-3 text-xs font-semibold leading-6 text-slate-700">{group.note.text}</p></aside>}
      </div>
    </section>
  );

  return (
    <div className="mt-3 grid gap-4">
      {renderGroup(proper, true)}
      {renderGroup(improper, false)}
      {lifeExample && <section className="grid items-center gap-3 rounded-[22px] border border-violet-100 bg-gradient-to-r from-violet-50 via-white to-violet-50 p-3 shadow-sm sm:grid-cols-[110px_minmax(0,1fr)_52px_150px_80px]"><img src={assetUrl(lifeExample.image)} alt="Pizza" className="mx-auto h-20 w-full object-contain drop-shadow-md" /><div><h2 className="text-sm font-black text-violet-700">{lifeExample.title}</h2><p className="mt-1 text-xs font-semibold leading-5 text-slate-700">{lifeExample.text}</p></div><StackedFraction numerator={lifeExample.numerator} denominator={lifeExample.denominator} className="text-nt-purple" /><p className="text-xs font-black leading-5 text-nt-text-primary">{lifeExample.message}</p>{lifeExample.neoImage && <img src={assetUrl(lifeExample.neoImage)} alt="NEO" className="mx-auto h-20 w-full object-contain drop-shadow-md" />}</section>}
      {continueLearning && <section className="grid items-center gap-3 overflow-hidden rounded-[22px] border border-sky-100 bg-gradient-to-r from-sky-50 via-white to-blue-50 px-4 py-3 shadow-sm sm:grid-cols-[68px_minmax(0,1fr)]">{continueLearning.image && <img src={assetUrl(continueLearning.image)} alt="NEO" className="mx-auto size-16 object-contain drop-shadow-md" />}<div><h2 className="text-base font-black text-nt-blue">{continueLearning.title}</h2><p className="mt-1 text-xs font-semibold leading-5 text-slate-700">{continueLearning.text}</p></div></section>}
    </div>
  );
}

function ReviewListCard({ section, variant }) {
  if (!section) return null;
  const ideas = variant === "ideas";
  return (
    <section className={`grid min-h-56 overflow-hidden rounded-[22px] border p-4 shadow-sm sm:grid-cols-[minmax(0,1fr)_130px] ${ideas ? "border-amber-200 bg-amber-50/70" : "border-rose-200 bg-rose-50/60"}`}>
      <div><h2 className={`text-lg font-black ${ideas ? "text-amber-800" : "text-red-700"}`}>{section.title}</h2><ul className="mt-3 grid gap-2">{section.items?.map((item) => <li key={item} className="flex gap-2 text-xs font-semibold leading-5 text-slate-700"><span className={`mt-1 grid size-4 shrink-0 place-items-center rounded-full text-[10px] text-white ${ideas ? "bg-amber-500" : "bg-red-400"}`}>{ideas ? "✓" : "!"}</span>{item}</li>)}</ul></div>
      {section.image && <img src={assetUrl(section.image)} alt="" className="mx-auto h-36 w-full self-end object-contain drop-shadow-md" />}
    </section>
  );
}

function LessonFiveContent({ sections }) {
  const section = (type) => sections.find((item) => item.type === type);
  const summary = section("learning_summary");
  const ideas = section("important_ideas");
  const mistakes = section("review_mistakes");
  const ready = section("ready_for_practice");
  const tones = {
    blue: "border-blue-100 bg-sky-50/60 text-blue-700",
    violet: "border-violet-100 bg-violet-50/60 text-violet-700",
    amber: "border-amber-100 bg-amber-50/60 text-amber-700",
    green: "border-emerald-100 bg-emerald-50/60 text-emerald-700",
    rose: "border-rose-100 bg-rose-50/60 text-rose-700",
  };

  return (
    <div className="mt-3 grid gap-4">
      {summary && <section><h2 className="text-xl font-black text-nt-text-primary">{summary.title}</h2><div className="mt-2 grid gap-3 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-5">{summary.items?.map((item) => <article key={item.number} className={`grid min-h-[245px] content-between overflow-hidden rounded-[22px] border p-3 shadow-sm ${tones[item.tone] ?? tones.blue}`}><div><span className="grid size-7 place-items-center rounded-full bg-current/10 text-sm font-black">{item.number}</span><h3 className="mt-2 text-sm font-black leading-5">{item.title}</h3></div><div className="my-2 grid min-h-20 place-items-center">{item.kind === "image" && item.image && <img src={assetUrl(item.image)} alt="" className="h-20 w-full object-contain drop-shadow-md" />}{item.kind === "fraction" && <div className="grid grid-cols-[minmax(0,1fr)_54px] items-center gap-2 text-[10px] font-bold"><div className="grid gap-4"><span className="text-orange-600">→ {item.numeratorLabel}</span><span className="text-emerald-700">→ {item.denominatorLabel}</span></div><StackedFraction numerator={item.numerator} denominator={item.denominator} className="text-nt-purple" /></div>}{item.kind === "classification" && <div className="flex items-end justify-center gap-3"><div className="grid place-items-center gap-1"><FractionFigure numerator={item.proper?.numerator} denominator={item.proper?.denominator} color="#5bb226" /><span className="text-[10px] font-black text-emerald-700">{item.proper?.label}</span></div><div className="grid place-items-center gap-1"><FractionFigure numerator={item.improper?.numerator} denominator={item.improper?.denominator} color="#f59e0b" /><span className="text-[10px] font-black text-orange-600">{item.improper?.label}</span></div></div>}</div><p className="text-xs font-semibold leading-5 text-slate-700">{item.text}</p></article>)}</div></section>}
      {(ideas || mistakes) && <div className="grid gap-3 lg:grid-cols-2"><ReviewListCard section={ideas} variant="ideas" /><ReviewListCard section={mistakes} variant="mistakes" /></div>}
      {ready && <section className="relative grid items-center gap-3 overflow-hidden rounded-[22px] border border-violet-100 bg-gradient-to-r from-violet-50 via-white to-sky-50 px-5 py-4 shadow-sm sm:grid-cols-[90px_minmax(0,1fr)]"><div className="pointer-events-none absolute -bottom-10 right-8 h-20 w-56 rounded-[50%] bg-gradient-to-t from-violet-200/60 to-sky-100/20" />{ready.image && <img src={assetUrl(ready.image)} alt="NEO" className="relative mx-auto size-20 object-contain drop-shadow-md" />}<div className="relative"><h2 className="text-lg font-black text-nt-purple">{ready.title}</h2><p className="mt-1 whitespace-pre-line text-xs font-semibold leading-5 text-slate-700">{ready.text}</p></div></section>}
    </div>
  );
}

function IntermediateWelcomeContent({ sections }) {
  const section = (type) => sections.find((item) => item.type === type);
  const objectives = section("level_objectives");
  const uses = section("operation_uses");
  const reminder = section("fraction_reminder");
  const objectiveIcons = { sum: Plus, multiply: Divide, puzzle: Puzzle, trophy: Trophy };
  const objectiveTones = {
    violet: "bg-violet-100 text-violet-700",
    amber: "bg-amber-100 text-orange-600",
    green: "bg-emerald-100 text-emerald-700",
    rose: "bg-rose-100 text-rose-600",
  };
  const useTones = {
    amber: "border-amber-200 bg-amber-50/60 text-amber-800",
    green: "border-emerald-200 bg-emerald-50/50 text-emerald-700",
    blue: "border-blue-200 bg-sky-50/60 text-blue-700",
    orange: "border-orange-200 bg-orange-50/50 text-orange-600",
  };

  return (
    <div className="mt-3 grid gap-4">
      {objectives && <section className="grid items-center gap-4 rounded-[22px] border border-sky-100 bg-gradient-to-r from-sky-50 via-white to-blue-50 p-4 shadow-sm lg:grid-cols-[minmax(240px,1fr)_minmax(0,2fr)]"><div className="flex items-start gap-3"><span className="grid size-12 shrink-0 place-items-center rounded-2xl bg-blue-100 text-nt-blue"><Target className="size-7" /></span><div><h2 className="text-lg font-black text-nt-blue">{objectives.title}</h2><p className="mt-1 text-sm font-semibold leading-6 text-slate-700">{objectives.text}</p></div></div><div className="grid gap-3 sm:grid-cols-2 xl:grid-cols-4">{objectives.items?.map((item) => { const ObjectiveIcon = objectiveIcons[item.icon] ?? Star; return <article key={item.label} className="grid place-items-center gap-2 text-center"><span className={`grid size-14 place-items-center rounded-[18px] ${objectiveTones[item.tone] ?? objectiveTones.violet}`}><ObjectiveIcon className="size-7" strokeWidth={3} /></span><p className="text-xs font-black leading-5 text-nt-text-primary">{item.label}</p></article>; })}</div></section>}

      {uses && <section><h2 className="flex items-center gap-2 text-xl font-black text-emerald-700"><Star className="size-5 fill-emerald-500 text-emerald-500" />{uses.title}</h2><div className="mt-2 grid gap-3 sm:grid-cols-2 xl:grid-cols-4">{uses.items?.map((item) => <article key={item.title} className={`grid min-h-48 grid-cols-[100px_minmax(0,1fr)] items-center gap-3 rounded-[22px] border p-3 shadow-sm ${useTones[item.tone] ?? useTones.blue}`}><img src={assetUrl(item.image)} alt={item.title} className="h-28 w-full object-contain drop-shadow-md" /><div><h3 className="text-sm font-black">{item.title}</h3><p className="mt-2 text-xs font-semibold leading-5 text-slate-700">{item.text}</p></div></article>)}</div></section>}

      {reminder && <section className="grid items-center gap-4 overflow-hidden rounded-[22px] border border-violet-100 bg-gradient-to-r from-violet-50 via-white to-sky-50 p-4 shadow-sm lg:grid-cols-[minmax(190px,1fr)_minmax(300px,1.5fr)_110px_minmax(160px,.8fr)]"><div><h2 className="flex items-center gap-2 text-lg font-black text-nt-purple"><Lightbulb className="size-5" />{reminder.title}</h2><p className="mt-2 text-xs font-semibold leading-5 text-slate-700">{reminder.text}</p></div><div className="grid grid-cols-[minmax(0,1fr)_56px_minmax(0,1fr)] items-center gap-3 rounded-[18px] border border-violet-100 bg-white/70 p-3"><div className="text-right"><p className="font-black text-orange-600">{reminder.numeratorLabel} →</p><p className="mt-1 text-[11px] font-semibold leading-4 text-slate-700">{reminder.numeratorText}</p></div><StackedFraction numerator={reminder.fraction?.numerator} denominator={reminder.fraction?.denominator} className="text-2xl text-nt-text-primary" /><div><p className="font-black text-emerald-700">← {reminder.denominatorLabel}</p><p className="mt-1 text-[11px] font-semibold leading-4 text-slate-700">{reminder.denominatorText}</p></div></div>{reminder.image && <img src={assetUrl(reminder.image)} alt="NEO observando" className="mx-auto h-28 w-full object-contain drop-shadow-md" />}<aside className="rounded-[18px] border border-violet-200 bg-white/80 p-3 shadow-sm"><h3 className="text-sm font-black text-nt-purple">{reminder.reminderTitle}</h3><p className="mt-1 text-xs font-semibold leading-5 text-slate-700">{reminder.reminderText}</p></aside></section>}
    </div>
  );
}

function LessonSidebar({ lesson, lessons, position, progress, neoTip, nextLessonData, showNeoCard = true }) {
  const totalLessons = progress?.total_lessons ?? lessons.length;
  const completedLessons = progress?.completed_lessons ?? progress?.theory_completed_lessons ?? null;
  const theoryPercentage = validPercentage(progress?.theory_progress_percentage);

  return (
    <div className="space-y-5">
      {theoryPercentage !== null && <section className="rounded-[26px] border border-white bg-white p-4 shadow-[0_14px_34px_rgba(30,58,138,0.11)]">
        <img src="/assets/teoria.png" alt="" className="mx-auto h-28 w-full object-contain drop-shadow-[0_12px_18px_rgba(30,58,138,0.16)]" />
        <h2 className="mt-1 text-center text-lg font-black text-nt-blue">Tu progreso en teoría</h2>
        <div className="mt-4 grid grid-cols-[58px_minmax(0,1fr)] items-center gap-3">
          <div
            className="grid size-[58px] place-items-center rounded-full"
            style={{ background: theoryPercentage === null ? "#e2e8f0" : `conic-gradient(#7c3aed ${theoryPercentage * 3.6}deg, #e2e8f0 0deg)` }}
          >
            <div className="grid size-11 place-items-center rounded-full bg-white text-xs font-black text-nt-purple">
              {theoryPercentage === null ? "—" : `${theoryPercentage}%`}
            </div>
          </div>
          <div className="min-w-0">
            <p className="text-xs font-semibold text-nt-text-secondary">
              {completedLessons === null ? `— / ${totalLessons}` : `${completedLessons} / ${totalLessons}`} lecciones completadas
            </p>
            <div className="mt-2 h-2 overflow-hidden rounded-full bg-slate-200">
              <div className="h-full rounded-full bg-gradient-to-r from-nt-blue to-nt-green" style={{ width: `${theoryPercentage ?? 0}%` }} />
            </div>
          </div>
        </div>
      </section>}

      <section className="rounded-[28px] border border-white bg-white p-5 shadow-[0_14px_34px_rgba(76,29,149,0.11)]">
        <p className="text-sm font-black text-nt-text-primary">Lección actual</p>
        <div className="mt-3 flex items-center gap-3">
          <span className="grid size-14 shrink-0 place-items-center rounded-full bg-gradient-to-br from-nt-blue to-nt-purple text-xl font-black text-white shadow-lg shadow-violet-200">
            {position}
          </span>
          <h2 className="text-base font-black leading-5 text-nt-text-primary">{lesson.title}</h2>
        </div>
      </section>

      {showNeoCard && <aside className="relative min-h-[156px] overflow-hidden rounded-3xl border border-violet-200 bg-gradient-to-br from-white via-violet-50 to-sky-100 p-4 shadow-[0_14px_34px_rgba(99,102,241,0.14)]">
          <span className="absolute left-4 top-3 text-sm text-violet-300/80" aria-hidden="true">✦</span>
          <span className="absolute right-5 top-4 text-xs text-amber-300/80" aria-hidden="true">✦</span>
          <div className="relative z-10 max-w-[58%] pt-3">
            <h2 className="text-lg font-black text-nt-text-primary">{neoTip?.title ?? "NEO dice"}</h2>
            <p className="mt-2 line-clamp-4 text-xs font-semibold leading-5 text-slate-700">{neoTip?.text ?? "Cuando repartes una pizza, una barra de chocolate o una torta en partes iguales, ya estás usando fracciones."}</p>
          </div>
          <div className="absolute -bottom-10 -right-8 size-32 rounded-full bg-white/50 blur-2xl" />
          <img src={assetUrl(neoTip?.image) ?? "/assets/neo_leccion.png"} alt="NEO" className="absolute bottom-0 right-1 z-10 size-38 object-contain drop-shadow-[0_12px_18px_rgba(76,29,149,0.18)]" />
      </aside>}
      {nextLessonData && (
        <aside className="relative overflow-hidden rounded-3xl border border-sky-100 bg-gradient-to-br from-white to-sky-50 p-4 shadow-sm">
          <div className="max-w-[68%]"><p className="text-xs font-black text-nt-blue">Próxima lección</p><h2 className="mt-1 text-sm font-black text-nt-text-primary">{nextLessonData.title}</h2><p className="mt-2 text-xs font-semibold leading-5 text-nt-text-secondary">{nextLessonData.description}</p></div>
          <img src="/assets/libro.png" alt="" className="absolute bottom-1 right-1 size-20 object-contain" />
        </aside>
      )}
    </div>
  );
}

function TheoryLesson() {
  const navigate = useNavigate();
  const location = useLocation();
  const { moduleId, levelId, lessonId } = useParams();
  const [lesson, setLesson] = useState(null);
  const [lessons, setLessons] = useState([]);
  const [moduleProgress, setModuleProgress] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [isCompleting, setIsCompleting] = useState(false);
  const [error, setError] = useState("");
  const [completionError, setCompletionError] = useState("");

  const module = location.state?.module ?? { id: moduleId, title: "Módulo" };
  const level = location.state?.level ?? { id: levelId, name: "Nivel", backendTitle: "Contenido del nivel" };
  const studentId = getStudentId();

  useEffect(() => {
    let active = true;
    setIsLoading(true);
    setError("");

    Promise.all([getTheoryLesson(lessonId), getTheoryLessons(levelId)])
      .then(([lessonData, lessonList]) => {
        if (!active) return;
        setLesson(lessonData);
        setLessons(Array.isArray(lessonList) ? lessonList : []);
      })
      .catch(() => {
        if (active) setError("No pudimos cargar esta lección desde el servidor.");
      })
      .finally(() => {
        if (active) setIsLoading(false);
      });

    return () => {
      active = false;
    };
  }, [lessonId, levelId]);

  useEffect(() => {
    if (!studentId) return undefined;
    let active = true;
    getModuleProgress(studentId, levelId)
      .then((progressData) => {
        if (active) setModuleProgress(progressData);
      })
      .catch(() => {
        if (active) setModuleProgress(null);
      });
    return () => {
      active = false;
    };
  }, [studentId, levelId]);

  const lessonIndex = useMemo(
    () => lessons.findIndex((item) => String(item.id) === String(lessonId)),
    [lessons, lessonId]
  );
  const previousLesson = lessonIndex > 0 ? lessons[lessonIndex - 1] : null;
  const nextLesson = lessonIndex >= 0 ? lessons[lessonIndex + 1] : null;
  const position = lessonIndex >= 0 ? lessonIndex + 1 : 0;
  const routeProgress = lessons.length ? Math.round((position / lessons.length) * 100) : 0;
  const normalizedLessonTitle = String(lesson?.title ?? "")
    .normalize("NFD")
    .replace(/[\u0300-\u036f]/g, "")
    .toLowerCase();
  const isWelcomeLesson = position === 1
    || normalizedLessonTitle.includes("bienvenida al mundo de las fracciones")
    || normalizedLessonTitle.includes("bienvenido al mundo de las fracciones");
  // Objectives are parsed from backend-provided content_html. The initial database
  // content is hardcoded by backend-spring-boot/.../config/TheoryLessonSeeder.java.
  const parsedLessonContent = useMemo(
    () => parseLessonContent(lesson?.content_html, { hideTip: isWelcomeLesson }),
    [lesson?.content_html, isWelcomeLesson]
  );
  const webContent = useMemo(() => {
    const value = lesson?.web_content_json ?? lesson?.webContent;
    if (!value) return null;
    if (typeof value === "object") return value;
    try {
      return JSON.parse(value);
    } catch (parseError) {
      console.warn("[TheoryLesson] web_content_json inválido", {
        lessonId: lesson?.id,
        webContentJson: value,
        parseError,
      });
      return null;
    }
  }, [lesson?.id, lesson?.web_content_json, lesson?.webContent]);
  useEffect(() => {
    if (!lesson) return;
    const rawWebContent = lesson.web_content_json ?? lesson.webContent ?? null;
    console.info("[TheoryLesson] diagnóstico de contenido", {
      lessonId: lesson.id,
      webContentJson: rawWebContent,
      parsedWebContent: webContent,
      usingContentHtmlFallback: !webContent && Boolean(parsedLessonContent.contentHtml.trim()),
    });
  }, [lesson, parsedLessonContent.contentHtml, webContent]);
  const webSections = Array.isArray(webContent?.sections) ? webContent.sections : [];
  const learningObjectivesSection = webSections.find((section) => section.type === "learning_objectives");
  const learningObjectives = Array.isArray(learningObjectivesSection?.items)
    ? learningObjectivesSection.items.filter(Boolean)
    : parsedLessonContent.learningObjectives;
  const neoTip = webSections.find((section) => section.type === "neo_tip") ?? null;
  const contentSections = webSections.filter((section) => !["learning_objectives", "neo_tip"].includes(section.type));
  const importantSection = contentSections.find((section) => section.type === "important_idea");
  const exampleSection = contentSections.find((section) => section.type === "example");
  const mainConceptSection = contentSections.find((section) => section.type === "main_concept");
  const commonMistakesSection = contentSections.find((section) => section.type === "common_mistakes");
  const reflectionSection = contentSections.find((section) => section.type === "reflection");
  const observeSection = contentSections.find((section) => section.type === "observe");
  const summarySection = contentSections.find((section) => section.type === "summary");
  const isLessonTwo = position === 2 && webSections.some((section) => section.type === "lesson_objective");
  const isLessonThree = position === 3 && webSections.some((section) => section.type === "fraction_parts");
  const isLessonFour = position === 4 && webSections.some((section) => section.type === "proper_fractions");
  const isLessonFive = position === 5 && webSections.some((section) => section.type === "learning_summary");
  const isIntermediateWelcome = webContent?.layout === "intermediate_welcome";
  const isIntermediateEquivalent = webContent?.layout === "intermediate_equivalent_fractions";
  const isIntermediateSameDenominator = webContent?.layout === "intermediate_same_denominator";
  const isIntermediateDifferentDenominator = webContent?.layout === "intermediate_different_denominator";
  const isIntermediateFractionSubtraction = webContent?.layout === "intermediate_fraction_subtraction";
  const featuredTypes = new Set(["important_idea", "example", "observe", "summary"]);
  const lessonTwoTypes = new Set(["main_concept", "common_mistakes", "reflection"]);
  const remainingSections = contentSections.filter((section) =>
    !featuredTypes.has(section.type) && !(isLessonTwo && lessonTwoTypes.has(section.type))
  );
  const heroContent = webContent?.hero ?? null;

  const sidebarItems = [
    { label: "Inicio", onClick: () => navigate("/student-dashboard") },
    { label: "Módulos", active: true },
    { label: "Mis Logros", onClick: () => navigate("/achievements") },
    { label: "Perfil", onClick: () => navigate("/profile") },
  ];

  const lessonPath = (targetLesson) =>
    `/module/${moduleId}/level/${levelId}/theory/lesson/${targetLesson.id}`;

  const goToLesson = (targetLesson) => {
    navigate(lessonPath(targetLesson), { state: { module, level } });
  };

  const completeTheory = async () => {
    if (!studentId) {
      setCompletionError("No pudimos identificar al estudiante. Inicia sesión nuevamente.");
      return;
    }

    setIsCompleting(true);
    setCompletionError("");
    try {
      await markTheoryCompleted(studentId, levelId);
      navigate(`/module/${moduleId}/level/${levelId}/practice`, { state: { module, level } });
    } catch {
      setCompletionError("No pudimos guardar la teoría como completada. Revisa tu conexión e intenta nuevamente.");
    } finally {
      setIsCompleting(false);
    }
  };

  if (isLoading) {
    return (
      <StudentLayout sidebar={<AppSidebar items={sidebarItems} />}>
        <div className="grid min-h-[500px] place-items-center">
          <div className="h-12 w-12 animate-spin rounded-full border-4 border-nt-blue border-t-transparent" />
        </div>
      </StudentLayout>
    );
  }

  if (error || !lesson) {
    return (
      <StudentLayout sidebar={<AppSidebar items={sidebarItems} />}>
        <section className="rounded-nt-card border border-amber-200 bg-amber-50 p-8 text-center shadow-nt-card">
          <h1 className="text-2xl font-black text-amber-900">Lección no disponible</h1>
          <p className="mt-2 text-sm font-semibold text-amber-800">{error}</p>
          <Button
            className="mt-5 rounded-[18px] bg-nt-blue font-black text-white"
            onClick={() => navigate(`/module/${moduleId}/level/${levelId}/theory`, { state: { module, level } })}
          >
            Volver a lecciones
          </Button>
        </section>
      </StudentLayout>
    );
  }

  return (
    <StudentLayout
      sidebar={<AppSidebar items={sidebarItems} />}
      topbar={
        <div className="rounded-[28px] border border-white/80 bg-white/85 p-3 shadow-sm backdrop-blur">
          <div className="flex items-center justify-between gap-3">
            <BackButton onClick={() => navigate(`/module/${moduleId}/level/${levelId}/theory`, { state: { module, level } })}>
              Lecciones
            </BackButton>
            <h1 className="hidden text-lg font-black text-nt-text-primary sm:block">Teoría - {level.name}</h1>
            <span className="rounded-full bg-nt-blue px-4 py-2 text-sm font-black text-white shadow-lg shadow-nt-blue/20">
              {position}/{lessons.length}
            </span>
          </div>
          <div className="mt-3 flex items-center gap-3">
            <div className="h-3 flex-1 overflow-hidden rounded-full bg-white shadow-inner">
              <div className="h-full rounded-full bg-gradient-to-r from-nt-green via-nt-blue to-nt-purple" style={{ width: `${routeProgress}%` }} />
            </div>
            <span className="text-sm font-black text-nt-blue">{routeProgress}%</span>
          </div>
        </div>
      }
      rightPanel={
        <LessonSidebar lesson={lesson} lessons={lessons} position={position} progress={moduleProgress} neoTip={neoTip} nextLessonData={webContent?.nextLesson} showNeoCard={!webContent} />
      }
    >
      <article className="rounded-nt-card border border-white/85 bg-white/95 p-3 shadow-nt-card sm:p-4 lg:p-5">
        <section className="relative overflow-hidden rounded-[24px] border border-blue-100 bg-gradient-to-br from-white via-blue-50 to-sky-50 px-4 py-4 shadow-[0_10px_26px_rgba(59,130,246,0.10)] sm:px-5">
          <div className="pointer-events-none absolute -right-16 -top-16 size-56 rounded-full bg-sky-200/25 blur-3xl" />
          <div className={`relative grid items-center gap-4 md:gap-5 ${isLessonThree || isLessonFour ? "md:grid-cols-[minmax(0,42%)_minmax(360px,58%)]" : "md:grid-cols-[minmax(0,1fr)_minmax(240px,42%)]"}`}>
            <div className="min-w-0">
              <span className="inline-flex rounded-full bg-violet-100 px-3 py-1 text-xs font-black text-violet-700">
                {heroContent?.badge ?? `Lección ${position}`}
              </span>
              <h1 className="mt-2.5 text-2xl font-black leading-tight text-nt-text-primary sm:text-3xl">
                {heroContent?.title ?? lesson.title}
              </h1>
              {(heroContent?.subtitle ?? lesson.subtitle) && <p className="mt-1.5 text-sm font-black text-nt-purple sm:text-base">{heroContent?.subtitle ?? lesson.subtitle}</p>}
              {(heroContent?.description ?? lesson.summary) && (
                <p className="mt-2 max-w-2xl whitespace-pre-line text-sm font-semibold leading-5 text-slate-600">{heroContent?.description ?? lesson.summary}</p>
              )}
            </div>
            <div className="order-first flex min-h-[150px] items-center justify-center md:order-none md:min-h-[180px]">
              {isLessonFour && Array.isArray(heroContent?.comparisons) ? (
                <div className="grid w-full grid-cols-[minmax(120px,1fr)_130px_minmax(120px,1fr)] items-center gap-3">
                  {heroContent.comparisons.slice(0, 1).map((item) => <article key={item.title} className="rounded-[18px] border border-emerald-200 bg-white/90 p-3 text-center shadow-sm"><h2 className="font-black text-emerald-700">{item.title}</h2><div className="mx-auto mt-2 w-10"><StackedFraction numerator={item.numerator} denominator={item.denominator} className="text-2xl text-nt-text-primary" /></div><p className="mt-2 text-[11px] font-semibold leading-4 text-slate-700">{item.text}</p></article>)}
                  <img src={assetUrl(heroContent.image)} alt="NEO comparando fracciones" className="h-40 w-full object-contain drop-shadow-md" />
                  {heroContent.comparisons.slice(1, 2).map((item) => <article key={item.title} className="rounded-[18px] border border-orange-200 bg-white/90 p-3 text-center shadow-sm"><h2 className="font-black text-orange-600">{item.title}</h2><div className="mx-auto mt-2 w-10"><StackedFraction numerator={item.numerator} denominator={item.denominator} className="text-2xl text-nt-text-primary" /></div><p className="mt-2 text-[11px] font-semibold leading-4 text-slate-700">{item.text}</p></article>)}
                </div>
              ) : isLessonThree && heroContent?.fraction ? (
                <div className="grid w-full grid-cols-[72px_minmax(0,1fr)_130px] items-center gap-3">
                  <StackedFraction numerator={heroContent.fraction.numerator} denominator={heroContent.fraction.denominator} className="text-4xl text-nt-text-primary" />
                  <div className="grid gap-4"><div><p className="font-black text-orange-600">→ {heroContent.numeratorLabel}</p><p className="mt-1 text-xs font-semibold leading-5 text-slate-700">{heroContent.numeratorText}</p></div><div><p className="font-black text-emerald-700">→ {heroContent.denominatorLabel}</p><p className="mt-1 text-xs font-semibold leading-5 text-slate-700">{heroContent.denominatorText}</p></div></div>
                  <img src={assetUrl(heroContent.image)} alt="NEO indicando las partes de una fracción" className="h-44 w-full object-contain drop-shadow-[0_16px_22px_rgba(30,58,138,0.18)]" />
                </div>
              ) : (
                <img
                  src={assetUrl(heroContent?.image) ?? (isLessonTwo ? "/assets/lecciones_basico2.png" : "/assets/lecciones_saludo.png")}
                  alt=""
                  className="max-h-[205px] w-full object-contain drop-shadow-[0_16px_22px_rgba(30,58,138,0.18)]"
                />
              )}
            </div>
          </div>
        </section>

        {isLessonTwo && (
          <LessonTwoContent
            sections={webSections}
            onNext={nextLesson ? () => goToLesson(nextLesson) : null}
          />
        )}

        {isLessonThree && <LessonThreeContent sections={webSections} />}

        {isLessonFour && <LessonFourContent sections={webSections} />}

        {isLessonFive && <LessonFiveContent sections={webSections} />}

        {isIntermediateWelcome && <IntermediateWelcomeContent sections={webSections} />}

        {isIntermediateEquivalent && <EquivalentFractionsContent sections={webSections} />}

        {isIntermediateSameDenominator && <SameDenominatorContent sections={webSections} />}

        {isIntermediateDifferentDenominator && <DifferentDenominatorContent sections={webSections} />}

        {isIntermediateFractionSubtraction && <FractionSubtractionContent sections={webSections} />}

        {!isLessonTwo && !isLessonThree && !isLessonFour && !isLessonFive && !isIntermediateWelcome && !isIntermediateEquivalent && !isIntermediateSameDenominator && !isIntermediateDifferentDenominator && !isIntermediateFractionSubtraction && (learningObjectives.length > 0 || importantSection || neoTip) && (
          <section className="mt-4 grid items-stretch gap-3 md:grid-cols-2 lg:grid-cols-[35fr_32fr_33fr]">
            {learningObjectives.length > 0 && (
              <div className="h-full rounded-[22px] border border-sky-100 bg-sky-50/60 p-3 shadow-sm">
                <div className="mb-2.5 flex items-center gap-2">
                  <BookOpen className="size-5 shrink-0 text-emerald-700" aria-hidden="true" />
                  <h2 className="text-lg font-black text-nt-text-primary">{learningObjectivesSection?.title ?? "Hoy aprenderás"}</h2>
                </div>
                <div className="grid gap-1.5">
                  {learningObjectives.map((objective) => (
                    <article key={objective} className="flex items-center gap-2 rounded-xl border border-emerald-100 bg-emerald-50 px-3 py-2 shadow-sm">
                      <span className="grid size-5 shrink-0 place-items-center rounded-full bg-emerald-500 text-white">
                        <Check className="size-3" strokeWidth={3} aria-hidden="true" />
                      </span>
                      <p className="text-xs font-bold leading-4 text-slate-700">{objective}</p>
                    </article>
                  ))}
                </div>
              </div>
            )}
            {importantSection && (
              <aside className="relative h-full min-h-[190px] overflow-hidden rounded-[22px] border border-amber-200 bg-amber-50 p-4 shadow-sm">
                <div className="relative z-10 max-w-[58%]">
                  <h2 className="flex items-center gap-2 text-sm font-black uppercase text-amber-800"><Lightbulb className="size-4 text-amber-500" />{importantSection.title}</h2>
                  <p className="mt-3 text-sm font-semibold leading-5 text-amber-950">{importantSection.text}</p>
                </div>
                <span className="absolute right-5 top-4 text-amber-400" aria-hidden="true">✦</span>
                <img src="/assets/idea.png" alt="" className="absolute bottom-1 right-1 h-[82%] w-[44%] object-contain drop-shadow-md" />
              </aside>
            )}
            {neoTip && (
              <aside className="grid h-full min-h-[190px] items-center gap-3 overflow-hidden rounded-[22px] border border-violet-100 bg-gradient-to-br from-violet-50 to-sky-50 p-4 shadow-sm sm:grid-cols-[minmax(0,58%)_minmax(120px,42%)]">
                <div className="relative z-10 min-w-0">
                  <h2 className="text-base font-black text-nt-purple">{neoTip.title}</h2>
                  <div className="mt-3 rounded-[16px] border border-white bg-white/80 px-3 py-2 shadow-sm">
                    <p className="text-xs font-semibold leading-5 text-slate-700">{neoTip.text}</p>
                  </div>
                </div>
                <img src="/assets/neo_pensando.png" alt="NEO pensando" className="mx-auto h-40 w-full self-end object-contain drop-shadow-md sm:h-44" />
              </aside>
            )}
          </section>
        )}

        {!isLessonTwo && !isLessonThree && !isLessonFour && !isLessonFive && !isIntermediateWelcome && !isIntermediateEquivalent && !isIntermediateSameDenominator && !isIntermediateDifferentDenominator && !isIntermediateFractionSubtraction && webContent && contentSections.length > 0 && (
          <div className="mt-4 grid gap-3">
            {isLessonTwo && (exampleSection || mainConceptSection || commonMistakesSection) && (
              <div className="grid items-stretch gap-3 lg:grid-cols-[minmax(0,2fr)_minmax(220px,1fr)]">
                <div className="grid gap-3 md:grid-cols-2 lg:grid-cols-1 xl:grid-cols-2">
                  {exampleSection && <StructuredSection section={exampleSection} />}
                  {mainConceptSection && <StructuredSection section={mainConceptSection} />}
                </div>
                {commonMistakesSection && <StructuredSection section={commonMistakesSection} />}
              </div>
            )}
            {isLessonTwo && reflectionSection && <StructuredSection section={reflectionSection} />}
            {!isLessonTwo && (exampleSection || observeSection) && (
              <div className="grid gap-3 lg:grid-cols-2">
                {exampleSection && <StructuredSection section={exampleSection} />}
                {observeSection && <StructuredSection section={observeSection} />}
              </div>
            )}
            {summarySection && <StructuredSection section={summarySection} />}
            {remainingSections.length > 0 && (
              <div className="grid gap-3 md:grid-cols-2">
                {remainingSections.map((section, index) => <StructuredSection key={`${section.type}-${index}`} section={section} />)}
              </div>
            )}
          </div>
        )}

        {!webContent && !isWelcomeLesson && parsedLessonContent.contentHtml.trim() && (
        <div
          className="mt-5 grid gap-4 text-base font-semibold leading-7 text-slate-700
            [&_h2]:text-2xl [&_h2]:font-black [&_h2]:text-nt-text-primary
            [&_h3]:text-lg [&_h3]:font-black [&_h3]:text-nt-blue
            [&_.lesson-lead]:rounded-[26px] [&_.lesson-lead]:bg-gradient-to-br [&_.lesson-lead]:from-nt-sky/80 [&_.lesson-lead]:to-violet-50 [&_.lesson-lead]:p-5
            [&_.lesson-list]:grid [&_.lesson-list]:gap-2 [&_.lesson-list]:pl-5
            [&_.lesson-list_li]:list-disc
            [&_.lesson-steps]:grid [&_.lesson-steps]:gap-3 [&_.lesson-steps]:pl-6 [&_.lesson-steps_li]:list-decimal
            [&_.math-example]:grid [&_.math-example]:gap-2 [&_.math-example]:rounded-[26px] [&_.math-example]:border [&_.math-example]:border-violet-100 [&_.math-example]:bg-violet-50 [&_.math-example]:p-5 [&_.math-example]:text-center
            [&_.math-example_strong]:text-2xl [&_.math-example_strong]:font-black [&_.math-example_strong]:text-nt-purple
            [&_.math-example_span]:text-sm [&_.math-example_span]:text-nt-text-secondary
            [&_.tip-box]:rounded-[24px] [&_.tip-box]:border [&_.tip-box]:border-amber-200 [&_.tip-box]:bg-amber-50 [&_.tip-box]:p-5 [&_.tip-box]:text-amber-900
            [&_.concept-grid]:grid [&_.concept-grid]:gap-3 [&_.concept-grid]:sm:grid-cols-3
            [&_.concept-grid_div]:grid [&_.concept-grid_div]:gap-1 [&_.concept-grid_div]:rounded-[22px] [&_.concept-grid_div]:bg-nt-sky/65 [&_.concept-grid_div]:p-4 [&_.concept-grid_div]:text-center
            [&_.concept-grid_strong]:text-2xl [&_.concept-grid_strong]:font-black [&_.concept-grid_strong]:text-nt-blue
            [&_.concept-grid_span]:text-sm [&_.concept-grid_span]:text-nt-text-secondary
            [&_.fraction-parts]:grid [&_.fraction-parts]:gap-3 [&_.fraction-parts]:sm:grid-cols-2
            [&_.fraction-parts_div]:grid [&_.fraction-parts_div]:gap-2 [&_.fraction-parts_div]:rounded-[24px] [&_.fraction-parts_div]:bg-nt-sky/70 [&_.fraction-parts_div]:p-5 [&_.fraction-parts_div]:text-center
            [&_.fraction-parts_strong]:text-5xl [&_.fraction-parts_strong]:font-black [&_.fraction-parts_strong]:text-nt-purple
            [&_.problem-box]:rounded-[24px] [&_.problem-box]:bg-green-50 [&_.problem-box]:p-5 [&_.problem-box]:text-green-900"
          dangerouslySetInnerHTML={{ __html: parsedLessonContent.contentHtml }}
        />
        )}
      </article>

      {completionError && (
        <p className="rounded-[20px] border border-amber-200 bg-amber-50 px-4 py-3 text-sm font-bold text-amber-800">
          {completionError}
        </p>
      )}

      <section className="flex flex-col gap-3 rounded-[26px] border border-white/85 bg-white/90 p-4 shadow-sm sm:flex-row sm:items-center sm:justify-between">
        <Button
          type="button"
          variant="ghost"
          disabled={!previousLesson}
          className="h-12 rounded-[18px] px-5 font-black text-nt-text-secondary"
          onClick={() => previousLesson && goToLesson(previousLesson)}
        >
          <ArrowLeft className="size-4" /> Anterior
        </Button>

        {nextLesson ? (
          <Button
            type="button"
            className="h-12 rounded-[18px] bg-gradient-to-r from-nt-blue to-nt-purple px-6 font-black text-white"
            onClick={() => goToLesson(nextLesson)}
          >
            Siguiente <ArrowRight className="size-4" />
          </Button>
        ) : (
          <Button
            type="button"
            disabled={isCompleting}
            className="h-12 rounded-[18px] bg-gradient-to-r from-nt-green to-emerald-500 px-6 font-black text-white"
            onClick={completeTheory}
          >
            {isCompleting ? "Guardando progreso..." : "Finalizar teoría e ir a práctica"}
            <CheckCircle2 className="size-4" />
          </Button>
        )}
      </section>
    </StudentLayout>
  );
}

export default TheoryLesson;
