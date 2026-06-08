import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import DiagnosticExam from "./pages/DiagnosticExam";
import DiagnosticResult from "./pages/DiagnosticResult";
import DiagnosticReview from "./pages/DiagnosticReview";
import StudentDashboard from "./pages/StudentDashboard";
import ProtectedRoute from "./components/ProtectedRoute";

function LoginMock() {
  const login = () => {
    localStorage.setItem("token", "token-demo");
    localStorage.setItem(
      "user",
      JSON.stringify({
        name: "Estudiante Demo",
        grade: "6to grado",
        section: "A",
      })
    );
    window.location.href = "/student-dashboard";
  };

  return (
    <div style={{ padding: "40px" }}>
      <h1>Login temporal</h1>
      <p>Este login es solo para probar Epica 2 sin backend.</p>
      <button onClick={login}>Ingresar como estudiante</button>
    </div>
  );
}

function App() {
  const token = localStorage.getItem("token");

  return (
    <BrowserRouter>
      <Routes>
        <Route
          path="/"
          element={
            token ? (
              <Navigate to="/student-dashboard" replace />
            ) : (
              <Navigate to="/login" replace />
            )
          }
        />

        <Route path="/login" element={<LoginMock />} />

        <Route path="/diagnostic-exam" element={<DiagnosticExam />} />
        <Route path="/diagnostic-result" element={<DiagnosticResult />} />
        <Route path="/diagnostic-review" element={<DiagnosticReview />} />

        <Route
          path="/student-dashboard"
          element={
            <ProtectedRoute>
              <StudentDashboard />
            </ProtectedRoute>
          }
        />
      </Routes>
    </BrowserRouter>
  );
}

export default App;