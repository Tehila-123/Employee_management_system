import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

const FEATURE_GROUPS = [
  {
    category: 'Core HR', icon: '👥',
    items: [
      { title: 'Employee Profiles', desc: 'Central database for personal details, contact info, job titles, and emergency contacts.' },
      { title: 'Document Management', desc: 'Secure storage for contracts, IDs, tax forms, and certifications.' },
      { title: 'Onboarding & Offboarding', desc: 'Automated checklists for new hires and exit interviews for departing staff.' },
      { title: 'Organization Chart', desc: 'Visual representation of the company hierarchy and reporting structure.' },
    ],
  },
  {
    category: 'Time & Attendance', icon: '🕐',
    items: [
      { title: 'Digital Timecards', desc: 'Clock-in/out functionality with support for remote work or geo-fencing.' },
      { title: 'Leave Management', desc: 'Portal for employees to request PTO, sick leave, or maternity leave with manager approvals.' },
      { title: 'Holiday Calendar', desc: 'Automated tracking of regional and company-wide holidays.' },
    ],
  },
  {
    category: 'Performance & Talent', icon: '🎯',
    items: [
      { title: 'Performance Reviews', desc: 'Scheduled 360-degree feedback cycles, self-evaluations, and manager ratings.' },
      { title: 'Goal Tracking (OKRs)', desc: 'Set Key Results at the individual level that roll up to company objectives.' },
      { title: 'Skill Matrix', desc: 'Track employee competencies to identify skill gaps for future training.' },
    ],
  },
  {
    category: 'Financials & Payroll', icon: '💰',
    items: [
      { title: 'Payroll Processing', desc: 'Calculate gross-to-net pay based on hours worked and tax brackets.' },
      { title: 'Expense Claims', desc: 'Allow employees to upload receipts for reimbursement.' },
      { title: 'Bonus & Commission Tracking', desc: 'Manage variable pay based on performance data.' },
    ],
  },
  {
    category: 'Employee Self-Service', icon: '🙋',
    items: [
      { title: 'Personal Dashboard', desc: 'Home screen showing upcoming tasks, remaining leave balance, and company news.' },
      { title: 'Payslip Access', desc: 'Secure portal to download historical pay stubs and tax documents.' },
      { title: 'Internal Communication', desc: 'Notice board or integration with Slack/Teams for company-wide announcements.' },
    ],
  },
  {
    category: 'Security & Compliance', icon: '🔐',
    items: [
      { title: 'Role-Based Access Control', desc: 'Ensure employees cannot access sensitive data outside their permission level.' },
      { title: 'Audit Logs', desc: 'Full history of who changed what data and when for compliance tracking.' },
      { title: 'Reporting & Analytics', desc: 'Dashboards showing turnover rates, diversity metrics, and headcounts.' },
    ],
  },
];

const STATS = [
  { value: '10x',   label: 'Faster HR Workflows' },
  { value: '99.9%', label: 'Uptime Reliability'  },
  { value: '100%',  label: 'Secure & Audited'    },
];

