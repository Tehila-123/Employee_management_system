import React, { createContext, useContext, useState, useEffect } from 'react';
import { CURRENT_USER, EMPLOYEES, LEAVE_REQUESTS, DOCUMENTS, GOALS } from '../data/mockData';

const AppContext = createContext(null);

// ── localStorage helpers ───────────────────────────────────────────────────

function load(key, fallback) {
  try {
    const raw = localStorage.getItem(key);
    return raw ? JSON.parse(raw) : fallback;
  } catch {
    return fallback;
  }
}

function save(key, value) {
  try {
    // Strip blob URLs before persisting — they don't survive a reload
    if (key === 'ems_documents') {
      const clean = value.map(({ blobUrl, ...rest }) => rest);
      localStorage.setItem(key, JSON.stringify(clean));
    } else {
      localStorage.setItem(key, JSON.stringify(value));
    }
  } catch {
    // localStorage quota exceeded — silently ignore
  }
}

// ── User helpers ───────────────────────────────────────────────────────────

function nameFromEmail(email) {
  return email
    .split('@')[0]
    .split(/[._-]/)
    .map(w => w.charAt(0).toUpperCase() + w.slice(1))
    .join(' ');
}

function resolveUser() {
  const email = localStorage.getItem('userEmail');
  if (!email) return CURRENT_USER;
  return { ...CURRENT_USER, email, name: nameFromEmail(email) };
}

// ── Provider ───────────────────────────────────────────────────────────────

export function AppProvider({ children }) {
  const [theme, setTheme]           = useState('dark');
  const [sidebarOpen, setSidebarOpen] = useState(true);
  const [currentUser, setCurrentUser] = useState(resolveUser);

  // Hydrate from localStorage on first load, fall back to mock data
  const [employees, setEmployees]       = useState(() => load('ems_employees',     EMPLOYEES));
  const [leaveRequests, setLeaveRequests] = useState(() => load('ems_leaves',      LEAVE_REQUESTS));
  const [documents, setDocuments]       = useState(() => load('ems_documents',     DOCUMENTS));
  const [goals]                         = useState(() => load('ems_goals',         GOALS));

  // Persist to localStorage whenever state changes
  useEffect(() => { save('ems_employees', employees); },     [employees]);
  useEffect(() => { save('ems_leaves',    leaveRequests); }, [leaveRequests]);
  useEffect(() => { save('ems_documents', documents); },     [documents]);

  // Dark mode
  useEffect(() => {
    document.documentElement.classList.toggle('dark', theme === 'dark');
  }, [theme]);

  const toggleTheme = () => setTheme(t => t === 'dark' ? 'light' : 'dark');

  const loginAs = (email, token) => {
    if (token) localStorage.setItem('token', token);
    localStorage.setItem('userEmail', email);
    setCurrentUser({ ...CURRENT_USER, email, name: nameFromEmail(email) });
  };

  const logout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('userEmail');
    setCurrentUser(CURRENT_USER);
  };

  const addDocument = (doc) =>
    setDocuments(prev => [doc, ...prev]);

  const updateLeaveStatus = (id, status) =>
    setLeaveRequests(prev => prev.map(r => r.id === id ? { ...r, status } : r));

  const addLeaveRequest = (req) =>
    setLeaveRequests(prev => [
      ...prev,
      { ...req, id: String(Date.now()), status: 'pending', submittedAt: new Date().toISOString().split('T')[0] },
    ]);

  return (
    <AppContext.Provider value={{
      theme, toggleTheme,
      sidebarOpen, setSidebarOpen,
      currentUser, setCurrentUser, loginAs, logout,
      employees, setEmployees,
      leaveRequests, updateLeaveStatus, addLeaveRequest,
      documents, addDocument, goals,
    }}>
      {children}
    </AppContext.Provider>
  );
}

export const useApp = () => useContext(AppContext);
