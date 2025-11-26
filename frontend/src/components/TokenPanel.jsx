import { useState } from 'react';
import { validateToken, revogarToken, setAuthToken, setDeviceId } from '../api';

export default function TokenPanel() {
  const [token, setToken] = useState('');
  const [device, setDevice] = useState(localStorage.getItem('device_id') || 'web-1');
  const [result, setResult] = useState(null);
  const [error, setError] = useState(null);

  const validar = async (e) => {
    e?.preventDefault();
    setError(null);
    setResult(null);
    try {
      const data = await validateToken(token, device);
      setResult(data);
      // opcional: guardar token/device localmente para chamadas subsequentes
      if (data) {
        setAuthToken(token);
        setDeviceId(device);
      }
    } catch (err) {
      setError(err.message);
    }
  };

  const revogar = async () => {
    setError(null);
    try {
      await revogarToken(token);
      setResult({ revoked: true });
      // limpa auth local
      setAuthToken(null);
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <section className="card">
      <h2>Token</h2>
      <form onSubmit={validar} style={{ display: 'grid', gap: 8 }}>
        <input value={token} onChange={(e) => setToken(e.target.value)} placeholder="token" />
        <input value={device} onChange={(e) => setDevice(e.target.value)} placeholder="device id" />
        <div style={{ display: 'flex', gap: 8 }}>
          <button type="submit">Validar</button>
          <button type="button" onClick={revogar}>Revogar</button>
        </div>
      </form>

      {error && <div style={{ color: 'red' }}>{error}</div>}
      {result && (
        <pre style={{ textAlign: 'left', marginTop: 8 }}>{JSON.stringify(result, null, 2)}</pre>
      )}
    </section>
  );
}
