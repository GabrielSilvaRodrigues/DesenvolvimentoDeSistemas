// ===============================
// CONFIG BASE
// ===============================
export const API_BASE = 'http://localhost:8080/api';


// ===============================
// HELPERS DE TOKEN / DEVICE
// ===============================

export function normalizeToken(token) {
  if (!token) return '';
  // remove prefixo "Bearer " caso o usuário tenha colado o header inteiro
  let t = token.trim();
  if (t.toLowerCase().startsWith('bearer ')) {
    t = t.substring(7).trim();
  }
  return t;
}

export function normalizeDevice(device) {
  if (!device) return '';
  return device
    .trim()
    .replace(/\u00A0/g, '')   // non-breaking space
    .replace(/\u2011/g, '-')  // hífen especial
    .replace(/\u2013/g, '-')
    .replace(/\u2014/g, '-')
    .toLowerCase();
}

// Salva token no localStorage
export function setAuthToken(token) {
  if (token) localStorage.setItem('auth_token', normalizeToken(token));
  else localStorage.removeItem('auth_token');
}

export function getAuthToken() {
  return normalizeToken(localStorage.getItem('auth_token'));
}

// Salva device
export function setDeviceId(deviceId) {
  if (deviceId) localStorage.setItem('device_id', normalizeDevice(deviceId));
  else localStorage.removeItem('device_id');
}

export function getDeviceId() {
  return normalizeDevice(localStorage.getItem('device_id'));
}

// Salva user id
export function setUserId(id) {
  if (id !== null && id !== undefined && id !== '') localStorage.setItem('user_id', String(id));
  else localStorage.removeItem('user_id');
}

export function getUserId() {
  return localStorage.getItem('user_id') || '';
}

// decode minimal JWT payload (returns object or null)
export function parseJwt(token) {
  try {
    const payload = token.split('.')[1];
    if (!payload) return null;
    const b64 = payload.replace(/-/g, '+').replace(/_/g, '/');
    // atob may throw; handle padding
    const jsonStr = decodeURIComponent(escape(window.atob(b64)));
    return JSON.parse(jsonStr);
  } catch (e) {
    return null;
  }
}

// salva user id (quando possível) a partir do token JWT
export function saveUserFromToken(token) {
  if (!token) return;
  const claims = parseJwt(token);
  if (!claims) return;
  // prioriza claim 'id', depois 'sub' (subject), depois 'email'
  const id = claims.id ?? claims.sub ?? claims.email;
  if (id) setUserId(id);
}

// ===============================
// HEADERS PADRÃO
// ===============================

function defaultHeaders(contentType) {
  const headers = {};
  const token = getAuthToken();
  const device = getDeviceId();

  if (token) headers['Authorization'] = `Bearer ${token}`;
  if (device) headers['X-Device-Id'] = device;
  if (contentType) headers['Content-Type'] = contentType;

  return headers;
}


// ===============================
// CEP VIA BACKEND
// ===============================
export async function fetchCep(cep) {
  const res = await fetch(`${API_BASE}/viacep/${encodeURIComponent(cep)}`, {
    headers: defaultHeaders(),
  });

  if (!res.ok) throw new Error(`Erro ao buscar CEP: ${res.status}`);
  return res.json();
}


// ===============================
// CRIAR ENDEREÇO
// ===============================
export async function criarEndereco(cep, numero, complemento) {
  const params = new URLSearchParams();
  params.append('cep', cep);
  params.append('numero', numero);
  if (complemento) params.append('complemento', complemento);

  const res = await fetch(`${API_BASE}/endereco`, {
    method: 'POST',
    headers: defaultHeaders('application/x-www-form-urlencoded'),
    body: params.toString(),
  });

  if (!res.ok) throw new Error(`Erro ao criar endereço: ${res.status}`);
  return res.json();
}


// ===============================
// BUSCAR USUÁRIO POR EMAIL
// ===============================
export async function buscarUsuarioPorEmail(email) {
  const res = await fetch(`${API_BASE}/usuario/email/${encodeURIComponent(email)}`, {
    headers: defaultHeaders(),
  });

  if (!res.ok) {
    if (res.status === 404) return null;
    throw new Error(`Erro ao buscar usuário: ${res.status}`);
  }

  return res.json();
}


// ===============================
// VALIDAR TOKEN  (GET)
// ===============================
export async function validateToken(tokenValue, device) {

  const safeToken = encodeURIComponent(normalizeToken(tokenValue));
  const safeDevice = encodeURIComponent(normalizeDevice(device));

  const url = `${API_BASE}/token/validar?token=${safeToken}&dispositivo=${safeDevice}`;

  const res = await fetch(url, {
    headers: defaultHeaders(),
  });

  if (!res.ok) {
    if (res.status === 404) return null;
    throw new Error(`Erro ao validar token: ${res.status}`);
  }

  return res.json();
}