export default function LandingPage() {
  const navigate = useNavigate();
  const [scrolled, setScrolled] = useState(false);
  const [menuOpen, setMenuOpen] = useState(false);

  useEffect(() => {
    const handler = () => setScrolled(window.scrollY > 20);
    window.addEventListener('scroll', handler);
    return () => window.removeEventListener('scroll', handler);
  }, []);

  // Close menu on resize
  useEffect(() => {
    const handler = () => { if (window.innerWidth > 768) setMenuOpen(false); };
    window.addEventListener('resize', handler);
    return () => window.removeEventListener('resize', handler);
  }, []);

  return (
    <div style={s.page}>

      {/* ── Navbar ── */}
      <nav style={{ ...s.nav, ...(scrolled ? s.navScrolled : {}) }}>
        <div style={s.navInner}>
          <div style={s.brand}>
            <span style={s.brandIcon}>⚡</span>
            <span style={s.brandName}>StaffEase</span>
          </div>

          {/* Desktop links */}
          <div style={s.navLinks}>
            <a href="#features" style={s.navLink}>Features</a>
            <a href="#about"    style={s.navLink}>About</a>
            <button style={s.btnOutline} onClick={() => navigate('/auth')}>Sign In</button>
            <button style={s.btnPrimary} onClick={() => navigate('/auth')}>Get Started</button>
          </div>

          {/* Hamburger */}
          <button
            style={s.hamburger}
            onClick={() => setMenuOpen(o => !o)}
            aria-label="Toggle menu"
          >
            <span style={s.bar} />
            <span style={s.bar} />
            <span style={s.bar} />
          </button>
        </div>

        {/* Mobile menu */}
        {menuOpen && (
          <div style={s.mobileMenu}>
            <a href="#features" style={s.mobileLink} onClick={() => setMenuOpen(false)}>Features</a>
            <a href="#about"    style={s.mobileLink} onClick={() => setMenuOpen(false)}>About</a>
            <button style={{ ...s.btnOutline, width: '100%', justifyContent: 'center' }} onClick={() => { navigate('/auth'); setMenuOpen(false); }}>Sign In</button>
            <button style={{ ...s.btnPrimary, width: '100%', justifyContent: 'center' }} onClick={() => { navigate('/auth'); setMenuOpen(false); }}>Get Started</button>
          </div>
        )}
      </nav>

      {/* ── Hero ── */}
      <section style={s.hero}>
        {/* Background orbs */}
        <div style={{ ...s.orb, ...s.orb1 }} />
        <div style={{ ...s.orb, ...s.orb2 }} />
        <div style={s.grid} />

        <div style={s.heroContent}>
          <div style={s.badge}>
            <span style={s.badgeDot} />
            StaffEase — Employee Management
          </div>

          <h1 style={s.heroTitle}>
            Manage Your Team<br />
            <span style={s.heroGradient}>Smarter & Faster</span>
          </h1>

          <p style={s.heroSub}>
            A modern, secure platform to handle employees, departments, leave requests,
            and audit logs — all in one place.
          </p>

          <div style={s.heroActions}>
            <button style={{ ...s.btnPrimary, ...s.btnLg }} onClick={() => navigate('/auth')}>
              Get Started Free
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5"><path d="M5 12h14M12 5l7 7-7 7"/></svg>
            </button>
            <button style={{ ...s.btnGhost, ...s.btnLg }} onClick={() => navigate('/dashboard')}>
              View Live Demo
            </button>
          </div>

          <div style={s.statsRow}>
            {STATS.map(st => (
              <div key={st.label} style={s.stat}>
                <span style={s.statValue}>{st.value}</span>
                <span style={s.statLabel}>{st.label}</span>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* ── Features ── */}
      <section id="features" style={s.featuresSection}>
        <div style={s.sectionInner}>
          <div style={s.sectionHeader}>
            <span style={s.tag}>Features</span>
            <h2 style={s.sectionTitle}>Everything you need to run HR</h2>
            <p style={s.sectionSub}>Built for modern teams that need speed, security, and simplicity.</p>
          </div>
          <div style={s.featuresGrid}>
            {FEATURE_GROUPS.map(group => (
              <FeatureCard key={group.category} group={group} />
            ))}
          </div>
        </div>
      </section>

      {/* ── CTA ── */}
      <section id="about" style={s.ctaSection}>
        <div style={s.ctaOrb} />
        <div style={s.ctaInner}>
          <span style={s.tag}>Ready to start?</span>
          <h2 style={s.ctaTitle}>Take control of your workforce today</h2>
          <p style={s.ctaSub}>Sign up in seconds. No credit card required.</p>
          <button style={{ ...s.btnPrimary, ...s.btnLg }} onClick={() => navigate('/auth')}>
            Create Free Account
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5"><path d="M5 12h14M12 5l7 7-7 7"/></svg>
          </button>
        </div>
      </section>

      {/* ── Footer ── */}
      <footer style={s.footer}>
        <div style={s.footerInner}>
          <div style={s.brand}>
            <span style={s.brandIcon}>⚡</span>
            <span style={s.brandName}>StaffEase</span>
          </div>
          <p style={s.footerCopy}>© {new Date().getFullYear()} StaffEase. All rights reserved.</p>
        </div>
      </footer>
    </div>
  );
}

function FeatureCard({ group }) {
  const [hovered, setHovered] = useState(false);
  return (
    <div
      style={{ ...s.featureCard, ...(hovered ? s.featureCardHover : {}) }}
      onMouseEnter={() => setHovered(true)}
      onMouseLeave={() => setHovered(false)}
    >
      <div style={s.featureCardHeader}>
        <span style={s.featureIcon}>{group.icon}</span>
        <h3 style={s.featureCategory}>{group.category}</h3>
      </div>
      <ul style={s.featureList}>
        {group.items.map(item => (
          <li key={item.title} style={s.featureItem}>
            <span style={s.checkBadge}>✓</span>
            <div>
              <span style={s.featureItemTitle}>{item.title}</span>
              <p style={s.featureItemDesc}>{item.desc}</p>
            </div>
          </li>
        ))}
      </ul>
    </div>
  );
}

/* ── All styles as JS objects — fully scoped, no global leakage ── */
const s = {
  page: {
    fontFamily: "'Inter', ui-sans-serif, system-ui, -apple-system, sans-serif",
    background: '#080c14',
    color: '#e2e8f0',
    minHeight: '100vh',
    overflowX: 'hidden',
    WebkitFontSmoothing: 'antialiased',
  },

  /* Navbar */
  nav: {
    position: 'fixed', top: 0, left: 0, right: 0, zIndex: 100,
    padding: '20px 0',
    transition: 'all 0.3s ease',
  },
  navScrolled: {
    background: 'rgba(8,12,20,0.88)',
    backdropFilter: 'blur(16px)',
    WebkitBackdropFilter: 'blur(16px)',
    borderBottom: '1px solid rgba(255,255,255,0.07)',
    padding: '14px 0',
  },
  navInner: {
    maxWidth: 1200, margin: '0 auto', padding: '0 32px',
    display: 'flex', alignItems: 'center', justifyContent: 'space-between',
  },
  brand: { display: 'flex', alignItems: 'center', gap: 10 },
  brandIcon: { fontSize: 22 },
  brandName: {
    fontSize: 20, fontWeight: 800,
    background: 'linear-gradient(135deg,#38bdf8,#818cf8)',
    WebkitBackgroundClip: 'text', WebkitTextFillColor: 'transparent',
    letterSpacing: '-0.5px',
  },
  navLinks: { display: 'flex', alignItems: 'center', gap: 8 },
  navLink: {
    color: '#94a3b8', textDecoration: 'none', fontSize: 14, fontWeight: 500,
    padding: '8px 14px', borderRadius: 8,
  },
  hamburger: {
    display: 'none', flexDirection: 'column', gap: 5,
    background: 'none', border: 'none', cursor: 'pointer', padding: 4,
  },
  bar: { display: 'block', width: 22, height: 2, background: '#94a3b8', borderRadius: 2 },
  mobileMenu: {
    display: 'flex', flexDirection: 'column', gap: 12,
    padding: '20px 24px',
    background: 'rgba(8,12,20,0.97)',
    borderBottom: '1px solid rgba(255,255,255,0.07)',
  },
  mobileLink: {
    color: '#94a3b8', textDecoration: 'none', fontSize: 15, fontWeight: 500,
    padding: '8px 0',
  },

  /* Buttons */
  btnPrimary: {
    display: 'inline-flex', alignItems: 'center', gap: 8,
    background: 'linear-gradient(135deg,#3b82f6,#6366f1)',
    color: '#fff', border: 'none', borderRadius: 10,
    fontSize: 14, fontWeight: 600, cursor: 'pointer',
    padding: '10px 20px', whiteSpace: 'nowrap',
    boxShadow: '0 4px 20px rgba(99,102,241,0.35)',
    transition: 'all 0.2s ease',
  },
  btnOutline: {
    display: 'inline-flex', alignItems: 'center', gap: 8,
    background: 'transparent', color: '#94a3b8',
    border: '1px solid rgba(255,255,255,0.12)',
    borderRadius: 10, fontSize: 14, fontWeight: 600,
    cursor: 'pointer', padding: '10px 20px', whiteSpace: 'nowrap',
    transition: 'all 0.2s ease',
  },
  btnGhost: {
    display: 'inline-flex', alignItems: 'center', gap: 8,
    background: 'rgba(255,255,255,0.06)', color: '#cbd5e1',
    border: '1px solid rgba(255,255,255,0.1)',
    borderRadius: 10, fontSize: 14, fontWeight: 600,
    cursor: 'pointer', padding: '10px 20px', whiteSpace: 'nowrap',
    transition: 'all 0.2s ease',
  },
  btnLg: { padding: '14px 28px', fontSize: 15, borderRadius: 12 },

  /* Hero */
  hero: {
    position: 'relative', minHeight: '100vh',
    display: 'flex', alignItems: 'center', justifyContent: 'center',
    textAlign: 'center', padding: '120px 32px 80px', overflow: 'hidden',
  },
  orb: {
    position: 'absolute', borderRadius: '50%',
    filter: 'blur(80px)', opacity: 0.18, pointerEvents: 'none',
  },
  orb1: {
    width: 600, height: 600,
    background: 'radial-gradient(circle,#6366f1,transparent 70%)',
    top: -100, left: -100,
  },
  orb2: {
    width: 500, height: 500,
    background: 'radial-gradient(circle,#38bdf8,transparent 70%)',
    bottom: -80, right: -80,
  },
  grid: {
    position: 'absolute', inset: 0, pointerEvents: 'none',
    backgroundImage: 'linear-gradient(rgba(255,255,255,0.03) 1px,transparent 1px),linear-gradient(90deg,rgba(255,255,255,0.03) 1px,transparent 1px)',
    backgroundSize: '60px 60px',
    maskImage: 'radial-gradient(ellipse 80% 80% at 50% 50%,black 40%,transparent 100%)',
  },
  heroContent: { position: 'relative', zIndex: 1, maxWidth: 780 },
  badge: {
    display: 'inline-flex', alignItems: 'center', gap: 8,
    background: 'rgba(99,102,241,0.12)', border: '1px solid rgba(99,102,241,0.3)',
    color: '#a5b4fc', fontSize: 13, fontWeight: 600,
    padding: '6px 16px', borderRadius: 100, marginBottom: 28, letterSpacing: '0.3px',
  },
  badgeDot: {
    width: 7, height: 7, background: '#6366f1', borderRadius: '50%',
    animation: 'pulse 2s infinite',
  },
  heroTitle: {
    fontSize: 'clamp(40px,6vw,72px)', fontWeight: 800,
    lineHeight: 1.1, letterSpacing: '-2px', color: '#f1f5f9',
    marginBottom: 24, marginTop: 0,
  },
  heroGradient: {
    background: 'linear-gradient(135deg,#38bdf8 0%,#818cf8 50%,#c084fc 100%)',
    WebkitBackgroundClip: 'text', WebkitTextFillColor: 'transparent',
  },
  heroSub: {
    fontSize: 18, color: '#64748b', lineHeight: 1.7,
    maxWidth: 560, margin: '0 auto 40px',
  },
  heroActions: {
    display: 'flex', gap: 14, justifyContent: 'center',
    flexWrap: 'wrap', marginBottom: 60,
  },
  statsRow: {
    display: 'flex', gap: 48, justifyContent: 'center', flexWrap: 'wrap',
    paddingTop: 40, borderTop: '1px solid rgba(255,255,255,0.07)',
  },
  stat: { textAlign: 'center' },
  statValue: {
    display: 'block', fontSize: 32, fontWeight: 800,
    background: 'linear-gradient(135deg,#38bdf8,#818cf8)',
    WebkitBackgroundClip: 'text', WebkitTextFillColor: 'transparent',
    letterSpacing: '-1px',
  },
  statLabel: { display: 'block', fontSize: 13, color: '#475569', marginTop: 4, fontWeight: 500 },

  /* Features */
  featuresSection: {
    padding: '100px 0',
    background: 'linear-gradient(180deg,#080c14 0%,#0d1220 100%)',
  },
  sectionInner: { maxWidth: 1200, margin: '0 auto', padding: '0 32px' },
  sectionHeader: { textAlign: 'center', marginBottom: 64 },
  tag: {
    display: 'inline-block',
    background: 'rgba(99,102,241,0.12)', border: '1px solid rgba(99,102,241,0.25)',
    color: '#a5b4fc', fontSize: 12, fontWeight: 700,
    textTransform: 'uppercase', letterSpacing: '1.2px',
    padding: '5px 14px', borderRadius: 100, marginBottom: 16,
  },
  sectionTitle: {
    fontSize: 'clamp(28px,4vw,44px)', fontWeight: 800,
    color: '#f1f5f9', letterSpacing: '-1px', marginBottom: 14, marginTop: 0,
  },
  sectionSub: { fontSize: 16, color: '#64748b', maxWidth: 480, margin: '0 auto', lineHeight: 1.7 },
  featuresGrid: {
    display: 'grid',
    gridTemplateColumns: 'repeat(auto-fit,minmax(320px,1fr))',
    gap: 24,
  },
  featureCard: {
    background: 'rgba(255,255,255,0.03)',
    border: '1px solid rgba(255,255,255,0.07)',
    borderRadius: 20, padding: '28px 32px',
    transition: 'all 0.3s ease', position: 'relative', overflow: 'hidden',
  },
  featureCardHover: {
    borderColor: 'rgba(99,102,241,0.35)',
    transform: 'translateY(-4px)',
    boxShadow: '0 20px 40px rgba(0,0,0,0.35)',
  },
  featureCardHeader: {
    display: 'flex', alignItems: 'center', gap: 12,
    marginBottom: 22, paddingBottom: 18,
    borderBottom: '1px solid rgba(255,255,255,0.07)',
  },
  featureIcon: { fontSize: 26 },
  featureCategory: { fontSize: 16, fontWeight: 700, color: '#f1f5f9', margin: 0, letterSpacing: '-0.3px' },
  featureList: { listStyle: 'none', padding: 0, margin: 0, display: 'flex', flexDirection: 'column', gap: 16 },
  featureItem: { display: 'flex', gap: 12, alignItems: 'flex-start' },
  checkBadge: {
    flexShrink: 0, width: 20, height: 20,
    background: 'rgba(99,102,241,0.15)', border: '1px solid rgba(99,102,241,0.3)',
    borderRadius: '50%', display: 'flex', alignItems: 'center', justifyContent: 'center',
    fontSize: 11, color: '#818cf8', marginTop: 1,
  },
  featureItemTitle: { display: 'block', fontSize: 14, fontWeight: 600, color: '#cbd5e1', marginBottom: 3 },
  featureItemDesc: { fontSize: 13, color: '#475569', lineHeight: 1.6, margin: 0 },

  /* CTA */
  ctaSection: {
    padding: '100px 32px', textAlign: 'center',
    position: 'relative', overflow: 'hidden',
  },
  ctaOrb: {
    position: 'absolute', width: 700, height: 700,
    background: 'radial-gradient(circle,rgba(99,102,241,0.15),transparent 70%)',
    borderRadius: '50%', top: '50%', left: '50%',
    transform: 'translate(-50%,-50%)', pointerEvents: 'none',
  },
  ctaInner: {
    position: 'relative', zIndex: 1, maxWidth: 600, margin: '0 auto',
    display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 20,
  },
  ctaTitle: {
    fontSize: 'clamp(28px,4vw,44px)', fontWeight: 800,
    color: '#f1f5f9', letterSpacing: '-1px', margin: 0,
  },
  ctaSub: { fontSize: 16, color: '#64748b', margin: 0 },

  /* Footer */
  footer: { borderTop: '1px solid rgba(255,255,255,0.07)', padding: 32 },
  footerInner: {
    maxWidth: 1200, margin: '0 auto',
    display: 'flex', alignItems: 'center', justifyContent: 'space-between',
    flexWrap: 'wrap', gap: 16,
  },
  footerCopy: { fontSize: 13, color: '#475569', margin: 0 },
};
