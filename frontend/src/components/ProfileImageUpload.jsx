import { useState } from 'react';
import { uploadProfileImage, getAuthToken, getDeviceId, setAuthToken, setDeviceId, getUserId } from '../api';

export default function ProfileImageUpload() {
  const [userId, setUserIdLocal] = useState(getUserId() || ''); // preenche com userId salvo
  const [file, setFile] = useState(null);
  const [url, setUrl] = useState(null);
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);

  // credenciais locais (mostradas ao usuário)
  const [authToken, setAuthTokenLocal] = useState(getAuthToken() || '');
  const [deviceId, setDeviceIdLocal] = useState(getDeviceId() || '');
  const [savedMsg, setSavedMsg] = useState('');

  const salvarCredenciais = () => {
    setAuthToken(authToken || null);
    setDeviceId(deviceId || null);
    setSavedMsg('Credenciais salvas no localStorage');
    setTimeout(() => setSavedMsg(''), 2500);
  };

  const extrairUserIdDoToken = () => {
    try {
      if (!authToken) {
        setError('Token ausente para extrair payload.');
        return;
      }
      const payload = authToken.split('.')[1];
      if (!payload) throw new Error('Formato JWT inválido');
      // atob may require padding
      const b64 = payload.replace(/-/g, '+').replace(/_/g, '/');
      const json = JSON.parse(decodeURIComponent(escape(window.atob(b64))));
      // tenta localizar id, fallback para sub/email
      const id = json.id ?? json.sub ?? json.email ?? '';
      setUserId(String(id));
      setError(null);
    } catch (e) {
      setError('Falha ao decodificar token: ' + e.message);
    }
  };

  const enviar = async (e) => {
    e?.preventDefault?.();
    setError(null);
    setUrl(null);
    if (!userId || !file) {
      setError('userId e arquivo são obrigatórios');
      return;
    }

    // garante que as credenciais atuais estejam no localStorage antes do upload
    setAuthToken(authToken || null);
    setDeviceId(deviceId || null);

    setLoading(true);
    try {
      const res = await uploadProfileImage(userId, file);
      setUrl(res.url ?? res);
    } catch (e) {
      setError(e.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <section className="card">
      <h2>Upload Imagem de Perfil</h2>

      <div style={{ display: 'grid', gap: 8, marginBottom: 8 }}>
        <label style={{ fontSize: 12, color: '#666' }}>Token (Authorization)</label>
        <input
          value={authToken}
          onChange={e => setAuthTokenLocal(e.target.value)}
          placeholder="Bearer token ou apenas o JWT"
        />
        <label style={{ fontSize: 12, color: '#666' }}>Device id (X-Device-Id)</label>
        <input
          value={deviceId}
          onChange={e => setDeviceIdLocal(e.target.value)}
          placeholder="ex: web-1"
        />
        <div style={{ display: 'flex', gap: 8 }}>
          <button type="button" onClick={salvarCredenciais}>Salvar credenciais</button>
          <button type="button" onClick={extrairUserIdDoToken}>Preencher userId do token</button>
        </div>
        {savedMsg && <div style={{ color: 'green' }}>{savedMsg}</div>}
      </div>

      <form onSubmit={enviar} style={{ display: 'grid', gap: 8 }}>
        <input placeholder="user id" value={userId} onChange={e => setUserId(e.target.value)} />
        <input type="file" accept="image/*" onChange={e => setFile(e.target.files?.[0] ?? null)} />
        <button type="submit" disabled={loading}>{loading ? 'Enviando...' : 'Enviar'}</button>
      </form>

      <div style={{ marginTop: 8, fontSize: 13, color: '#666' }}>
        Observação: este endpoint exige Authorization (JWT) e X-Device-Id. Se você receber 401, verifique:
        <ol>
          <li>Se salvou o token acima (Salvar credenciais)</li>
          <li>Se o device id está configurado</li>
          <li>Se o token é válido / não expirou</li>
        </ol>
      </div>

      {error && <div style={{ color: 'red' }}>{error}</div>}
      {url && (
        <div style={{ marginTop: 8 }}>
          <div>URL: <a href={url} target="_blank" rel="noreferrer">{url}</a></div>
          <div style={{ marginTop: 8 }}><img src={url} alt="profile" style={{ maxWidth: 200 }} /></div>
        </div>
      )}
    </section>
  );
}
