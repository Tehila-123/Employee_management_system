import React from 'react';
import { Users, CalendarOff, UserPlus, TrendingUp } from 'lucide-react';
import { useApp } from '../../context/AppContext';
import { cn } from '../../lib/utils';

const AVATAR_COLORS = ['bg-indigo-500', 'bg-sky-500', 'bg-emerald-500', 'bg-violet-500', 'bg-rose-500', 'bg-amber-500'];

function avatarColor(name) {
  return AVATAR_COLORS[name.charCodeAt(0) % AVATAR_COLORS.length];
}

function Avatar({ name, size = 'md' }) {
  const initials = name.split(' ').map(n => n[0]).join('').slice(0, 2).toUpperCase();
  return (
    <div className={cn(
      'rounded-full flex items-center justify-center font-bold text-white shrink-0',
      avatarColor(name),
      size === 'sm' ? 'w-8 h-8 text-xs' : 'w-10 h-10 text-sm'
    )}>
      {initials}
    </div>
  );
}

function StatCard({ icon: Icon, label, value, color }) {
  return (
    <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-xl p-5 flex items-center gap-4">
      <div className={cn('w-11 h-11 rounded-xl flex items-center justify-center shrink-0', color)}>
        <Icon size={20} className="text-white" />
      </div>
      <div className="min-w-0">
        <p className="text-2xl font-bold text-slate-900 dark:text-slate-100 leading-tight">{value}</p>
        <p className="text-xs text-slate-500 dark:text-slate-400 mt-0.5 truncate">{label}</p>
      </div>
    </div>
  );
}

const CHECKLIST = [
  { task: 'Sign Employment Contract', done: true },
  { task: 'IT Setup — Laptop & Accounts', done: true },
  { task: 'Intro Meeting with Manager', done: false },
  { task: 'Complete Security Training', done: false },
  { task: 'Review Company Handbook', done: false },
];

function OnboardingCard({ employee }) {
  const done = CHECKLIST.filter(c => c.done).length;
  const pct = Math.round((done / CHECKLIST.length) * 100);
  return (
    <div className="border border-slate-200 dark:border-slate-700 rounded-xl p-4 space-y-3 bg-slate-50 dark:bg-slate-800/40">
      <div className="flex items-center gap-3">
        <div className="w-9 h-9 rounded-full bg-amber-500 flex items-center justify-center text-white text-sm font-bold shrink-0">
          {employee.name[0]}
        </div>
        <div className="min-w-0 flex-1">
          <p className="text-sm font-semibold text-slate-800 dark:text-slate-200 truncate">{employee.name}</p>
          <p className="text-xs text-slate-400">Started {employee.hireDate}</p>
        </div>
        <span className="text-xs font-bold text-amber-500 shrink-0">{pct}%</span>
      </div>
      <div className="w-full bg-slate-200 dark:bg-slate-700 rounded-full h-1.5">
        <div className="bg-amber-500 h-1.5 rounded-full transition-all duration-500" style={{ width: `${pct}%` }} />
      </div>
      <ul className="space-y-2">
        {CHECKLIST.map((c, i) => (
          <li key={i} className="flex items-center gap-2.5 text-xs">
            <span className={cn(
              'w-4 h-4 rounded-full border-2 flex items-center justify-center shrink-0 text-[9px] font-bold',
              c.done
                ? 'bg-emerald-500 border-emerald-500 text-white'
                : 'border-slate-300 dark:border-slate-600 text-transparent'
            )}>✓</span>
            <span className={cn(
              c.done ? 'line-through text-slate-400' : 'text-slate-600 dark:text-slate-300'
            )}>{c.task}</span>
          </li>
        ))}
      </ul>
    </div>
  );
}

