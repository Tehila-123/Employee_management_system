import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { loginUser, registerUser, verifyOtp } from "../api/auth";
import { useApp } from "../context/AppContext";

function Field({ label, children }) {
  return (
    <div style={{ display: "flex", flexDirection: "column", gap: 6 }}>
      <label style={s.label}>{label}</label>
      {children}
    </div>
  );
}

export default function Auth() {
  const navigate = useNavigate();
  const { loginAs } = useApp();
  const [isLogin, setIsLogin] = useState(true);
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [otp, setOtp] = useState("");
  const [userId, setUserId] = useState(null);
  const [step, setStep] = useState("auth");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const reset = () => { setEmail(""); setPassword(""); setOtp(""); setUserId(null); setError(""); };
  const toggleMode = () => { setIsLogin(v => !v); reset(); setStep("auth"); };

  const handleAuth = async e => {
    e.preventDefault(); setError(""); setLoading(true);
    try {
      if (isLogin) {
        const res = await loginUser(email, password);
        if (res.requiresOtp) {
          setUserId(res.userId);
          setStep("otp");
        } else {
          loginAs(email, res.token);
          setStep("success");
        }
      } else {
        await registerUser({ email, passwordHash: password, roleId: 1 });
        setIsLogin(true);
        setError("Registration successful! Please sign in.");
        setPassword("");
      }
    } catch (err) {
      setError(err.message || "Something went wrong.");
    } finally { setLoading(false); }
  };

  const handleOtp = async e => {
    e.preventDefault(); setError(""); setLoading(true);
    try {
      const res = await verifyOtp(userId, otp);
      loginAs(email, res && res.token ? res.token : null);
      setStep("success");
    } catch (err) {
      setError(err.message || "Invalid OTP.");
    } finally { setLoading(false); }
  };

  if (step === "success") {
    return (
      <div style={s.page}>
        <div style={{ ...s.card, textAlign: "center" }}>
          <div style={s.successIcon}>&#10003;</div>
          <h2 style={s.successTitle}>Welcome Back!</h2>
          <p style={s.successSub}>You have signed in successfully.</p>
          <button style={s.btn} onClick={() => navigate("/dashboard")}>Go to Dashboard</button>
        </div>
      </div>
    );
  }

  return (
    <div style={s.page}>
      <button style={s.backBtn} onClick={() => navigate("/")}>Back to Home</button>
      <div style={s.card}>
        <div style={s.logoRow}>
          <span style={s.logoIcon}>&#9889;</span>
          <span style={s.logoName}>StaffEase</span>
        </div>
        <div style={s.header}>
          <h2 style={s.title}>
            {step === "otp" ? "Verify your identity" : isLogin ? "Welcome back" : "Create account"}
          </h2>
          <p style={s.subtitle}>
            {step === "otp" ? "Enter the 6-digit code sent to your email"
              : isLogin ? "Sign in to your StaffEase account"
              : "Get started with StaffEase today"}
          </p>
        </div>
        {error && <div style={error.includes("successful") ? s.msgSuccess : s.msgError}>{error}</div>}
        {step === "otp" ? (
          <form onSubmit={handleOtp} style={s.form}>
            <Field label="OTP Code">
              <input type="text" value={otp} onChange={e => setOtp(e.target.value)}
                placeholder="000000" required maxLength={6} style={s.input}
                onFocus={e => Object.assign(e.target.style, s.inputFocus)}
                onBlur={e => Object.assign(e.target.style, s.input)} />
            </Field>
            <button type="submit" style={s.btn} disabled={loading}>{loading ? "Verifying..." : "Verify Code"}</button>
            <button type="button" style={s.textBtn} onClick={() => setStep("auth")}>Back to sign in</button>
          </form>
        ) : (
          <form onSubmit={handleAuth} style={s.form}>
            <Field label="Email address">
              <input type="email" value={email} onChange={e => setEmail(e.target.value)}
                placeholder="you@company.com" required style={s.input}
                onFocus={e => Object.assign(e.target.style, s.inputFocus)}
                onBlur={e => Object.assign(e.target.style, s.input)} />
            </Field>
            <Field label="Password">
              <input type="password" value={password} onChange={e => setPassword(e.target.value)}
                placeholder="password" required style={s.input}
                onFocus={e => Object.assign(e.target.style, s.inputFocus)}
                onBlur={e => Object.assign(e.target.style, s.input)} />
            </Field>
            <button type="submit" style={s.btn} disabled={loading}>
              {loading ? "Processing..." : isLogin ? "Sign In" : "Create Account"}
            </button>
          </form>
        )}
        {step === "auth" && (
          <p style={s.toggleRow}>
            {isLogin ? "Don't have an account? " : "Already have an account? "}
            <span style={s.toggleLink} onClick={toggleMode}>{isLogin ? "Sign up" : "Sign in"}</span>
          </p>
        )}
      </div>
    </div>
  );
}

