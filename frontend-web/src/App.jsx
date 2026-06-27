import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import Login from "./pages/Login";
import Register from "./pages/Register";
import ForgotPassword from "./pages/ForgotPassword";
import ResetPassword from "./pages/ResetPassword";
import DiagnosticExam from "./pages/DiagnosticExam";
import DiagnosticResult from "./pages/DiagnosticResult";
import DiagnosticReview from "./pages/DiagnosticReview";
import StudentDashboard from "./pages/StudentDashboard";
import ProtectedRoute from "./components/ProtectedRoute";
import LearningPath from "./pages/LearningPath";
import ModuleDetail from "./pages/ModuleDetail";
import LevelActivities from "./pages/LevelActivities";
import Theory from "./pages/Theory";
import TheoryLesson from "./pages/TheoryLesson";
import PracticeExercises from "./pages/PracticeExercises";
import FinalExam from "./pages/FinalExam";
import Profile from "./pages/Profile";
import AdminDashboard from "./pages/admin/AdminDashboard";
import AdminStudents from "./pages/admin/AdminStudents";
import AdminStudentDetail from "./pages/admin/AdminStudentDetail";

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
        <Route path="/forgot-password" element={<ForgotPassword />} />
        <Route path="/reset-password" element={<ResetPassword />} />

        <Route path="/admin/dashboard" element={<AdminDashboard />} />
        <Route path="/admin/students" element={<AdminStudents />} />
        <Route path="/admin/students/:id" element={<AdminStudentDetail />} />

        <Route
          path="/diagnostic-exam"
          element={
            <ProtectedRoute>
              <DiagnosticExam />
            </ProtectedRoute>
          }
        />
        <Route
          path="/diagnostic-result"
          element={
            <ProtectedRoute>
              <DiagnosticResult />
            </ProtectedRoute>
          }
        />
        <Route
          path="/diagnostic-review"
          element={
            <ProtectedRoute>
              <DiagnosticReview />
            </ProtectedRoute>
          }
        />

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
          path="/module/:moduleId"
          element={
            <ProtectedRoute>
              <ModuleDetail />
            </ProtectedRoute>
          }
        />

        <Route
          path="/profile"
          element={
            <ProtectedRoute>
              <Profile />
            </ProtectedRoute>
          }
        />

        <Route
          path="/module/:moduleId/level/:levelId"
          element={
            <ProtectedRoute>
              <LevelActivities />
            </ProtectedRoute>
          }
        />

        <Route
          path="/module/:moduleId/level/:levelId/theory"
          element={
            <ProtectedRoute>
              <Theory />
            </ProtectedRoute>
          }
        />

        <Route
          path="/module/:moduleId/level/:levelId/theory/lesson/:lessonId"
          element={
            <ProtectedRoute>
              <TheoryLesson />
            </ProtectedRoute>
          }
        />

        <Route
          path="/module/:moduleId/level/:levelId/theory/:lessonId"
          element={
            <ProtectedRoute>
              <TheoryLesson />
            </ProtectedRoute>
          }
        />

        <Route
          path="/module/:moduleId/level/:levelId/practice"
          element={
            <ProtectedRoute>
              <PracticeExercises />
            </ProtectedRoute>
          }
        />

        <Route
          path="/practice/:moduleId"
          element={
            <ProtectedRoute>
              <PracticeExercises />
            </ProtectedRoute>
          }
        />

        <Route
          path="/final-exam/:moduleId"
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
