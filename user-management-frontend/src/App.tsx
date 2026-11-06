import { useCallback, useEffect, useState, type FormEvent } from 'react';
import { api, type AuthSession, type User, type UserInput, type UserPage } from './api/backend';
import './App.css';

const SESSION_KEY = 'user-management-demo-auth';
const blank: UserInput = { username: '', email: '', firstName: '', lastName: '', phone: '', age: null, password: '' };

function savedSession(): AuthSession | null {
  try { const data = sessionStorage.getItem(SESSION_KEY); return data ? JSON.parse(data) as AuthSession : null; }
  catch { return null; }
}

function App() {
  const [session, setSession] = useState<AuthSession | null>(savedSession);
  const [mode, setMode] = useState<'login' | 'register'>('login');
  const [identifier, setIdentifier] = useState('');
  const [password, setPassword] = useState('');
  const [signup, setSignup] = useState({ username: '', email: '', firstName: '', lastName: '', phone: '', password: '' });
  const [me, setMe] = useState<User | null>(null);
  const [users, setUsers] = useState<UserPage | null>(null);
  const [keyword, setKeyword] = useState('');
  const [role, setRole] = useState('');
  const [status, setStatus] = useState('');
  const [applied, setApplied] = useState({ keyword: '', role: '', status: '' });
  const [page, setPage] = useState(0);
  const [editor, setEditor] = useState<User | 'new' | null>(null);
  const [userForm, setUserForm] = useState<UserInput>(blank);
  const [error, setError] = useState('');
  const [notice, setNotice] = useState('');
  const [busy, setBusy] = useState(false);
  const admin = session?.role === 'ADMIN';

  const logout = useCallback(() => {
    setSession(null); setMe(null); setUsers(null);
    sessionStorage.removeItem(SESSION_KEY);
  }, []);

  const load = useCallback(async () => {
    if (!session) return;
    try {
      const current = await api.me(session.token);
      setMe(current);
      if (session.role === 'ADMIN') {
        const params = new URLSearchParams({ page: String(page), size: '8', sortBy: 'username', sortDir: 'asc' });
        if (applied.keyword) params.set('keyword', applied.keyword);
        if (applied.role) params.set('role', applied.role);
        if (applied.status) params.set('active', applied.status);
        setUsers(await api.list(session.token, params));
      }
    } catch (err) { setError((err as Error).message); }
  }, [session, page, applied]);

  useEffect(() => { void load(); }, [load]);

  async function authenticate(event: FormEvent) {
    event.preventDefault(); setBusy(true); setError('');
    try {
      const next = mode === 'login'
        ? await api.login(identifier, password)
        : await api.register(signup);
      sessionStorage.setItem(SESSION_KEY, JSON.stringify(next));
      setSession(next); setNotice('Welcome, ' + next.username + '.');
    } catch (err) { setError((err as Error).message); }
    finally { setBusy(false); }
  }

  function openEditor(user?: User) {
    setError('');
    setEditor(user || 'new');
    setUserForm(user ? {
      username: user.username, email: user.email, firstName: user.firstName,
      lastName: user.lastName || '', phone: user.phone || '', age: user.age, password: '',
    } : { ...blank });
  }

  async function saveUser(event: FormEvent) {
    event.preventDefault(); if (!session) return;
    setBusy(true); setError('');
    try {
      if (editor && editor !== 'new') await api.update(session.token, editor.id, userForm);
      else await api.create(session.token, userForm);
      setEditor(null); setNotice('User saved successfully.');
      await load();
    } catch (err) { setError((err as Error).message); }
    finally { setBusy(false); }
  }

  async function toggle(user: User) {
    if (!session) return;
    setError('');
    try {
      await api.setActive(session.token, user.id, !user.active);
      setNotice('Account status updated.'); await load();
    } catch (err) { setError((err as Error).message); }
  }

  async function remove(user: User) {
    if (!session || !confirm('Soft-delete ' + user.username + '?')) return;
    setError('');
    try {
      await api.remove(session.token, user.id);
      setNotice('Account soft-deleted.'); await load();
    } catch (err) { setError((err as Error).message); }
  }

  return (
    <div className="app">
      <div className="ambient ambient-one" /><div className="ambient ambient-two" />
      <header className="topbar">
        <a className="brand" href="/" aria-label="User Management home">
          <span className="brand-mark">UM</span><span>User <strong>Management</strong><small>ENGINEERING DEMO</small></span>
        </a>
        {session && <div className="account"><span className="account-role">{session.role}</span><span className="account-name">{session.username}</span><button className="ghost small" onClick={logout}>Sign out</button></div>}
        {!session && <span className="top-hint">Spring Boot · MongoDB · React</span>}
      </header>
      <main className="shell">
        {!session ? (
          <section className="auth-layout">
            <div className="hero">
              <div className="eyebrow">IDENTITY & ACCESS · DEMONSTRATION</div>
              <h1>Manage users.<br/><em>Securely, by design.</em></h1>
              <p>A practical full-stack reference for JWT authentication, role-based access control, searchable users, and MongoDB persistence.</p>
              <div className="hero-chips"><span>JWT / BCrypt</span><span>Spring Security</span><span>MongoDB</span><span>React 19</span></div>
              <div className="illustration" aria-hidden="true"><div className="illustration-card"><span>✓</span> Authentication ready</div><div className="illustration-card indent"><span>✓</span> Permission-aware access</div><div className="illustration-card"><span>✓</span> Structured user records</div></div>
            </div>
            <div className="panel auth-card">
              <div className="eyebrow">ACCOUNT ACCESS</div>
              <h2>{mode === 'login' ? 'Welcome back' : 'Create an account'}</h2>
              <p className="muted">{mode === 'login' ? 'Sign in using your username or email.' : 'New accounts receive the USER role.'}</p>
              <div className="tabs"><button className={mode === 'login' ? 'selected' : ''} onClick={() => setMode('login')}>Sign in</button><button className={mode === 'register' ? 'selected' : ''} onClick={() => setMode('register')}>Register</button></div>
              <form onSubmit={authenticate} className="form-grid">
                {mode === 'login' ? <>
                  <label>Username or email<input required value={identifier} onChange={e => setIdentifier(e.target.value)} placeholder="your username" /></label>
                  <label>Password<input required type="password" value={password} onChange={e => setPassword(e.target.value)} placeholder="••••••••" /></label>
                </> : <>
                  <label>Username<input required minLength={3} value={signup.username} onChange={e => setSignup({...signup, username:e.target.value})}/></label>
                  <label>Email<input required type="email" value={signup.email} onChange={e => setSignup({...signup, email:e.target.value})}/></label>
                  <label>First name<input required value={signup.firstName} onChange={e => setSignup({...signup, firstName:e.target.value})}/></label>
                  <label>Last name<input value={signup.lastName} onChange={e => setSignup({...signup, lastName:e.target.value})}/></label>
                  <label>Phone (optional)<input value={signup.phone} onChange={e => setSignup({...signup, phone:e.target.value})}/></label>
                  <label>Password (8+ characters)<input required minLength={8} type="password" value={signup.password} onChange={e => setSignup({...signup, password:e.target.value})}/></label>
                </>}
                <button className="primary" disabled={busy}>{busy ? 'Working…' : mode === 'login' ? 'Sign in' : 'Create account'}</button>
              </form>
              <p className="subtle">This is a portfolio demo, not a hosted production identity provider.</p>
            </div>
          </section>
        ) : (
          <div className="dashboard">
            <div className="dashboard-heading"><div><div className="eyebrow">CONTROL CENTER</div><h1>Overview <span className="spark">✦</span></h1><p className="muted">Signed in as {me?.firstName || session.username} · {admin ? 'Administrator' : 'Standard user'}</p></div>{admin && <button className="primary" onClick={() => openEditor()}>+ Add user</button>}</div>
            {notice && <div className="notice" role="status">{notice}<button onClick={()=>setNotice('')}>×</button></div>}
            {error && <div className="error" role="alert">{error}<button onClick={()=>setError('')}>×</button></div>}
            <div className="stats">
              <article className="stat panel"><span>ACCOUNT ROLE</span><strong>{session.role}</strong><small>Assigned via role-based access</small></article>
              <article className="stat panel"><span>{admin ? 'TOTAL USERS' : 'YOUR ACCOUNT'}</span><strong>{admin ? (users?.totalItems ?? '—') : 'Active'}</strong><small>{admin ? 'Matches current filters' : 'Authenticated via JWT'}</small></article>
              <article className="stat panel"><span>PLATFORM</span><strong>MongoDB</strong><small>Document-based persistence</small></article>
            </div>
            {admin ? <section className="panel directory">
              <div className="section-title"><div><div className="eyebrow">DIRECTORY</div><h2>User accounts</h2><p className="muted">Filter, update, deactivate, and soft-delete accounts.</p></div></div>
              <form className="filters" onSubmit={e => {e.preventDefault();setPage(0);setApplied({keyword,role,status});}}>
                <input aria-label="Search users" value={keyword} onChange={e=>setKeyword(e.target.value)} placeholder="Search name, email, username…" />
                <select aria-label="Role" value={role} onChange={e=>setRole(e.target.value)}><option value="">All roles</option><option value="ADMIN">Admin</option><option value="USER">User</option></select>
                <select aria-label="Account status" value={status} onChange={e=>setStatus(e.target.value)}><option value="">All statuses</option><option value="true">Active</option><option value="false">Inactive</option></select>
                <button className="primary" type="submit">Apply</button>
              </form>
              <div className="table-scroll"><table><thead><tr><th>USER</th><th>EMAIL</th><th>ROLE</th><th>STATUS</th><th>ACTIONS</th></tr></thead>
              <tbody>{(users?.users || []).map(u => <tr key={u.id}><td><div className="user-cell"><span className="avatar">{(u.firstName || u.username).slice(0,2).toUpperCase()}</span><div><strong>{u.firstName} {u.lastName || ''}</strong><small>@{u.username}</small></div></div></td><td>{u.email}</td><td><span className="tag">{u.role}</span></td><td><span className={'status ' + (u.active ? 'on' : 'off')}>{u.active ? 'Active' : 'Inactive'}</span></td><td><div className="actions"><button onClick={()=>openEditor(u)}>Edit</button><button onClick={()=>void toggle(u)}>{u.active ? 'Disable' : 'Enable'}</button><button className="danger" onClick={()=>void remove(u)}>Delete</button></div></td></tr>)}</tbody></table>{users?.users.length===0 && <p className="empty">No accounts match these filters.</p>}</div>
              <div className="table-footer"><span>{users?.totalItems ?? 0} accounts · page {page+1}</span><div className="pager"><button disabled={page===0} onClick={()=>setPage(p=>Math.max(0,p-1))}>Previous</button><button disabled={!users?.hasNext} onClick={()=>setPage(p=>p+1)}>Next</button></div></div>
            </section> : <div className="panel member"><div className="eyebrow">YOUR PROFILE</div><h2>{me?.firstName} {me?.lastName}</h2><p className="muted">{me?.email}</p><p>You've signed in successfully. Directory management requires an administrator role. To demo the admin UI, initialize an administrator via the documented bootstrap environment variables.</p></div>}
          </div>
        )}
      </main>
      <footer><span>Designed & developed by Shubham Kumar</span><span>Portfolio demonstration · Not for production use</span></footer>
      {editor && <div className="modal-cover" role="presentation" onMouseDown={e => {if (e.target===e.currentTarget) setEditor(null);}}><section className="modal panel" role="dialog" aria-modal="true" aria-label="Edit user"><div className="modal-heading"><div><div className="eyebrow">ADMIN ACTION</div><h2>{editor==='new'?'Create user':'Edit user'}</h2></div><button className="close" onClick={()=>setEditor(null)}>×</button></div><form className="form-grid two-col" onSubmit={saveUser}>
        <label>Username<input required value={userForm.username} onChange={e=>setUserForm({...userForm,username:e.target.value})}/></label>
        <label>Email<input required type="email" value={userForm.email} onChange={e=>setUserForm({...userForm,email:e.target.value})}/></label>
        <label>First name<input required value={userForm.firstName} onChange={e=>setUserForm({...userForm,firstName:e.target.value})}/></label>
        <label>Last name<input value={userForm.lastName} onChange={e=>setUserForm({...userForm,lastName:e.target.value})}/></label>
        <label>Phone<input value={userForm.phone} onChange={e=>setUserForm({...userForm,phone:e.target.value})}/></label>
        <label>Age<input type="number" min={18} max={120} value={userForm.age ?? ''} onChange={e=>setUserForm({...userForm,age:e.target.value?Number(e.target.value):null})}/></label>
        <label className="wide">Password {editor==='new'?'(required)':'(leave blank to keep current)'}<input type="password" required={editor==='new'} minLength={8} value={userForm.password||''} onChange={e=>setUserForm({...userForm,password:e.target.value})}/></label>
        <div className="modal-buttons wide"><button type="button" className="ghost" onClick={()=>setEditor(null)}>Cancel</button><button className="primary" disabled={busy}>{busy?'Saving…':'Save user'}</button></div>
      </form>{error && <div className="error">{error}</div>}</section></div>}
    </div>
  );
}
export default App;
