import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import Login from "./pages/Login";
import Register from "./pages/Register";
import DiagnosticExam from "./pages/DiagnosticExam";
import DiagnosticResult from "./pages/DiagnosticResult";
import DiagnosticReview from "./pages/DiagnosticReview";
import StudentDashboard from "./pages/StudentDashboard";
import ProtectedRoute from "./components/ProtectedRoute";
import LearningPath from "./pages/LearningPath";
import ModuleDetail from "./pages/ModuleDetail";
import PracticeExercises from "./pages/PracticeExercises";
import FinalExam from "./pages/FinalExam";

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

        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />

        <Route path="/diagnostic-exam" element={<DiagnosticExam />} />
        <Route path="/diagnostic-result" element={<DiagnosticResult />} />
        <Route path="/diagnostic-review" element={<DiagnosticReview />} />

        <Route path="/dashboard" element={<Navigate to="/student-dashboard" replace />} />

        <Route
          path="/student-dashboard"
          element={
            <ProtectedRoute>
              <StudentDashboard />
            </ProtectedRoute>
          }
        />

        <Route
          path="/learning-path"
          element={
            <ProtectedRoute>
              <LearningPath />
            </ProtectedRoute>
          }
        />

        <Route
          path="/module/:moduleId/:levelId"
          element={
            <ProtectedRoute>
              <ModuleDetail />
            </ProtectedRoute>
          }
        />

        <Route
          path="/practice/:moduleId/:levelId"
          element={
            <ProtectedRoute>
              <PracticeExercises />
            </ProtectedRoute>
          }
        />

        <Route
          path="/final-exam/:moduleId/:levelId"
          element={
            <ProtectedRoute>
              <FinalExam />
            </ProtectedRoute>
          }
        />
      </Routes>
    </BrowserRouter>
  );
}

export default App;