export default function DashboardHome() {
  const { employees, leaveRequests } = useApp();

  const today = new Date().toISOString().split('T')[0];
  const outToday = leaveRequests.filter(r =>
    r.status === 'approved' && r.startDate <= today && r.endDate >= today
  );
  const recentHires = [...employees]
    .sort((a, b) => new Date(b.hireDate) - new Date(a.hireDate))
    .slice(0, 4);
  const onboarding = employees.filter(e => e.status === 'onboarding');
  const pending = leaveRequests.filter(r => r.status === 'pending').length;

  const upcomingBirthdays = employees
    .map(e => ({ ...e, bday: e.birthday.slice(5) }))
    .filter(e => {
      const diff = (new Date(`2026-${e.bday}`) - new Date(today)) / 86400000;
      return diff >= 0 && diff <= 30;
    })
    .sort((a, b) => a.bday.localeCompare(b.bday))
    .slice(0, 4);

  return (
    <div className="space-y-6">

      {/* Stat cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-4 gap-4">
        <StatCard icon={Users}       label="Total Employees"   value={employees.length}  color="bg-indigo-500" />
        <StatCard icon={CalendarOff} label="Out Today"         value={outToday.length}   color="bg-rose-500"   />
        <StatCard icon={UserPlus}    label="Onboarding"        value={onboarding.length} color="bg-amber-500"  />
        <StatCard icon={TrendingUp}  label="Pending Requests"  value={pending}           color="bg-emerald-500"/>
      </div>

      {/* Three info panels */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-5">

        {/* Team Out Today */}
        <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-xl p-5 flex flex-col gap-4">
          <h2 className="flex items-center gap-2 text-sm font-semibold text-slate-700 dark:text-slate-300">
            <CalendarOff size={15} className="text-rose-400 shrink-0" />
            Team Out Today
          </h2>
          {outToday.length === 0 ? (
            <p className="text-sm text-slate-400">Everyone is in today 🎉</p>
          ) : (
            <ul className="space-y-3">
              {outToday.map(r => (
                <li key={r.id} className="flex items-center gap-3">
                  <Avatar name={r.employeeName} size="sm" />
                  <div className="min-w-0">
                    <p className="text-sm font-medium text-slate-800 dark:text-slate-200 truncate">{r.employeeName}</p>
                    <p className="text-xs text-slate-400">{r.type}</p>
                  </div>
                </li>
              ))}
            </ul>
          )}
        </div>

        {/* Recent Hires */}
        <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-xl p-5 flex flex-col gap-4">
          <h2 className="flex items-center gap-2 text-sm font-semibold text-slate-700 dark:text-slate-300">
            <UserPlus size={15} className="text-indigo-400 shrink-0" />
            Recent Hires
          </h2>
          <ul className="space-y-3">
            {recentHires.map(e => (
              <li key={e.id} className="flex items-center gap-3">
                <Avatar name={e.name} size="sm" />
                <div className="min-w-0 flex-1">
                  <p className="text-sm font-medium text-slate-800 dark:text-slate-200 truncate">{e.name}</p>
                  <p className="text-xs text-slate-400 truncate">{e.title}</p>
                </div>
                <span className="text-[10px] text-slate-400 shrink-0 tabular-nums">{e.hireDate}</span>
              </li>
            ))}
          </ul>
        </div>

        {/* Upcoming Birthdays */}
        <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-xl p-5 flex flex-col gap-4">
          <h2 className="flex items-center gap-2 text-sm font-semibold text-slate-700 dark:text-slate-300">
            <span>🎂</span>
            Upcoming Birthdays
          </h2>
          {upcomingBirthdays.length === 0 ? (
            <p className="text-sm text-slate-400">No birthdays in the next 30 days</p>
          ) : (
            <ul className="space-y-3">
              {upcomingBirthdays.map(e => (
                <li key={e.id} className="flex items-center gap-3">
                  <Avatar name={e.name} size="sm" />
                  <div className="min-w-0">
                    <p className="text-sm font-medium text-slate-800 dark:text-slate-200 truncate">{e.name}</p>
                    <p className="text-xs text-slate-400">
                      {new Date(`2026-${e.bday}`).toLocaleDateString('en-US', { month: 'short', day: 'numeric' })}
                    </p>
                  </div>
                </li>
              ))}
            </ul>
          )}
        </div>
      </div>

      {/* Active Onboarding */}
      {onboarding.length > 0 && (
        <div className="bg-white dark:bg-slate-900 border border-amber-200 dark:border-amber-900/50 rounded-xl p-5">
          <h2 className="flex items-center gap-2 text-sm font-semibold text-slate-700 dark:text-slate-300 mb-4">
            <span>📋</span> Active Onboarding
          </h2>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {onboarding.map(emp => (
              <OnboardingCard key={emp.id} employee={emp} />
            ))}
          </div>
        </div>
      )}
    </div>
  );
}
