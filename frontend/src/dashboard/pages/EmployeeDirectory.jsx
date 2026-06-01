import React, { useState } from 'react';
import { Search, MapPin, Briefcase, X, Plus, UserPlus, Trash2 } from 'lucide-react';
import { useApp } from '../../context/AppContext';
import { cn } from '../../lib/utils';
import { createEmployee } from '../../api/employees';

const STATUS_COLORS = {
  active:     'bg-emerald-100 text-emerald-700 dark:bg-emerald-900/30 dark:text-emerald-400',
  onboarding: 'bg-amber-100  text-amber-700  dark:bg-amber-900/30  dark:text-amber-400',
  inactive:   'bg-slate-100  text-slate-500  dark:bg-slate-800     dark:text-slate-400',
};
const AVATAR_COLORS = ['bg-indigo-500','bg-sky-500','bg-emerald-500','bg-violet-500','bg-rose-500','bg-amber-500'];
const avatarColor = name => AVATAR_COLORS[name.charCodeAt(0) % AVATAR_COLORS.length];
const DEPARTMENTS = ['Engineering','Design','Marketing','Product','Operations','HR','Finance'];
const STATUSES    = ['active','onboarding','inactive'];
const SKILL_LEVELS = [1,2,3,4,5];
const LEVEL_LABELS = {1:'Beginner',2:'Basic',3:'Intermediate',4:'Advanced',5:'Expert'};

// ── Shared UI ──────────────────────────────────────────────────────────────

function Initials({ name, size = 'md' }) {
  const text = name.split(' ').map(n => n[0]).join('').slice(0, 2).toUpperCase();
  return (
    <div className={cn(
      'rounded-full flex items-center justify-center font-bold text-white shrink-0',
      avatarColor(name),
      size === 'sm' ? 'w-8 h-8 text-xs' : size === 'lg' ? 'w-16 h-16 text-xl' : 'w-12 h-12 text-base'
    )}>{text}</div>
  );
}

// ── Employee Card ──────────────────────────────────────────────────────────

function EmployeeCard({ emp, onClick }) {
  return (
    <div onClick={() => onClick(emp)}
      className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-xl p-5 cursor-pointer hover:border-indigo-400 dark:hover:border-indigo-500 hover:shadow-lg transition-all group flex flex-col gap-3">
      <div className="flex items-start gap-3">
        <Initials name={emp.name} size="md" />
        <div className="min-w-0 flex-1">
          <p className="font-semibold text-slate-900 dark:text-slate-100 truncate group-hover:text-indigo-500 transition-colors text-sm">{emp.name}</p>
          <p className="text-xs text-slate-500 truncate mt-0.5">{emp.title}</p>
        </div>
        <span className={cn('text-[10px] font-semibold px-2 py-0.5 rounded-full capitalize shrink-0', STATUS_COLORS[emp.status])}>
          {emp.status}
        </span>
      </div>
      <div className="flex flex-col gap-1">
        <div className="flex items-center gap-1.5 text-xs text-slate-400"><Briefcase size={11} /><span className="truncate">{emp.department}</span></div>
        <div className="flex items-center gap-1.5 text-xs text-slate-400"><MapPin size={11} /><span>{emp.workMode}</span></div>
        {emp.birthday && <div className="text-xs text-slate-400">🎂 {new Date(emp.birthday).toLocaleDateString('en-US',{month:'short',day:'numeric'})}</div>}
      </div>
      <div className="flex flex-wrap gap-1 pt-1 border-t border-slate-100 dark:border-slate-800">
        {(emp.skills||[]).slice(0,3).map(s => (
          <span key={s.name} className="text-[10px] bg-slate-100 dark:bg-slate-800 text-slate-600 dark:text-slate-400 px-2 py-0.5 rounded-full">{s.name}</span>
        ))}
        {(emp.skills||[]).length > 3 && <span className="text-[10px] text-slate-400 px-1">+{emp.skills.length-3} more</span>}
      </div>
    </div>
  );
}

// ── View Modal ─────────────────────────────────────────────────────────────

