import React from 'react';
import { Outlet, useLocation } from 'react-router-dom';
import Sidebar from './Sidebar';
import Header from './Header';

const TITLES = {
  '/dashboard': 'Dashboard',
  '/dashboard/employees': 'Employee Directory',
  '/dashboard/time-off': 'Time Off Manager',
  '/dashboard/skills': 'Skill Matrix',
  '/dashboard/documents': 'Document Vault',
};

export default function DashboardLayout() {
  const { pathname } = useLocation();
  const title = TITLES[pathname] ?? 'StaffEase';

  return (
    <div className="flex h-full overflow-hidden bg-slate-50 dark:bg-slate-950 text-slate-900 dark:text-slate-100">
      <Sidebar />
      <div className="flex flex-col flex-1 min-w-0 overflow-hidden">
        <Header title={title} />
        <main className="flex-1 overflow-y-auto">
          <div className="p-6 max-w-screen-2xl mx-auto">
            <Outlet />
          </div>
        </main>
      </div>
    </div>
  );
}
