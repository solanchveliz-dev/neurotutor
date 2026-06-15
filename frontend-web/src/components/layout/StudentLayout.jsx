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
        "min-h-screen w-full bg-[linear-gradient(180deg,rgba(223,244,255,0.3)_0%,rgba(191,231,255,0.18)_100%),url('/assets/hero-bg.png')] bg-cover bg-center bg-fixed px-4 py-4 text-nt-text-primary sm:px-6 sm:py-6 lg:px-8 lg:py-8",
        className
      )}
    >
      <div className="grid w-full grid-cols-1 gap-5 lg:grid-cols-[260px_minmax(0,1fr)] 2xl:grid-cols-[260px_minmax(0,1fr)_320px]">
        {sidebar && <aside className="lg:sticky lg:top-8 lg:self-start">{sidebar}</aside>}

        <main className={cn("min-w-0 w-full space-y-5", contentClassName)}>
          {topbar}
          {children}
        </main>

        {rightPanel && (
          <aside className="min-w-0 space-y-5 lg:col-start-2 2xl:col-start-auto 2xl:sticky 2xl:top-8 2xl:self-start">
            {rightPanel}
          </aside>
        )}
      </div>
    </div>
  );
}

export default StudentLayout;
