import { Badge } from "@/components/ui/badge";

const labels = {
  active: "Activo",
  inactive: "Inactivo",
};

function AdminStatusBadge({ status }) {
  const isActive = status === "active";

  return (
    <Badge
      className={`h-6 rounded-full border-0 px-2.5 text-[11px] font-semibold shadow-none ${
        isActive
          ? "bg-[#DFF4FF] text-[#2563FF] hover:bg-[#DFF4FF]"
          : "bg-[#EEF3FB] text-[#52617C] hover:bg-[#EEF3FB]"
      }`}
    >
      <span className={`mr-1.5 size-1.5 rounded-full ${isActive ? "bg-[#2563FF]" : "bg-[#95A3BA]"}`} />
      {labels[status] || status || "Sin estado"}
    </Badge>
  );
}

export default AdminStatusBadge;
