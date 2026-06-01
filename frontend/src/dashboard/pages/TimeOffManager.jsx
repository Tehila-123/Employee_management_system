import React, { useState } from 'react';
import { CheckCircle, XCircle, Plus, X, CalendarDays } from 'lucide-react';
import { useApp } from '../../context/AppContext';
import { cn } from '../../lib/utils';

const STATUS_STYLES = {
  pending:  'bg-amber-100  text-amber-700  dark:bg-amber-900/30  dark:text-amber-400',
  approved: 'bg-emerald-100 text-emerald-700 dark:bg-emerald-900/30 dark:text-emerald-400',
  denied:   'bg-red-100    text-red-700    dark:bg-red-900/30    dark:text-red-400',
};

const MONTHS = ['Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec'];

function MiniCalendar({ leaveRequests }) {
  const [viewDate, setViewDate] = useState(new Date(2026, 4, 1));
  const year  = viewDate.getFullYear();
  const month = viewDate.getMonth();
  const firstDay    = new Date(year, month, 1).getDay();
  const daysInMonth = new Date(year, month + 1, 0).getDate();
  const approved = leaveRequests.filter(r => r.status === 'approved');

  const isLeaveDay = day => {
    const d = `${year}-${String(month+1).padStart(2,'0')}-${String(day).padStart(2,'0')}`;
    return approved.some(r => r.startDate <= d && r.endDate >= d);
  };

  const cells = [
    ...Array(firstDay).fill(null),
    ...Array.from({ length: daysInMonth }, (_, i) => i + 1),
  ];

  return (
    <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-xl p-5 h-fit">
      <div className="flex items-center justify-between mb-4">
        <button onClick={() => setViewDate(new Date(year, month-1, 1))}
          className="w-7 h-7 flex items-center justify-center rounded-lg text-slate-500 hover:bg-slate-100 dark:hover:bg-slate-800 transition-colors">‹</button>
        <span className="text-sm font-semibold text-slate-800 dark:text-slate-200">{MONTHS[month]} {year}</span>
        <button onClick={() => setViewDate(new Date(year, month+1, 1))}
          className="w-7 h-7 flex items-center justify-center rounded-lg text-slate-500 hover:bg-slate-100 dark:hover:bg-slate-800 transition-colors">›</button>
      </div>
      <div className="grid grid-cols-7 mb-1">
        {['Su','Mo','Tu','We','Th','Fr','Sa'].map(d => (
          <div key={d} className="text-center text-[10px] font-semibold text-slate-400 py-1">{d}</div>
        ))}
      </div>
      <div className="grid grid-cols-7 gap-y-0.5">
        {cells.map((day, i) => (
          <div key={i} className={cn(
            'text-center text-xs py-1.5 rounded-md leading-none',
            !day && 'invisible',
            day && !isLeaveDay(day) && 'text-slate-600 dark:text-slate-400',
            day && isLeaveDay(day) && 'bg-indigo-100 dark:bg-indigo-900/40 text-indigo-700 dark:text-indigo-300 font-semibold'
          )}>{day ?? ''}</div>
        ))}
      </div>
      <div className="mt-4 pt-3 border-t border-slate-100 dark:border-slate-800 flex items-center gap-2 text-xs text-slate-400">
        <span className="w-3 h-3 rounded bg-indigo-100 dark:bg-indigo-900/40 shrink-0" />
        Approved leave
      </div>
    </div>
  );
}

function RequestModal({ onClose, onSubmit }) {
  const { currentUser } = useApp();
  const [form, setForm] = useState({ type: 'PTO', startDate: '', endDate: '', reason: '' });
  const inputCls = 'w-full px-3 py-2 text-sm rounded-lg border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 text-slate-900 dark:text-slate-100 focus:outline-none focus:ring-2 focus:ring-indigo-500';

  const handleSubmit = e => {
    e.preventDefault();
    const days = Math.max(1, Math.ceil((new Date(form.endDate) - new Date(form.startDate)) / 86400000) + 1);
    onSubmit({ ...form, days, employeeId: currentUser.id, employeeName: currentUser.name });
    onClose();
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-sm" onClick={onClose}>
      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl w-full max-w-md shadow-2xl" onClick={e => e.stopPropagation()}>
        <div className="flex items-center justify-between p-5 border-b border-slate-100 dark:border-slate-800">
          <h2 className="text-base font-bold text-slate-900 dark:text-slate-100">Request Time Off</h2>
          <button onClick={onClose} className="p-1.5 rounded-lg text-slate-400 hover:bg-slate-100 dark:hover:bg-slate-800 transition-colors"><X size={16} /></button>
        </div>
        <form onSubmit={handleSubmit} className="p-5 space-y-4">
          <div>
            <label className="block text-xs font-semibold text-slate-500 uppercase tracking-wider mb-1.5">Leave Type</label>
            <select value={form.type} onChange={e => setForm(f => ({...f, type: e.target.value}))} className={inputCls}>
              {['PTO','Sick Leave','Maternity/Paternity','Unpaid Leave'].map(t => <option key={t}>{t}</option>)}
            </select>
          </div>
          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="block text-xs font-semibold text-slate-500 uppercase tracking-wider mb-1.5">Start Date</label>
              <input type="date" required value={form.startDate} onChange={e => setForm(f => ({...f, startDate: e.target.value}))} className={inputCls} />
            </div>
            <div>
              <label className="block text-xs font-semibold text-slate-500 uppercase tracking-wider mb-1.5">End Date</label>
              <input type="date" required value={form.endDate} onChange={e => setForm(f => ({...f, endDate: e.target.value}))} className={inputCls} />
            </div>
          </div>
          <div>
            <label className="block text-xs font-semibold text-slate-500 uppercase tracking-wider mb-1.5">Reason <span className="normal-case font-normal text-slate-400">(optional)</span></label>
            <textarea value={form.reason} onChange={e => setForm(f => ({...f, reason: e.target.value}))} rows={3}
              placeholder="Add a note for your manager…"
              className={cn(inputCls, 'resize-none placeholder-slate-400')} />
          </div>
          <button type="submit" className="w-full py-2.5 rounded-lg bg-indigo-600 hover:bg-indigo-700 text-white text-sm font-semibold transition-colors">
            Submit Request
          </button>
        </form>
      </div>
    </div>
  );
}

