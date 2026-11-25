import { useCallback, useEffect, useMemo, useState } from 'react';
import {
  Activity, BriefcaseBusiness, MapPin, Search, SlidersHorizontal, Clock3,
  ExternalLink, RefreshCw, LocateFixed, WifiOff, Bookmark, BookmarkCheck,
  LayoutDashboard, ClipboardList, X, CheckCircle2, Filter
} from 'lucide-react';
import { fetchJobs, fetchMeta, triggerCollection, fetchWorkspace, fetchWorkspaceJobs, saveFavorite, removeFavorite, updateApplication, removeApplication } from './api';

const categories = [
  'All categories','IT & Software','DevOps & Cloud','IT Support','Business & Management',
  'Finance & Accounting','Sales & Marketing','Education','Healthcare','Manufacturing',
  'Logistics & Delivery','Engineering','Human Resources','Hospitality',
  'Retail & Customer Service','Construction','Legal','Design & Creative',
  'Customer Support','Operations','Other'
];
const statuses = ['Interested','Applied','Interview','Offer','Rejected'];
const STORE_KEY = 'jobradar-workspace-v1';
const age = value => {
  if (!value) return 'Posting date unavailable';
  const hours = Math.max(0, Math.floor((Date.now() - new Date(value).getTime()) / 3600000));
  return hours < 1 ? 'Recently' : hours < 24 ? hours + 'h ago' : Math.floor(hours / 24) + 'd ago';
};
function readWorkspace() {
  try { return JSON.parse(localStorage.getItem(STORE_KEY) || '{"saved":[],"tracking":{}}'); }
  catch { return { saved: [], tracking: {} }; }
}
export default function App() {
  const [jobs,setJobs] = useState([]);
  const [meta,setMeta] = useState(null);
  const [workspace,setWorkspace] = useState(readWorkspace);
  const [workspaceJobs,setWorkspaceJobs] = useState([]);
  const [view,setView] = useState('discover');
  const [keyword,setKeyword] = useState('');
  const [category,setCategory] = useState('');
  const [city,setCity] = useState('Pune');
  const [type,setType] = useState('');
  const [hours,setHours] = useState('');
  const [radius,setRadius] = useState('');
  const [coords,setCoords] = useState(null);
  const [loading,setLoading] = useState(false);
  const [error,setError] = useState('');
  const [locMsg,setLocMsg] = useState('');
  const [statusFilter,setStatusFilter] = useState('');
  const [minSalary,setMinSalary] = useState('');
  const [maxSalary,setMaxSalary] = useState('');
  const [page,setPage] = useState(0);
  const [scanning,setScanning] = useState(false);
  const [scanMessage,setScanMessage] = useState('');

  useEffect(() => { localStorage.setItem(STORE_KEY, JSON.stringify(workspace)); }, [workspace]);
  useEffect(() => { fetchWorkspace().then(data => setWorkspace({saved:data.saved||[],tracking:data.tracking||{}})).catch(e => setError('Could not load account workspace: '+e.message)); }, []);
  useEffect(() => { if(view==='saved'||view==='tracker') fetchWorkspaceJobs(view).then(setWorkspaceJobs).catch(e => setError('Could not load your saved jobs: '+e.message)); }, [view,workspace]);
  useEffect(() => { setPage(0); },[keyword,city,category,type,hours,radius,minSalary,maxSalary]);
  const filters = useMemo(() => ({
    keyword, city, category, employmentType:type, postedWithinHours:hours,
    minSalary, maxSalary, offset:page*20, limit:20,
    ...(coords && radius ? { latitude:coords.latitude, longitude:coords.longitude, radiusKm:radius } : {})
  }),[keyword,city,category,type,hours,coords,radius,minSalary,maxSalary,page]);
  const load = useCallback(async () => {
    setLoading(true); setError('');
    try { const [list,info] = await Promise.all([fetchJobs(filters),fetchMeta()]); setJobs(list); setMeta(info); }
    catch (e) { setError(e.message || 'Could not connect to JobRadar API.'); }
    finally { setLoading(false); }
  },[filters]);
  useEffect(() => { load(); },[load]);

  const savedIds = new Set(workspace.saved);
  const trackedCount = Object.keys(workspace.tracking).length;
  const visibleJobs = useMemo(() => {
    let result = view === 'saved' || view === 'tracker' ? workspaceJobs : jobs;
    if (view === 'tracker' && statusFilter) result = result.filter(job => workspace.tracking[job.id] === statusFilter);
    return result;
  },[jobs,workspaceJobs,view,workspace,statusFilter]);
  async function toggleSaved(id) {
    const exists=workspace.saved.includes(id);
    try { if(exists) await removeFavorite(id); else await saveFavorite(id); setWorkspace(old => ({...old,saved:exists?old.saved.filter(x=>x!==id):[...old.saved,id]})); }
    catch(e) { setError(e.message||'Could not update saved jobs.'); }
  }
  async function updateStatus(id,value) {
    try { if(value) await updateApplication(id,value); else await removeApplication(id); setWorkspace(old => { const tracking={...old.tracking}; if(!value) delete tracking[id]; else tracking[id]=value; return {...old,tracking}; }); }
    catch(e) { setError(e.message||'Could not update application status.'); }
  }
  async function scanCity() {
    const token = window.prompt('Enter the admin token configured in your local .env file.');
    if (!token) return;
    setScanning(true);
    setScanMessage('Scanning ' + (city || 'configured city') + '…');
    try {
      const result = await triggerCollection(city,token);
      setScanMessage('Scan completed for ' + result.city + ': ' + result.processed + ' listings processed.');
      setPage(0);
      await load();
    } catch (e) {
      setScanMessage(e.message || 'City scan failed. Check token and provider configuration.');
    } finally { setScanning(false); }
  }
  function formatSalary(job) {
    if (job.salaryMin == null && job.salaryMax == null) return null;
    const currency = job.salaryCurrency || 'INR';
    const fmt = value => new Intl.NumberFormat('en-IN',{style:'currency',currency,maximumFractionDigits:0}).format(value);
    if (job.salaryMin != null && job.salaryMax != null) return fmt(job.salaryMin) + ' – ' + fmt(job.salaryMax);
    return job.salaryMin != null ? 'From ' + fmt(job.salaryMin) : 'Up to ' + fmt(job.salaryMax);
  }
  function locate() {
    if (!navigator.geolocation) { setLocMsg('Browser location is unavailable.'); return; }
    navigator.geolocation.getCurrentPosition(({coords:c}) => {
      setCoords({latitude:c.latitude,longitude:c.longitude});
      setLocMsg('Your location is enabled. Choose a radius to filter jobs with available coordinates.');
    },() => setLocMsg('Location permission denied. City search is still available.'),{timeout:10000});
  }
  const title = view === 'saved' ? 'Saved opportunities' : view === 'tracker' ? 'Application tracker' : 'Latest opportunities';
  return <div className="shell">
    <aside className="sidebar">
      <div className="mark"><Activity/></div><div className="brand">JobRadar<span>AI</span></div>
      <p className="tagline">Opportunity, in one place.</p>
      <div className="side-label">WORKSPACE</div>
      <button className={'nav '+(view==='discover'?'active':'')} onClick={()=>setView('discover')}><LayoutDashboard size={17}/>Discover jobs</button>
      <button className={'nav '+(view==='saved'?'active':'')} onClick={()=>setView('saved')}><Bookmark size={17}/>Saved jobs <span className="nav-count">{workspace.saved.length}</span></button>
      <button className={'nav '+(view==='tracker'?'active':'')} onClick={()=>setView('tracker')}><ClipboardList size={17}/>Application tracker <span className="nav-count">{trackedCount}</span></button>
      <div className="side-note"><i/><div><b>Automatic discovery</b><small>Configured sources poll hourly</small></div></div>
      <div className="profile"><div className="avatar">JR</div><div><b>Personal workspace</b><small>Synced to your account</small></div></div>
    </aside>
    <main>
      <header className="header"><div><div className="eyebrow">JOB DISCOVERY / {view.toUpperCase()}</div><h1>Find your next opportunity.</h1><p>Explore jobs across industries, cities and nearby locations.</p></div><button className="refresh" onClick={load} disabled={loading}><RefreshCw size={15}/>Refresh</button></header>
      <section className="stats">
        <div className="stat"><span className="stat-icon blue"><BriefcaseBusiness size={17}/></span><small>Jobs in database</small><strong>{(meta?.totalJobs||0).toLocaleString()}</strong><em>Across connected sources</em></div>
        <div className="stat"><span className="stat-icon purple"><BookmarkCheck size={17}/></span><small>Saved opportunities</small><strong>{workspace.saved.length}</strong><em>Synced to your account</em></div>
        <div className="stat"><span className="stat-icon green"><ClipboardList size={17}/></span><small>Applications tracked</small><strong>{trackedCount}</strong><em>Interested through offer</em></div>
      </section>
      {view==='discover' && <section className="panel">
        <div className="panel-head"><div><h2>Explore opportunities</h2><p>Search by role, company, skill, industry or location.</p></div><span className="status"><i/>SOURCE CONNECTORS</span></div>
        <div className="search-row"><label className="search"><Search size={17}/><input value={keyword} onChange={e=>setKeyword(e.target.value)} placeholder="Job title, company or skill"/></label><label className="city"><MapPin size={16}/><input value={city} onChange={e=>setCity(e.target.value)} placeholder="City or area"/></label><button className="locate" onClick={locate}><LocateFixed size={15}/>Near me</button><button className="locate scan-button" onClick={scanCity} disabled={scanning}><RefreshCw size={15}/>{scanning ? "Scanning…" : "Scan city"}</button></div>
        {locMsg && <div className="loc-msg">{locMsg}</div>}
        {scanMessage && <div className="loc-msg scan-message">{scanMessage}</div>}
        <div className="filters">
          <label><SlidersHorizontal size={14}/><select value={category} onChange={e=>setCategory(e.target.value)}>{categories.map((c,i)=><option key={c} value={i===0?'':c}>{c}</option>)}</select></label>
          <label><select value={type} onChange={e=>setType(e.target.value)}><option value="">Any employment type</option>{['Full-time','Part-time','Contract','Internship','Temporary','Freelance'].map(x=><option key={x}>{x}</option>)}</select></label>
          <label><select value={hours} onChange={e=>setHours(e.target.value)}><option value="">Any posting date</option><option value="24">Last 24 hours</option><option value="72">Last 3 days</option><option value="168">Last 7 days</option><option value="720">Last 30 days</option></select></label>
          <label><select value={radius} onChange={e=>setRadius(e.target.value)} disabled={!coords}><option value="">Any distance</option>{[5,10,25,50,100].map(x=><option value={x} key={x}>Within {x} km</option>)}</select></label>
          <label className="salary-filter"><span>Min ₹</span><input type="number" min="0" value={minSalary} onChange={e=>setMinSalary(e.target.value)} placeholder="Min salary"/></label>
          <label className="salary-filter"><span>Max ₹</span><input type="number" min="0" value={maxSalary} onChange={e=>setMaxSalary(e.target.value)} placeholder="Max salary"/></label>
        </div>
      </section>}
      <section className="results">
        <div className="results-head"><div><h2>{title}</h2><p>{visibleJobs.length} listings shown · newest first</p></div>
          {view==='tracker' ? <label className="tracker-filter"><Filter size={13}/><select value={statusFilter} onChange={e=>setStatusFilter(e.target.value)}><option value="">All statuses</option>{statuses.map(s=><option key={s}>{s}</option>)}</select></label> : <span className="chip"><Activity size={13}/>Indexed jobs</span>}
        </div>
        {error ? <div className="empty error"><WifiOff/><b>Backend connection issue</b><p>{error}. Start Docker Compose and refresh.</p></div>
        : loading ? <div className="empty"><RefreshCw/>Loading opportunities…</div>
        : visibleJobs.length===0 ? <div className="empty"><Search/><b>{view==='saved'?'No saved jobs yet':view==='tracker'?'No applications tracked yet':'No matching jobs yet'}</b><p>{view==='discover'?'Connect an authorized job source in backend configuration to discover real vacancies.': 'Save a job or set its application status from Discover jobs. Your workspace is synced to your account.'}</p><button className="empty-action" onClick={()=>setView('discover')}>Explore jobs</button></div>
        : <div className="job-list">{visibleJobs.map(job=><article className="job" key={job.id}>
          <div className="company-icon">{(job.company||job.source||'J').slice(0,1).toUpperCase()}</div>
          <div className="job-main">
            <div className="job-top"><span className="category">{job.category||'Other'}</span><time>{age(job.postedAt||job.discoveredAt)}</time></div>
            <h3>{job.title}</h3><div className="company">{job.company||'Company not provided'}</div>
            <div className="details"><span><MapPin size={13}/>{job.location||job.city||'Location not provided'}</span>{job.employmentType&&<span>{job.employmentType}</span>}{job.experienceLevel&&<span>{job.experienceLevel}</span>}{job.distanceKm!=null&&<span>{job.distanceKm.toFixed(1)} km away</span>}</div>
            <p className="description">{job.description||'No description provided by source.'}</p>{formatSalary(job)&&<div className="salary-value">{formatSalary(job)}</div>}
            <div className="job-foot">{job.source==='Adzuna'?<a className="adzuna-attribution" href="https://www.adzuna.in/" target="_blank" rel="noreferrer">Adzuna</a>:job.source==='RemoteOK'?<a className="source-attribution" href="https://remoteok.com/" target="_blank" rel="noreferrer">Remote OK</a>:job.source==='Arbeitnow'?<a className="source-attribution" href="https://www.arbeitnow.com/" target="_blank" rel="noreferrer">Arbeitnow</a>:<small>SOURCE · {job.source}</small>}
              {workspace.tracking[job.id]&&<span className="tracked-label">{workspace.tracking[job.id]}</span>}
            </div>
          </div>
          <div className="job-actions">
            <button className={'save-job '+(savedIds.has(job.id)?'saved':'')} title={savedIds.has(job.id)?'Remove saved job':'Save job'} onClick={()=>toggleSaved(job.id)}>{savedIds.has(job.id)?<BookmarkCheck size={15}/>:<Bookmark size={15}/>}</button>
            <select className="status-select" aria-label="Application status" value={workspace.tracking[job.id]||''} onChange={e=>updateStatus(job.id,e.target.value)}><option value="">Track status</option>{statuses.map(s=><option key={s}>{s}</option>)}</select>
            <a className="apply" href={job.applyUrl||'#'} target="_blank" rel="noreferrer">Apply <ExternalLink size={14}/></a>
          </div>
        </article>)}</div>}
        {!error && view==='discover' && <div className="pagination">
          <button className="locate" onClick={()=>setPage(p=>Math.max(0,p-1))} disabled={page===0 || loading}>Previous</button>
          <span>Page {page+1}</span>
          <button className="locate" onClick={()=>setPage(p=>p+1)} disabled={jobs.length<20 || loading}>Next</button>
        </div>}
      </section>
      <footer>JobRadar AI · Discover responsibly across every category.<span>Listings: {meta?.totalJobs??'—'} · Account workspace</span></footer>
    </main>
  </div>;
}