function EmployeeModal({ emp, onClose }) {
  if (!emp) return null;
  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-sm" onClick={onClose}>
      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl w-full max-w-md shadow-2xl max-h-[90vh] flex flex-col" onClick={e => e.stopPropagation()}>
        <div className="flex items-center gap-4 p-6 border-b border-slate-100 dark:border-slate-800">
          <Initials name={emp.name} size="lg" />
          <div className="min-w-0 flex-1">
            <h2 className="text-lg font-bold text-slate-900 dark:text-slate-100 truncate">{emp.name}</h2>
            <p className="text-sm text-slate-500 truncate">{emp.title} · {emp.department}</p>
            <span className={cn('inline-block mt-1 text-[10px] font-semibold px-2 py-0.5 rounded-full capitalize', STATUS_COLORS[emp.status])}>{emp.status}</span>
          </div>
          <button onClick={onClose} className="p-1.5 rounded-lg text-slate-400 hover:bg-slate-100 dark:hover:bg-slate-800 transition-colors shrink-0"><X size={16} /></button>
        </div>
        <div className="overflow-y-auto p-6 space-y-5">
          <div className="grid grid-cols-2 gap-3">
            {[
              { label:'Email',     value: emp.email },
              { label:'Work Mode', value: emp.workMode },
              { label:'Hire Date', value: emp.hireDate },
              { label:'Birthday',  value: emp.birthday ? new Date(emp.birthday).toLocaleDateString('en-US',{month:'long',day:'numeric'}) : '—' },
            ].map(({ label, value }) => (
              <div key={label} className="bg-slate-50 dark:bg-slate-800 rounded-lg p-3">
                <p className="text-[10px] font-semibold text-slate-400 uppercase tracking-wider mb-1">{label}</p>
                <p className="text-sm font-medium text-slate-700 dark:text-slate-300 truncate">{value}</p>
              </div>
            ))}
          </div>
          {(emp.skills||[]).length > 0 && (
            <div>
              <p className="text-[10px] font-semibold text-slate-400 uppercase tracking-wider mb-3">Skills</p>
              <div className="space-y-2.5">
                {emp.skills.map(s => (
                  <div key={s.name} className="flex items-center gap-3">
                    <span className="text-sm text-slate-700 dark:text-slate-300 w-28 shrink-0 truncate">{s.name}</span>
                    <div className="flex-1 bg-slate-200 dark:bg-slate-700 rounded-full h-1.5 overflow-hidden">
                      <div className="bg-indigo-500 h-full rounded-full" style={{ width:`${(s.level/5)*100}%` }} />
                    </div>
                    <span className="text-xs text-slate-400 w-20 text-right shrink-0">{LEVEL_LABELS[s.level]}</span>
                  </div>
                ))}
              </div>
            </div>
          )}
        </div>
        <div className="p-4 border-t border-slate-100 dark:border-slate-800">
          <button onClick={onClose} className="w-full py-2 rounded-lg bg-slate-100 dark:bg-slate-800 text-slate-600 dark:text-slate-300 text-sm font-medium hover:bg-slate-200 dark:hover:bg-slate-700 transition-colors">Close</button>
        </div>
      </div>
    </div>
  );
}

// ── Add Employee Modal ─────────────────────────────────────────────────────

const EMPTY_FORM = {
  firstName:'', lastName:'', email:'', phone:'',
  jobTitle:'', department:'', status:'onboarding',
  salary:'', hireDate:'', workMode:'Remote', birthday:'',
};

function SkillsBuilder({ skills, onChange }) {
  const addSkill = () => onChange([...skills, { name:'', level:3 }]);
  const removeSkill = i => onChange(skills.filter((_,idx) => idx !== i));
  const updateSkill = (i, field, val) => onChange(skills.map((s,idx) => idx===i ? {...s,[field]:val} : s));

  return (
    <div className="space-y-2">
      {skills.map((s, i) => (
        <div key={i} className="flex items-center gap-2">
          <input
            value={s.name}
            onChange={e => updateSkill(i,'name',e.target.value)}
            placeholder="e.g. React, Python…"
            className="flex-1 px-3 py-2 text-sm rounded-lg border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 text-slate-900 dark:text-slate-100 placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-indigo-500"
          />
          <select
            value={s.level}
            onChange={e => updateSkill(i,'level',Number(e.target.value))}
            className="px-2 py-2 text-sm rounded-lg border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 text-slate-700 dark:text-slate-300 focus:outline-none focus:ring-2 focus:ring-indigo-500"
          >
            {SKILL_LEVELS.map(l => <option key={l} value={l}>{l} – {LEVEL_LABELS[l]}</option>)}
          </select>
          <button type="button" onClick={() => removeSkill(i)}
            className="p-1.5 rounded-lg text-slate-400 hover:text-rose-500 hover:bg-rose-50 dark:hover:bg-rose-900/20 transition-colors">
            <Trash2 size={14} />
          </button>
        </div>
      ))}
      <button type="button" onClick={addSkill}
        className="flex items-center gap-1.5 text-xs text-indigo-500 hover:text-indigo-600 font-medium py-1 transition-colors">
        <Plus size={13} /> Add skill
      </button>
    </div>
  );
}

