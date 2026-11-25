const BASE=import.meta.env.VITE_API_URL||'/api';const TOKEN_KEY='jobradar-access-token';
export function getToken(){return localStorage.getItem(TOKEN_KEY)}
export function storeSession(d){localStorage.setItem(TOKEN_KEY,d.token);localStorage.setItem('jobradar-user',JSON.stringify({email:d.email,fullName:d.fullName,userId:d.userId}))}
export function clearSession(){localStorage.removeItem(TOKEN_KEY);localStorage.removeItem('jobradar-user')}
export function getCurrentUser(){try{return JSON.parse(localStorage.getItem('jobradar-user')||'null')}catch{return null}}
async function request(path,options={}){const headers={...(options.headers||{})};const token=getToken();if(token)headers.Authorization='Bearer '+token;if(options.body)headers['Content-Type']='application/json';const response=await fetch(BASE+path,{...options,headers});if(response.status===204)return null;if(!response.ok){let message='Request failed ('+response.status+')';try{const body=await response.json();message=body.message||body.error||message}catch{}if(response.status===401&&token){clearSession();window.location.reload()}throw new Error(message)}return response.json()}
export function registerAccount(payload){return request('/auth/register',{method:'POST',body:JSON.stringify(payload)})}
export function loginAccount(payload){return request('/auth/login',{method:'POST',body:JSON.stringify(payload)})}
export function fetchWorkspace(){return request('/workspace')}
export function saveFavorite(id){return request('/workspace/favorites/'+id,{method:'POST'})}
export function removeFavorite(id){return request('/workspace/favorites/'+id,{method:'DELETE'})}
export function updateApplication(id,status){return request('/workspace/applications/'+id,{method:'PUT',body:JSON.stringify({status})})}
export function removeApplication(id){return request('/workspace/applications/'+id,{method:'DELETE'})}
export function fetchMeta(){return request('/meta')}
export function fetchJobs(filters={}){const params=new URLSearchParams();Object.entries(filters).forEach(([k,v])=>{if(v!==''&&v!==null&&v!==undefined)params.set(k,v)});return request('/jobs?'+params.toString())}
export function triggerCollection(city,token){const params=new URLSearchParams();if(city)params.set('city',city);return request('/admin/collect?'+params.toString(),{method:'POST',headers:{'X-JobRadar-Admin-Token':token}})}
