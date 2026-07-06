import { useEffect, useState } from "react";
import { Navigate, useLocation } from "react-router-dom";

import { getCurrentAdmin } from "../services/adminService";

function AdminRoute({ children }) {
  const location = useLocation();
  const [status, setStatus] = useState("checking");

  useEffect(() => {
    let active = true;

    getCurrentAdmin()
      .then((admin) => {
        if (!active) return;
        setStatus(admin?.is_staff || admin?.is_superuser ? "authenticated" : "anonymous");
      })
      .catch(() => active && setStatus("anonymous"));

    return () => {
      active = false;
    };
  }, []);

  if (status === "checking") {
    return <div className="grid min-h-screen place-items-center bg-[#F4F8FF] font-semibold text-[#52617C]">Validando sesion administrativa...</div>;
  }

  if (status === "anonymous") {
    return <Navigate to="/login" replace state={{ from: location.pathname, adminRequired: true }} />;
  }

  return children;
}

export default AdminRoute;