function AddEmployeeModal({ onClose, onSaved }) {
  const [form, setForm] = useState(EMPTY_FORM);
  const [skills, setSkills] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const set = (k, v) => setForm(f => ({ ...f, [k]: v }));

  const handleSubmit = async e => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      const payload = {
        firstName: form.firstName, lastName: form.lastName,
        email: form.email, phone: form.phone || null,
        jobTitle: form.jobTitle, status: form.status,
        salary: parseFloat(form.salary) || 0,
        hireDate: form.hireDate || null,
      };
      const saved = await createEmployee(payload);
      onSaved(saved, form, skills);
      onClose();
    } catch {
      // Backend offline — save locally
      onSaved(null, form, skills);
      onClose();
    } finally { setLoading(false); }
  };

  const ic = 'w-full px-3 py-2 text-sm rounded-lg border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 text-slate-900 dark:text-slate-100 placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-indigo-500';
  const lc = 'block text-xs font-semibold text-slate-500 dark:text-slate-400 uppercase tracking-wider mb-1.5';

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-sm" onClick={onClose}>
      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl w-full max-w-lg shadow-2xl max-h-[92vh] flex flex-col" onClick={e => e.stopPropagation()}>

        {/* Header */}
        <div className="flex items-center justify-between p-5 border-b border-slate-100 dark:border-slate-800 shrink-0">
          <div className="flex items-center gap-3">
            <div className="w-9 h-9 rounded-xl bg-indigo-600 flex items-center justify-center"><UserPlus size={18} className="text-white" /></div>
            <div>
              <h2 className="text-base font-bold text-slate-900 dark:text-slate-100">Add Employee</h2>
              <p className="text-xs text-slate-400">Fill in the details below</p>
            </div>
          </div>
          <button onClick={onClose} className="p-1.5 rounded-lg text-slate-400 hover:bg-slate-100 dark:hover:bg-slate-800 transition-colors"><X size={16} /></button>
        </div>

        <form onSubmit={handleSubmit} className="overflow-y-auto p-5 space-y-4 flex-1">
          {error && <div className="p-3 rounded-lg bg-red-50 dark:bg-red-900/20 border border-red-200 dark:border-red-800 text-sm text-red-600 dark:text-red-400">{error}</div>}

          {/* Name */}
          <div className="grid grid-cols-2 gap-3">
            <div><label className={lc}>First Name *</label><input required value={form.firstName} onChange={e=>set('firstName',e.target.value)} placeholder="Jane" className={ic} /></div>
            <div><label className={lc}>Last Name *</label><input required value={form.lastName} onChange={e=>set('lastName',e.target.value)} placeholder="Doe" className={ic} /></div>
          </div>

          {/* Email + Phone */}
          <div className="grid grid-cols-2 gap-3">
            <div><label className={lc}>Email *</label><input required type="email" value={form.email} onChange={e=>set('email',e.target.value)} placeholder="jane@company.com" className={ic} /></div>
            <div><label className={lc}>Phone</label><input value={form.phone} onChange={e=>set('phone',e.target.value)} placeholder="+1 555 000 0000" className={ic} /></div>
          </div>

          {/* Job + Dept */}
          <div className="grid grid-cols-2 gap-3">
            <div><label className={lc}>Job Title *</label><input required value={form.jobTitle} onChange={e=>set('jobTitle',e.target.value)} placeholder="Senior Engineer" className={ic} /></div>
            <div>
              <label className={lc}>Department</label>
              <select value={form.department} onChange={e=>set('department',e.target.value)} className={ic}>
                <option value="">Select…</option>
                {DEPARTMENTS.map(d=><option key={d}>{d}</option>)}
              </select>
            </div>
          </div>

          {/* Status + Work Mode */}
          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className={lc}>Status *</label>
              <select required value={form.status} onChange={e=>set('status',e.target.value)} className={ic}>
                {STATUSES.map(s=><option key={s} className="capitalize">{s}</option>)}
              </select>
            </div>
            <div>
              <label className={lc}>Work Mode</label>
              <select value={form.workMode} onChange={e=>set('workMode',e.target.value)} className={ic}>
                {['Remote','In-office','Hybrid'].map(m=><option key={m}>{m}</option>)}
              </select>
            </div>
          </div>

          {/* Salary + Hire Date */}
          <div className="grid grid-cols-2 gap-3">
            <div><label className={lc}>Salary (monthly) *</label><input required type="number" min="0" step="0.01" value={form.salary} onChange={e=>set('salary',e.target.value)} placeholder="5000" className={ic} /></div>
            <div><label className={lc}>Hire Date</label><input type="date" value={form.hireDate} onChange={e=>set('hireDate',e.target.value)} className={ic} /></div>
          </div>

          {/* Birthday */}
          <div>
            <label className={lc}>Birthday <span className="normal-case font-normal text-slate-400">(for birthday tracking)</span></label>
            <input type="date" value={form.birthday} onChange={e=>set('birthday',e.target.value)} className={ic} />
            <p className="text-[10px] text-slate-400 mt-1">Used to show upcoming birthdays on the dashboard.</p>
          </div>

          {/* Skills */}
          <div>
            <label className={lc}>Skills & Proficiency <span className="normal-case font-normal text-slate-400">(shown in Skill Matrix)</span></label>
            <SkillsBuilder skills={skills} onChange={setSkills} />
          </div>

          {/* Buttons */}
          <div className="flex gap-3 pt-2">
            <button type="button" onClick={onClose} className="flex-1 py-2.5 rounded-lg bg-slate-100 dark:bg-slate-800 text-slate-600 dark:text-slate-300 text-sm font-medium hover:bg-slate-200 dark:hover:bg-slate-700 transition-colors">Cancel</button>
            <button type="submit" disabled={loading} className="flex-1 py-2.5 rounded-lg bg-indigo-600 hover:bg-indigo-700 disabled:opacity-60 text-white text-sm font-semibold transition-colors flex items-center justify-center gap-2">
              {loading ? <><span className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin" />Saving…</> : <><Plus size={15} />Add Employee</>}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

// ── Main page ──────────────────────────────────────────────────────────────

export default function EmployeeDirectory() {
  const { employees, setEmployees } = useApp();
  const [search, setSearch]         = useState('');
  const [filterDept, setFilterDept] = useState('All');
  const [filterStatus, setFilterStatus] = useState('All');
  const [selected, setSelected]     = useState(null);
  const [showAdd, setShowAdd]       = useState(false);

  const departments = ['All', ...new Set(employees.map(e => e.department))];
  const statuses    = ['All','active','onboarding','inactive'];

  const filtered = employees.filter(e => {
    const q = search.toLowerCase();
    return (
      (e.name.toLowerCase().includes(q) || e.title.toLowerCase().includes(q) ||
       (e.skills||[]).some(s => s.name.toLowerCase().includes(q))) &&
      (filterDept   === 'All' || e.department === filterDept) &&
      (filterStatus === 'All' || e.status     === filterStatus)
    );
  });

  const handleEmployeeSaved = (apiEmployee, form, skills) => {
    const newEmp = {
      id:         String(apiEmployee?.empId ?? Date.now()),
      name:       `${form.firstName} ${form.lastName}`,
      email:      form.email,
      role:       'employee',
      title:      form.jobTitle,
      department: form.department,
      status:     form.status,
      workMode:   form.workMode,
      hireDate:   form.hireDate || new Date().toISOString().split('T')[0],
      birthday:   form.birthday || null,
      skills:     skills.filter(s => s.name.trim()),
    };
    setEmployees(prev => [newEmp, ...prev]);
  };

  const ic = 'px-3 py-2 text-sm rounded-lg border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-900 text-slate-900 dark:text-slate-100 focus:outline-none focus:ring-2 focus:ring-indigo-500 transition-shadow';

  return (
    <div className="space-y-5">
      {/* Toolbar */}
      <div className="flex flex-wrap gap-3 items-center">
        <div className="relative flex-1 min-w-[200px]">
          <Search size={15} className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400 pointer-events-none" />
          <input value={search} onChange={e=>setSearch(e.target.value)} placeholder="Search by name, role, or skill…" className={cn(ic,'w-full pl-9 pr-4 placeholder-slate-400')} />
        </div>
        <select value={filterDept}   onChange={e=>setFilterDept(e.target.value)}   className={ic}>{departments.map(d=><option key={d}>{d}</option>)}</select>
        <select value={filterStatus} onChange={e=>setFilterStatus(e.target.value)} className={ic}>{statuses.map(s=><option key={s} className="capitalize">{s}</option>)}</select>
        <button onClick={()=>setShowAdd(true)} className="flex items-center gap-1.5 px-4 py-2 bg-indigo-600 hover:bg-indigo-700 text-white text-sm font-semibold rounded-lg transition-colors shrink-0">
          <Plus size={15} /> Add Employee
        </button>
      </div>

      <p className="text-xs text-slate-400">{filtered.length} employee{filtered.length!==1?'s':''} found</p>

      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4">
        {filtered.map(emp => <EmployeeCard key={emp.id} emp={emp} onClick={setSelected} />)}
      </div>

      <EmployeeModal emp={selected} onClose={()=>setSelected(null)} />
      {showAdd && <AddEmployeeModal onClose={()=>setShowAdd(false)} onSaved={handleEmployeeSaved} />}
    </div>
  );
}
