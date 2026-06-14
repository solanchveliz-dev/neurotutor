import { cn } from "@/lib/utils";

function StudentLayout({
  sidebar,
  topbar,
  children,
  rightPanel,
  className,
  contentClassName,
}) {
  return (
    <div
      className={cn(
        "min-h-screen bg-[linear-gradient(180deg,var(--nt-sky)_0%,var(--nt-sky-deep)_100%)] p-4 text-nt-text-primary sm:p-6 lg:p-8",
        className
      )}
    >
      <div className="mx-auto grid w-full max-w-7xl gap-5 lg:grid-cols-[240px_minmax(0,1fr)] xl:grid-cols-[252px_minmax(0,1fr)_300px]">
        {sidebar && <aside className="lg:sticky lg:top-8 lg:self-start">{sidebar}</aside>}

        <main className={cn("min-w-0 space-y-5", contentClassName)}>
          {topbar}
          {children}
        </main>

        {rightPanel && (
          <aside className="min-w-0 space-y-5 lg:col-start-2 xl:col-start-auto xl:sticky xl:top-8 xl:self-start">
            {rightPanel}
          </aside>
        )}
      </div>
    </div>
  );
}

export default StudentLayout;
