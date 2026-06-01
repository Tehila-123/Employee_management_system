import React, { useState, useRef } from 'react';
import { Download, Lock, Search, Upload, FileText, X, CloudUpload } from 'lucide-react';
import { useApp } from '../../context/AppContext';
import { cn } from '../../lib/utils';

const TYPE_STYLES = {
  contract: { badge:'bg-indigo-100 text-indigo-700 dark:bg-indigo-900/30 dark:text-indigo-400', icon:'📄' },
  payslip:  { badge:'bg-emerald-100 text-emerald-700 dark:bg-emerald-900/30 dark:text-emerald-400', icon:'💰' },
  id:       { badge:'bg-amber-100 text-amber-700 dark:bg-amber-900/30 dark:text-amber-400', icon:'🪪' },
  other:    { badge:'bg-slate-100 text-slate-600 dark:bg-slate-800 dark:text-slate-400', icon:'📎' },
};

// ── Upload Modal ───────────────────────────────────────────────────────────

function UploadModal({ employees, onClose, onUploaded }) {
  const [docName, setDocName]       = useState('');
  const [docType, setDocType]       = useState('contract');
  const [employeeId, setEmployeeId] = useState(employees[0]?.id ?? '');
  const [file, setFile]             = useState(null);
  const [dragOver, setDragOver]     = useState(false);
  const [error, setError]           = useState('');
  const fileRef = useRef();

  const handleFile = f => {
    if (!f) return;
    setFile(f);
    if (!docName) setDocName(f.name.replace(/\.[^.]+$/, ''));
  };

  const handleDrop = e => {
    e.preventDefault(); setDragOver(false);
    handleFile(e.dataTransfer.files[0]);
  };

  const handleSubmit = e => {
    e.preventDefault();
    if (!file) { setError('Please select a file.'); return; }
    if (!docName.trim()) { setError('Please enter a document name.'); return; }

    // Store file as a blob URL so it can be downloaded later in the same session
    const blobUrl = URL.createObjectURL(file);
    const newDoc = {
      id:         String(Date.now()),
      employeeId,
      name:       docName.trim(),
      type:       docType,
      uploadedAt: new Date().toISOString().split('T')[0],
      size:       file.size > 1024*1024
                    ? `${(file.size/1024/1024).toFixed(1)} MB`
                    : `${Math.round(file.size/1024)} KB`,
      blobUrl,
      fileName:   file.name,
    };
    onUploaded(newDoc);
    onClose();
  };

  const ic = 'w-full px-3 py-2 text-sm rounded-lg border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 text-slate-900 dark:text-slate-100 placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-indigo-500';
  const lc = 'block text-xs font-semibold text-slate-500 dark:text-slate-400 uppercase tracking-wider mb-1.5';

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-sm" onClick={onClose}>
      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl w-full max-w-md shadow-2xl" onClick={e=>e.stopPropagation()}>

        {/* Header */}
        <div className="flex items-center justify-between p-5 border-b border-slate-100 dark:border-slate-800">
          <div className="flex items-center gap-3">
            <div className="w-9 h-9 rounded-xl bg-indigo-600 flex items-center justify-center"><CloudUpload size={18} className="text-white" /></div>
            <div>
              <h2 className="text-base font-bold text-slate-900 dark:text-slate-100">Upload Document</h2>
              <p className="text-xs text-slate-400">Attach a file to an employee record</p>
            </div>
          </div>
          <button onClick={onClose} className="p-1.5 rounded-lg text-slate-400 hover:bg-slate-100 dark:hover:bg-slate-800 transition-colors"><X size={16} /></button>
        </div>

        <form onSubmit={handleSubmit} className="p-5 space-y-4">
          {error && <div className="p-3 rounded-lg bg-red-50 dark:bg-red-900/20 border border-red-200 dark:border-red-800 text-sm text-red-600 dark:text-red-400">{error}</div>}

          {/* File drop zone */}
          <div
            onDragOver={e=>{e.preventDefault();setDragOver(true)}}
            onDragLeave={()=>setDragOver(false)}
            onDrop={handleDrop}
            onClick={()=>fileRef.current.click()}
            className={cn(
              'border-2 border-dashed rounded-xl p-6 text-center cursor-pointer transition-colors',
              dragOver
                ? 'border-indigo-500 bg-indigo-50 dark:bg-indigo-900/20'
                : 'border-slate-300 dark:border-slate-700 hover:border-indigo-400 dark:hover:border-indigo-600'
            )}
          >
            <input ref={fileRef} type="file" className="hidden" accept=".pdf,.doc,.docx,.png,.jpg,.jpeg"
              onChange={e=>handleFile(e.target.files[0])} />
            {file ? (
              <div className="flex items-center justify-center gap-2 text-sm text-indigo-600 dark:text-indigo-400 font-medium">
                <FileText size={18} />
                <span className="truncate max-w-[240px]">{file.name}</span>
              </div>
            ) : (
              <>
                <CloudUpload size={28} className="mx-auto text-slate-400 mb-2" />
                <p className="text-sm text-slate-500 dark:text-slate-400">Drag & drop or <span className="text-indigo-500 font-medium">browse</span></p>
                <p className="text-xs text-slate-400 mt-1">PDF, DOC, DOCX, PNG, JPG</p>
              </>
            )}
          </div>

          {/* Document name */}
          <div>
            <label className={lc}>Document Name *</label>
            <input required value={docName} onChange={e=>setDocName(e.target.value)}
              placeholder="e.g. Employment Contract – Jane Doe" className={ic} />
          </div>

          {/* Type + Employee */}
          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className={lc}>Document Type</label>
              <select value={docType} onChange={e=>setDocType(e.target.value)} className={ic}>
                {['contract','payslip','id','other'].map(t=><option key={t} className="capitalize">{t}</option>)}
              </select>
            </div>
            <div>
              <label className={lc}>Employee</label>
              <select value={employeeId} onChange={e=>setEmployeeId(e.target.value)} className={ic}>
                {employees.map(emp=><option key={emp.id} value={emp.id}>{emp.name}</option>)}
              </select>
            </div>
          </div>

          {/* Buttons */}
          <div className="flex gap-3 pt-1">
            <button type="button" onClick={onClose} className="flex-1 py-2.5 rounded-lg bg-slate-100 dark:bg-slate-800 text-slate-600 dark:text-slate-300 text-sm font-medium hover:bg-slate-200 dark:hover:bg-slate-700 transition-colors">Cancel</button>
            <button type="submit" className="flex-1 py-2.5 rounded-lg bg-indigo-600 hover:bg-indigo-700 text-white text-sm font-semibold transition-colors flex items-center justify-center gap-2">
              <Upload size={15} /> Upload
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

// ── Doc Row ────────────────────────────────────────────────────────────────

function DocRow({ doc }) {
  const style = TYPE_STYLES[doc.type] ?? TYPE_STYLES.other;

  const handleDownload = () => {
    if (doc.blobUrl) {
      const a = document.createElement('a');
      a.href = doc.blobUrl;
      a.download = doc.fileName || doc.name;
      a.click();
    } else {
      alert(`Simulated download: ${doc.name}`);
    }
  };

  return (
    <div className="flex items-center gap-4 px-5 py-3.5 hover:bg-slate-50 dark:hover:bg-slate-800/30 transition-colors">
      <span className="text-xl shrink-0 w-7 text-center">{style.icon}</span>
      <div className="flex-1 min-w-0">
        <p className="text-sm font-medium text-slate-800 dark:text-slate-200 truncate">{doc.name}</p>
        <p className="text-xs text-slate-400 mt-0.5 tabular-nums">{doc.uploadedAt} · {doc.size}</p>
      </div>
      <span className={cn('text-[10px] font-semibold px-2 py-0.5 rounded-full capitalize shrink-0', style.badge)}>{doc.type}</span>
      <button onClick={handleDownload}
        className="p-1.5 rounded-lg text-slate-400 hover:text-indigo-500 hover:bg-indigo-50 dark:hover:bg-indigo-900/20 transition-colors shrink-0" title="Download">
        <Download size={15} />
      </button>
    </div>
  );
}

// ── Main page ──────────────────────────────────────────────────────────────

export default function DocumentVault() {
  const { documents, addDocument, employees, currentUser } = useApp();
  const [search, setSearch]         = useState('');
  const [filterType, setFilterType] = useState('All');
  const [showUpload, setShowUpload] = useState(false);

  const isAdmin = currentUser.role === 'super_admin';

  const visibleDocs = isAdmin
    ? documents
    : documents.filter(d => d.employeeId === currentUser.id);

  const filtered = visibleDocs.filter(d =>
    d.name.toLowerCase().includes(search.toLowerCase()) &&
    (filterType === 'All' || d.type === filterType)
  );

  const ic = 'px-3 py-2 text-sm rounded-lg border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-900 text-slate-900 dark:text-slate-100 focus:outline-none focus:ring-2 focus:ring-indigo-500';

  return (
    <div className="space-y-5">

      {/* Security notice */}
      <div className="flex items-start gap-3 bg-amber-50 dark:bg-amber-900/20 border border-amber-200 dark:border-amber-800/60 rounded-xl p-4">
        <Lock size={16} className="text-amber-600 dark:text-amber-400 shrink-0 mt-0.5" />
        <p className="text-sm text-amber-700 dark:text-amber-300 leading-relaxed">
          Documents are stored locally in this session. Only authorized personnel can access employee files.
          {!isAdmin && ' You can only view your own documents.'}
        </p>
      </div>

      {/* Filter bar */}
      <div className="flex flex-wrap gap-3 items-center">
        <div className="relative flex-1 min-w-[200px]">
          <Search size={15} className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400 pointer-events-none" />
          <input value={search} onChange={e=>setSearch(e.target.value)} placeholder="Search documents…"
            className={cn(ic,'w-full pl-9 pr-4 placeholder-slate-400')} />
        </div>
        <select value={filterType} onChange={e=>setFilterType(e.target.value)} className={ic}>
          {['All','contract','payslip','id','other'].map(t=><option key={t} className="capitalize">{t}</option>)}
        </select>
        {isAdmin && (
          <button onClick={()=>setShowUpload(true)}
            className="flex items-center gap-1.5 px-3 py-2 bg-indigo-600 hover:bg-indigo-700 text-white text-xs font-semibold rounded-lg transition-colors shrink-0">
            <Upload size={14} /> Upload Document
          </button>
        )}
      </div>

      {/* Document list */}
      {isAdmin ? (
        <div className="space-y-4">
          {employees.map(emp => {
            const empDocs = filtered.filter(d => d.employeeId === emp.id);
            if (empDocs.length === 0) return null;
            return (
              <div key={emp.id} className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-xl overflow-hidden">
                <div className="flex items-center gap-3 px-5 py-3 border-b border-slate-100 dark:border-slate-800 bg-slate-50 dark:bg-slate-800/50">
                  <div className="w-7 h-7 rounded-full bg-indigo-500 flex items-center justify-center text-white text-xs font-bold shrink-0">{emp.name[0]}</div>
                  <div className="min-w-0">
                    <span className="text-sm font-semibold text-slate-700 dark:text-slate-300">{emp.name}</span>
                    <span className="text-xs text-slate-400 ml-2">{emp.title}</span>
                  </div>
                  <span className="ml-auto text-xs text-slate-400 shrink-0">{empDocs.length} file{empDocs.length!==1?'s':''}</span>
                </div>
                <div className="divide-y divide-slate-100 dark:divide-slate-800">
                  {empDocs.map(doc=><DocRow key={doc.id} doc={doc} />)}
                </div>
              </div>
            );
          })}
          {filtered.length === 0 && (
            <div className="flex flex-col items-center justify-center py-16 text-slate-400 gap-3">
              <FileText size={36} className="opacity-25" />
              <p className="text-sm">No documents yet. Click "Upload Document" to add one.</p>
            </div>
          )}
        </div>
      ) : (
        <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-xl overflow-hidden">
          {filtered.length === 0 ? (
            <div className="flex flex-col items-center justify-center py-16 text-slate-400 gap-3">
              <FileText size={36} className="opacity-25" />
              <p className="text-sm">No documents found</p>
            </div>
          ) : (
            <div className="divide-y divide-slate-100 dark:divide-slate-800">
              {filtered.map(doc=><DocRow key={doc.id} doc={doc} />)}
            </div>
          )}
        </div>
      )}

      {showUpload && (
        <UploadModal
          employees={employees}
          onClose={()=>setShowUpload(false)}
          onUploaded={doc=>{ addDocument(doc); setShowUpload(false); }}
        />
      )}
    </div>
  );
}