export default function TimeOffManager() {
  const { leaveRequests, updateLeaveStatus, addLeaveRequest, currentUser } = useApp();
  const [showModal, setShowModal] = useState(false);
  const [tab, setTab] = useState('all');

  const filtered = leaveRequests.filter(r => {
    if (tab === 'pending')  return r.status === 'pending';
    if (tab === 'approved') return r.status === 'approved';
    if (tab === 'mine')     return r.employeeId === currentUser.id;
    return true;
  });

  const isManager = currentUser.role === 'manager' || currentUser.role === 'super_admin';

  return (
    <div className="space-y-5">
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-5 items-start">
        <div className="lg:col-span-2 space-y-4">
          <div className="flex flex-wrap items-center gap-3">
            <div className="flex gap-0.5 bg-slate-100 dark:bg-slate-800 rounded-lg p-1">
              {['all','pending','approved','mine'].map(t => (
                <button key={t} onClick={() => setTab(t)}
                  className={cn('px-3 py-1.5 text-xs font-medium rounded-md capitalize transition-colors whitespace-nowrap',
                    tab === t ? 'bg-white dark:bg-slate-700 text-slate-900 dark:text-slate-100 shadow-sm' : 'text-slate-500 hover:text-slate-700 dark:hover:text-slate-300'
                  )}>
                  {t === 'mine' ? 'My Requests' : t}
                </button>
              ))}
            </div>
            <button onClick={() => setShowModal(true)}
              className="ml-auto flex items-center gap-1.5 px-3 py-2 bg-indigo-600 hover:bg-indigo-700 text-white text-xs font-semibold rounded-lg transition-colors">
              <Plus size={14} /> Request
            </button>
          </div>

          <div className="space-y-2.5">
            {filtered.length === 0 && (
              <div className="flex flex-col items-center justify-center py-12 text-slate-400 gap-2">
                <CalendarDays size={32} className="opacity-30" />
                <p className="text-sm">No requests found</p>
              </div>
            )}
            {filtered.map(r => (
              <div key={r.id} className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-xl p-4 flex items-center gap-3">
                <div className="w-9 h-9 rounded-full bg-indigo-500 flex items-center justify-center text-white text-xs font-bold shrink-0">
                  {r.employeeName[0]}
                </div>
                <div className="flex-1 min-w-0">
                  <div className="flex items-center gap-2 flex-wrap mb-0.5">
                    <p className="text-sm font-semibold text-slate-800 dark:text-slate-200">{r.employeeName}</p>
                    <span className={cn('text-[10px] font-semibold px-2 py-0.5 rounded-full capitalize', STATUS_STYLES[r.status])}>
                      {r.status}
                    </span>
                  </div>
                  <p className="text-xs text-slate-500">
                    {r.type} · <span className="tabular-nums">{r.startDate}</span> → <span className="tabular-nums">{r.endDate}</span>
                    <span className="text-slate-400"> ({r.days}d)</span>
                  </p>
                  {r.reason && <p className="text-xs text-slate-400 mt-0.5 truncate italic">"{r.reason}"</p>}
                </div>
                {isManager && r.status === 'pending' && (
                  <div className="flex gap-2 shrink-0">
                    <button onClick={() => updateLeaveStatus(r.id, 'approved')}
                      className="flex items-center gap-1 px-2.5 py-1.5 bg-emerald-50 dark:bg-emerald-900/20 text-emerald-600 dark:text-emerald-400 text-xs font-medium rounded-lg hover:bg-emerald-100 dark:hover:bg-emerald-900/40 border border-emerald-200 dark:border-emerald-800 transition-colors">
                      <CheckCircle size={13} /> Approve
                    </button>
                    <button onClick={() => updateLeaveStatus(r.id, 'denied')}
                      className="flex items-center gap-1 px-2.5 py-1.5 bg-red-50 dark:bg-red-900/20 text-red-600 dark:text-red-400 text-xs font-medium rounded-lg hover:bg-red-100 dark:hover:bg-red-900/40 border border-red-200 dark:border-red-800 transition-colors">
                      <XCircle size={13} /> Deny
                    </button>
                  </div>
                )}
              </div>
            ))}
          </div>
        </div>
        <MiniCalendar leaveRequests={leaveRequests} />
      </div>
      {showModal && <RequestModal onClose={() => setShowModal(false)} onSubmit={addLeaveRequest} />}
    </div>
  );
}
