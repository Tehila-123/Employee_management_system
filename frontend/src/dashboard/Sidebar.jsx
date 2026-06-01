import React from 'react';
import { NavLink, useNavigate } from 'react-router-dom';
import {
  LayoutDashboard, Users, CalendarDays, Star,
  FolderLock, ChevronLeft, ChevronRight, LogOut,
} from 'lucide-react';
import { useApp } from '../context/AppContext';
import { cn } from '../lib/utils';

const NAV = [
  { to: '/dashboard', icon: LayoutDashboard, label: 'Dashboard' },
  { to: '/dashboard/employees', icon: Users, label: 'Employees' },
  { to: '/dashboard/time-off', icon: CalendarDays, label: 'Time Off' },
  { to: '/dashboard/skills', icon: Star, label: 'Skill Matrix' },
  { to: '/dashboard/documents', icon: FolderLock, label: 'Documents' },
];

export default function Sidebar() {
  const { sidebarOpen, setSidebarOpen, currentUser, logout } = useApp();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/auth');
  };

  // Avatar initials + colour
  const initials = currentUser.name
    .split(' ').map(w => w[0]).join('').slice(0, 2).toUpperCase();
  const COLORS = ['bg-indigo-500','bg-sky-500','bg-emerald-500','bg-violet-500','bg-rose-500','bg-amber-500'];
  const avatarBg = COLORS[currentUser.name.charCodeAt(0) % COLORS.length];

  return (
    <aside
      className={cn(
        'flex flex-col shrink-0 h-full overflow-hidden',
        'bg-slate-900 border-r border-slate-800 text-slate-100',
        'transition-[width] duration-300 ease-in-out',
        sidebarOpen ? 'w-56' : 'w-16'
      )}
    >
      {/* Brand */}
      <div className={cn(
        'flex items-center gap-3 border-b border-slate-800 shrink-0',
        sidebarOpen ? 'px-4 py-5' : 'justify-center px-0 py-5'
      )}>
        <span className="text-xl shrink-0">⚡</span>
        {sidebarOpen && (
          <span className="font-bold text-base tracking-tight bg-gradient-to-r from-sky-400 to-indigo-400 bg-clip-text text-transparent whitespace-nowrap overflow-hidden">
            StaffEase
          </span>
        )}
      </div>

      {/* Nav */}
      <nav className="flex-1 py-3 px-2 space-y-0.5 overflow-y-auto overflow-x-hidden">
        {NAV.map(({ to, icon: Icon, label }) => (
          <NavLink
            key={to}
            to={to}
            end={to === '/dashboard'}
            title={!sidebarOpen ? label : undefined}
            className={({ isActive }) => cn(
              'flex items-center gap-3 rounded-lg text-sm font-medium transition-colors',
              'py-2.5 px-3',
              isActive
                ? 'bg-indigo-600 text-white'
                : 'text-slate-400 hover:bg-slate-800 hover:text-slate-100',
              !sidebarOpen && 'justify-center px-0'
            )}
          >
            <Icon size={18} className="shrink-0" />
            {sidebarOpen && <span className="truncate">{label}</span>}
          </NavLink>
        ))}
      </nav>

      {/* User profile + controls */}
      <div className="border-t border-slate-800 p-3 space-y-1 shrink-0">

        {/* Profile row */}
        {sidebarOpen ? (
          <div className="flex items-center gap-2.5 px-1 py-2 rounded-lg hover:bg-slate-800 transition-colors min-w-0">
            <div className={cn(
              'w-8 h-8 rounded-full flex items-center justify-center text-xs font-bold text-white shrink-0',
              avatarBg
            )}>
              {initials}
            </div>
            <div className="min-w-0 flex-1">
              <p className="text-xs font-semibold text-slate-200 truncate leading-tight">
                {currentUser.name}
              </p>
              <p className="text-[10px] text-slate-500 truncate leading-tight">
                {currentUser.email || currentUser.role?.replace('_', ' ')}
              </p>
            </div>
            <button
              onClick={handleLogout}
              title="Sign out"
              className="p-1 rounded text-slate-500 hover:text-rose-400 transition-colors shrink-0"
            >
              <LogOut size={14} />
            </button>
          </div>
        ) : (
          <button
            onClick={handleLogout}
            title="Sign out"
            className="w-full flex items-center justify-center py-2 rounded-lg text-slate-500 hover:text-rose-400 hover:bg-slate-800 transition-colors"
          >
            <LogOut size={16} />
          </button>
        )}

        {/* Collapse toggle */}
        <button
          onClick={() => setSidebarOpen(o => !o)}
          className={cn(
            'w-full flex items-center gap-2 rounded-lg text-slate-400 hover:bg-slate-800 hover:text-slate-100 text-xs transition-colors py-2',
            sidebarOpen ? 'px-3 justify-start' : 'px-0 justify-center'
          )}
        >
          {sidebarOpen
            ? <><ChevronLeft size={15} /><span>Collapse</span></>
            : <ChevronRight size={15} />
          }
        </button>
      </div>
    </aside>
  );
}
