import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AppProvider } from './context/AppContext';
import LandingPage from './components/LandingPage';
import Auth from './components/Auth';
import DashboardLayout from './dashboard/DashboardLayout';
import DashboardHome from './dashboard/pages/DashboardHome';
import EmployeeDirectory from './dashboard/pages/EmployeeDirectory';
import TimeOffManager from './dashboard/pages/TimeOffManager';
import SkillMatrix from './dashboard/pages/SkillMatrix';
import DocumentVault from './dashboard/pages/DocumentVault';

function App() {
  return (
    <AppProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<LandingPage />} />
          <Route path="/auth" element={<Auth />} />
          <Route path="/dashboard" element={<DashboardLayout />}>
            <Route index element={<DashboardHome />} />
            <Route path="employees" element={<EmployeeDirectory />} />
            <Route path="time-off" element={<TimeOffManager />} />
            <Route path="skills" element={<SkillMatrix />} />
            <Route path="documents" element={<DocumentVault />} />
          </Route>
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </BrowserRouter>
    </AppProvider>
  );
}

export default App;
