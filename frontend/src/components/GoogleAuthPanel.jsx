import { useState } from 'react';
import { autenticarGoogle, setAuthToken, setUserId } from '../api';
import GoogleHandle from './GoogleHandle'; // novo: botão para OAuth popup

export default function GoogleAuthPanel() {
  const [googleId, setGoogleId] = useState('google-123');
  const [email, setEmail] = useState('');
  const [name, setName] = useState('');
  const [picture, setPicture] = useState('');
  const [locale, setLocale] = useState('');
  const [dispositivo, setDispositivo] = useState('web-1');
  const [ip, setIp] = useState('127.0.0.1');
  const [resp, setResp] = useState(null);
  const [err, setErr] = useState(null);

  const enviar = async (e) => {
    e.preventDefault();
    setErr(null);
    setResp(null);
    try {
      const body = { googleId, email, name, picture, locale, dispositivo, ip };
      const data = await autenticarGoogle(body);
      // se API retornar TokenEntity, salva token + user id (quando presente)
      if (data?.valor) {
        setAuthToken(data.valor);
        try { setUserId(data.usuario?.id ?? data.usuario?.id); } catch {}
        setResp({ tokenPrefix: data.valor.substring?.(0,16), userId: data.usuario?.id });
      } else {
        setResp(data);
      }
    } catch (error) {
      setErr(error.message);
    }
  };

  return (
    <section className="card">
      <h2>Autenticar via Google</h2>

      {/* OAuth2 real via popup */}
      <div style={{ marginBottom: 12 }}>
        <GoogleHandle onSuccess={({ token, device }) => {
          // opcional: mostrar confirmação quando recebido via postMessage
          setResp({ tokenPrefix: token?.substring?.(0,16), device });
          setErr(null);
        }} />
      </div>

      <hr />

      {/* Fallback: simulador (mantido) */}
      <h4>Simulação (fallback)</h4>
      <form onSubmit={enviar} style={{ display: 'grid', gap: 8 }}>
        <input value={googleId} onChange={e => setGoogleId(e.target.value)} placeholder="googleId" />
        <input value={email} onChange={e => setEmail(e.target.value)} placeholder="email" />
        <input value={name} onChange={e => setName(e.target.value)} placeholder="name" />
        <input value={picture} onChange={e => setPicture(e.target.value)} placeholder="picture (url)" />
        <input value={locale} onChange={e => setLocale(e.target.value)} placeholder="locale" />
        <input value={dispositivo} onChange={e => setDispositivo(e.target.value)} placeholder="dispositivo" />
        <input value={ip} onChange={e => setIp(e.target.value)} placeholder="ip" />
        <button type="submit">Autenticar (simulado)</button>
      </form>

      {err && <div style={{ color: 'red' }}>{err}</div>}
      {resp && <pre style={{ textAlign: 'left', marginTop: 8 }}>{JSON.stringify(resp, null, 2)}</pre>}
    </section>
  );
}
