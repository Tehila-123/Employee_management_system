import React from 'react';
import { Sun, Moon, Bell } from 'lucide-react';
import { useApp } from '../context/AppContext';

export default function Header({ title }) {
  const { theme, toggleTheme, leaveRequests } = useApp();
  const pending = leaveRequests.filter(r => r.status === 'pending').length;

  return (
    <header className="flex items-center justify-between px-6 py-4 border-b border-slate-200 dark:border-slate-800 bg-white dark:bg-slate-950 shrink-0">
      <h1 className="text-lg font-semibold text-slate-900 dark:text-slate-100 truncate">{title}</h1>
      <div className="flex items-center gap-1 shrink-0 ml-4">
        <button
          onClick={toggleTheme}
          className="p-2 rounded-lg text-slate-500 dark:text-slate-400 hover:bg-slate-100 dark:hover:bg-slate-800 transition-colors"
          aria-label="Toggle theme"
        >
          {theme === 'dark' ? <Sun size={18} /> : <Moon size={18} />}
        </button>
        <button className="relative p-2 rounded-lg text-slate-500 dark:text-slate-400 hover:bg-slate-100 dark:hover:bg-slate-800 transition-colors" aria-label="Notifications">
          <Bell size={18} />
          {pending > 0 && (
            <span className="absolute top-1.5 right-1.5 w-3.5 h-3.5 bg-red-500 text-white text-[9px] font-bold rounded-full flex items-center justify-center leading-none">
              {pending}
            </span>
          )}
        </button>
      </div>
    </header>
  );
}
