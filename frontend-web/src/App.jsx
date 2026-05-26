import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import DiagnosticExam from "./pages/DiagnosticExam";
import DiagnosticResult from "./pages/DiagnosticResult";
import DiagnosticReview from "./pages/DiagnosticReview";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Navigate to="/diagnostic-exam" />} />
        <Route path="/diagnostic-exam" element={<DiagnosticExam />} />
        <Route path="/diagnostic-result" element={<DiagnosticResult />} />
        <Route path="/diagnostic-review" element={<DiagnosticReview />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;