const s = {
  page: { minHeight: "100vh", width: "100%", display: "flex", alignItems: "center", justifyContent: "center", background: "linear-gradient(135deg,#0f172a 0%,#1e293b 100%)", fontFamily: "Inter,ui-sans-serif,system-ui,-apple-system,sans-serif", WebkitFontSmoothing: "antialiased", padding: 20, boxSizing: "border-box", position: "relative" },
  backBtn: { position: "fixed", top: 24, left: 24, background: "rgba(255,255,255,0.06)", border: "1px solid rgba(255,255,255,0.1)", color: "#94a3b8", fontSize: 13, fontWeight: 500, padding: "8px 16px", borderRadius: 8, cursor: "pointer", zIndex: 10, fontFamily: "inherit" },
  card: { background: "rgba(255,255,255,0.03)", backdropFilter: "blur(20px)", WebkitBackdropFilter: "blur(20px)", border: "1px solid rgba(255,255,255,0.1)", borderRadius: 24, padding: 40, width: "100%", maxWidth: 420, boxShadow: "0 25px 50px -12px rgba(0,0,0,0.5)", color: "#f8fafc" },
  logoRow: { display: "flex", alignItems: "center", justifyContent: "center", gap: 8, marginBottom: 28 },
  logoIcon: { fontSize: 24 },
  logoName: { fontSize: 20, fontWeight: 800, background: "linear-gradient(135deg,#38bdf8,#818cf8)", WebkitBackgroundClip: "text", WebkitTextFillColor: "transparent", letterSpacing: "-0.5px" },
  header: { textAlign: "center", marginBottom: 28 },
  title: { margin: "0 0 8px", fontSize: 26, fontWeight: 700, background: "linear-gradient(to right,#38bdf8,#818cf8)", WebkitBackgroundClip: "text", WebkitTextFillColor: "transparent" },
  subtitle: { margin: 0, color: "#94a3b8", fontSize: 14, lineHeight: 1.5 },
  msgError: { padding: "12px 16px", borderRadius: 10, fontSize: 14, marginBottom: 20, textAlign: "center", background: "rgba(239,68,68,0.1)", color: "#fca5a5", border: "1px solid rgba(239,68,68,0.2)" },
  msgSuccess: { padding: "12px 16px", borderRadius: 10, fontSize: 14, marginBottom: 20, textAlign: "center", background: "rgba(34,197,94,0.1)", color: "#86efac", border: "1px solid rgba(34,197,94,0.2)" },
  form: { display: "flex", flexDirection: "column", gap: 18 },
  label: { fontSize: 13, fontWeight: 500, color: "#cbd5e1" },
  input: { background: "rgba(15,23,42,0.5)", border: "1px solid rgba(255,255,255,0.1)", borderRadius: 12, padding: "13px 16px", color: "#f8fafc", fontSize: 15, outline: "none", width: "100%", boxSizing: "border-box", fontFamily: "inherit", transition: "all 0.2s ease" },
  inputFocus: { background: "rgba(15,23,42,0.8)", border: "1px solid #38bdf8", boxShadow: "0 0 0 3px rgba(56,189,248,0.2)", borderRadius: 12, padding: "13px 16px", color: "#f8fafc", fontSize: 15, outline: "none", width: "100%", boxSizing: "border-box", fontFamily: "inherit", transition: "all 0.2s ease" },
  btn: { background: "linear-gradient(135deg,#38bdf8 0%,#6366f1 100%)", color: "#fff", border: "none", borderRadius: 12, padding: 14, fontSize: 15, fontWeight: 600, cursor: "pointer", marginTop: 4, boxShadow: "0 4px 15px rgba(99,102,241,0.4)", fontFamily: "inherit", transition: "all 0.2s ease", width: "100%" },
  textBtn: { background: "none", border: "none", color: "#64748b", cursor: "pointer", fontSize: 14, fontFamily: "inherit", textAlign: "center", padding: "4px 0" },
  toggleRow: { marginTop: 24, textAlign: "center", fontSize: 14, color: "#94a3b8" },
  toggleLink: { color: "#38bdf8", cursor: "pointer", fontWeight: 600 },
  successIcon: { width: 64, height: 64, background: "linear-gradient(135deg,#22c55e,#16a34a)", borderRadius: "50%", display: "flex", alignItems: "center", justifyContent: "center", fontSize: 28, color: "#fff", margin: "0 auto 20px", boxShadow: "0 10px 25px rgba(34,197,94,0.4)" },
  successTitle: { fontSize: 24, fontWeight: 700, margin: "0 0 8px", color: "#f1f5f9" },
  successSub: { color: "#94a3b8", margin: "0 0 28px", fontSize: 14 },
};
