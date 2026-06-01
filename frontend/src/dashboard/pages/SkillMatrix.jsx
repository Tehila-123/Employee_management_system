import React, { useState } from 'react';
import { useApp } from '../../context/AppContext';
import { cn } from '../../lib/utils';

const LEVEL_LABELS = ['', 'Beginner', 'Basic', 'Intermediate', 'Advanced', 'Expert'];
const LEVEL_COLORS = ['', 'bg-slate-400', 'bg-sky-400', 'bg-blue-500', 'bg-indigo-500', 'bg-violet-600'];
const LEVEL_BG    = ['', 'bg-slate-100 text-slate-600 dark:bg-slate-800 dark:text-slate-400',
                        'bg-sky-100 text-sky-700 dark:bg-sky-900/30 dark:text-sky-400',
                        'bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-400',
                        'bg-indigo-100 text-indigo-700 dark:bg-indigo-900/30 dark:text-indigo-400',
                        'bg-violet-100 text-violet-700 dark:bg-violet-900/30 dark:text-violet-400'];

function SkillPips({ level }) {
  return (
    <div className="flex gap-0.5 items-center">
      {[1,2,3,4,5].map(i => (
        <div key={i} className={cn(
          'w-2 h-2 rounded-full',
          i <= level ? LEVEL_COLORS[level] : 'bg-slate-200 dark:bg-slate-700'
        )} />
      ))}
    </div>
  );
}

export default function SkillMatrix() {
  const { employees } = useApp();
  const [search, setSearch]         = useState('');
  const [filterSkill, setFilterSkill] = useState('All');

  const allSkills = [...new Set(employees.flatMap(e => e.skills.map(s => s.name)))].sort();

  const filtered = employees.filter(e => {
    const q = search.toLowerCase();
    return (e.name.toLowerCase().includes(q) || e.department.toLowerCase().includes(q)) &&
      (filterSkill === 'All' || e.skills.some(s => s.name === filterSkill));
  });

  // Coverage summary
  const skillCounts = {};
  employees.forEach(e => e.skills.forEach(s => {
    if (!skillCounts[s.name]) skillCounts[s.name] = { count: 0, total: 0 };
    skillCounts[s.name].count++;
    skillCounts[s.name].total += s.level;
  }));
  const topSkills = Object.entries(skillCounts)
    .map(([name, { count, total }]) => ({ name, count, avg: (total / count).toFixed(1) }))
    .sort((a, b) => b.count - a.count)
    .slice(0, 6);

  const inputCls = 'px-3 py-2 text-sm rounded-lg border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-900 text-slate-900 dark:text-slate-100 focus:outline-none focus:ring-2 focus:ring-indigo-500';

  return (
    <div className="space-y-6">

      {/* Coverage summary */}
      <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-6 gap-3">
        {topSkills.map(s => (
          <div key={s.name} className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-xl p-4 text-center">
            <p className="text-xs font-semibold text-slate-700 dark:text-slate-300 truncate">{s.name}</p>
            <p className="text-2xl font-bold text-indigo-500 mt-1 leading-tight">{s.count}</p>
            <p className="text-[10px] text-slate-400 mt-0.5">people · avg {s.avg}</p>
          </div>
        ))}
      </div>

      {/* Filters */}
      <div className="flex flex-wrap gap-3">
        <input
          value={search}
          onChange={e => setSearch(e.target.value)}
          placeholder="Search by employee or department…"
          className={cn(inputCls, 'flex-1 min-w-[200px] placeholder-slate-400')}
        />
        <select value={filterSkill} onChange={e => setFilterSkill(e.target.value)} className={inputCls}>
          <option>All</option>
          {allSkills.map(s => <option key={s}>{s}</option>)}
        </select>
      </div>

      {/* Matrix table */}
      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-xl overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-sm" style={{ minWidth: '600px' }}>
            <thead>
              <tr className="border-b border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-800/60">
                <th className="text-left px-4 py-3 text-[11px] font-semibold text-slate-500 uppercase tracking-wider w-44">Employee</th>
                <th className="text-left px-4 py-3 text-[11px] font-semibold text-slate-500 uppercase tracking-wider w-32">Department</th>
                <th className="text-left px-4 py-3 text-[11px] font-semibold text-slate-500 uppercase tracking-wider">Skills & Proficiency</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100 dark:divide-slate-800">
              {filtered.map(emp => (
                <tr key={emp.id} className="hover:bg-slate-50 dark:hover:bg-slate-800/30 transition-colors">
                  <td className="px-4 py-3.5">
                    <div className="flex items-center gap-2.5">
                      <div className="w-7 h-7 rounded-full bg-indigo-500 flex items-center justify-center text-white text-xs font-bold shrink-0">
                        {emp.name[0]}
                      </div>
                      <span className="font-medium text-slate-800 dark:text-slate-200 text-sm truncate">{emp.name}</span>
                    </div>
                  </td>
                  <td className="px-4 py-3.5">
                    <span className="text-xs text-slate-500 dark:text-slate-400">{emp.department}</span>
                  </td>
                  <td className="px-4 py-3.5">
                    <div className="flex flex-wrap gap-2">
                      {emp.skills.map(s => (
                        <div key={s.name} className={cn('flex items-center gap-1.5 px-2.5 py-1 rounded-lg text-xs font-medium', LEVEL_BG[s.level])}>
                          <span>{s.name}</span>
                          <SkillPips level={s.level} />
                        </div>
                      ))}
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* Legend */}
      <div className="flex flex-wrap gap-3">
        {[1,2,3,4,5].map(l => (
          <div key={l} className={cn('flex items-center gap-2 px-3 py-1.5 rounded-lg text-xs font-medium', LEVEL_BG[l])}>
            <SkillPips level={l} />
            <span>{LEVEL_LABELS[l]}</span>
          </div>
        ))}
      </div>
    </div>
  );
}