// ===============================
// REVOGAR TOKEN (POST)
// ===============================
export async function revogarToken(tokenValue) {

  const safe = encodeURIComponent(normalizeToken(tokenValue));

  const res = await fetch(`${API_BASE}/token/revogar/${safe}`, {
    method: 'POST',
    headers: defaultHeaders(),
  });

  if (!res.ok) throw new Error(`Erro ao revogar token: ${res.status}`);
}


// ===============================
// ENVIAR EMAIL (POST + Query Params)
// ===============================
export async function enviarEmail(params) {
  const normalized = {};

  for (const key of Object.keys(params)) {
    normalized[key] = encodeURIComponent(params[key]);
  }

  const qs = new URLSearchParams(normalized).toString();

  const res = await fetch(`${API_BASE}/email/enviar?${qs}`, {
    method: 'POST',
    headers: defaultHeaders(),
  });

  if (!res.ok) throw new Error(`Erro ao enviar e-mail: ${res.status}`);
  return res.json();
}

// ===============================
// AUTENTICAR GOOGLE
// ===============================
export async function autenticarGoogle(body) {
  const res = await fetch(`${API_BASE}/google/auth`, {
    method: 'POST',
    headers: defaultHeaders('application/json'),
    body: JSON.stringify(body),
  });

  if (!res.ok) {
    const text = await res.text();
    throw new Error(`Erro Google auth: ${res.status} ${text}`);
  }

  return res.json();
}

// ===============================
// LOGOUT
// ===============================
export async function logout() {
  const token = getAuthToken();
  // envia token no body como application/x-www-form-urlencoded (requisição "simples" -> evita preflight)
  const params = new URLSearchParams();
  if (token) params.append('token', token);

  const res = await fetch('http://localhost:8080/auth/logout', {
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' }, // simple content-type
    body: params.toString(),
  });

  if (!res.ok && res.status !== 204) {
    const text = await res.text().catch(() => '');
    throw new Error(`Erro ao fazer logout: ${res.status} ${text}`);
  }

  // limpa local
  setAuthToken(null);
  setDeviceId(null);
}

// ===============================
// UPLOAD IMAGEM DE PERFIL (MULTIPART)
// ===============================
export async function uploadProfileImage(userId, file) {
  if (!userId) throw new Error('userId obrigatório');
  if (!file) throw new Error('file obrigatório');

  const form = new FormData();
  form.append('file', file);

  // não forçar Content-Type (fetch define o multipart boundary)
  const headers = defaultHeaders(); // defaultHeaders não inclui Content-Type se não passado
  // remove Content-Type caso exista por segurança
  delete headers['Content-Type'];

  const res = await fetch(`${API_BASE}/usuario/${encodeURIComponent(userId)}/profile-image`, {
    method: 'POST',
    headers,
    body: form
  });

  if (!res.ok) {
    const text = await res.text().catch(() => '');
    throw new Error(`Erro upload profile image: ${res.status} ${text}`);
  }

  return res.json();
}

// ===============================
// NOTIFICAÇÕES
// ===============================
export async function listarNotificacoes(userId) {
  if (!userId) throw new Error('userId obrigatório');
  const res = await fetch(`${API_BASE}/notificacao/user/${encodeURIComponent(userId)}`, {
    headers: defaultHeaders(),
  });
  if (!res.ok) throw new Error(`Erro ao listar notificações: ${res.status}`);
  return res.json();
}

export async function listarNotificacoesNaoLidas(userId) {
  if (!userId) throw new Error('userId obrigatório');
  const res = await fetch(`${API_BASE}/notificacao/user/${encodeURIComponent(userId)}/nao-lidas`, {
    headers: defaultHeaders(),
  });
  if (!res.ok) throw new Error(`Erro ao listar notificações não lidas: ${res.status}`);
  return res.json();
}

export async function criarNotificacao(userId, body) {
  const res = await fetch(`${API_BASE}/notificacao/user/${encodeURIComponent(userId)}`, {
    method: 'POST',
    headers: defaultHeaders('application/json'),
    body: JSON.stringify(body),
  });
  if (!res.ok) {
    const text = await res.text().catch(() => '');
    throw new Error(`Erro ao criar notificação: ${res.status} ${text}`);
  }
  return res.json();
}

export async function marcarNotificacaoLida(id) {
  const res = await fetch(`${API_BASE}/notificacao/${encodeURIComponent(id)}/lida`, {
    method: 'POST',
    headers: defaultHeaders(),
  });
  if (!res.ok) throw new Error(`Erro ao marcar notificação como lida: ${res.status}`);
}
