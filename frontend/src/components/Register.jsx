import { useState } from 'react';
import { setUserId } from '../api';

export default function Register() {
  const [nome, setNome] = useState('');
  const [email, setEmail] = useState('');
  const [senha, setSenha] = useState('');
  const [dispositivo, setDispositivo] = useState('web-1');
  const [ip, setIp] = useState('127.0.0.1');
  const [resp, setResp] = useState(null);
  const [err, setErr] = useState(null);

  const enviar = async (e) => {
    e.preventDefault();
    setErr(null);
    setResp(null);
    try {
      const res = await fetch('http://localhost:8080/auth/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ nome, email, senha, dispositivo, ip })
      });
      if (!res.ok) {
        const t = await res.text();
        throw new Error(`${res.status} ${t}`);
      }
      const data = await res.json();
      setResp(data);
      // salva user id retornado (quando presente)
      if (data?.id) setUserId(data.id);
    } catch (error) {
      setErr(error.message);
    }
  };

  return (
    <section className="card">
      <h2>Registrar Usuário (local)</h2>
      <form onSubmit={enviar} style={{ display: 'grid', gap: 8 }}>
        <input value={nome} onChange={e => setNome(e.target.value)} placeholder="Nome" />
        <input value={email} onChange={e => setEmail(e.target.value)} placeholder="E-mail" />
        <input value={senha} onChange={e => setSenha(e.target.value)} placeholder="Senha" type="password" />
        <input value={dispositivo} onChange={e => setDispositivo(e.target.value)} placeholder="Dispositivo" />
        <input value={ip} onChange={e => setIp(e.target.value)} placeholder="IP" />
        <button type="submit">Registrar</button>
      </form>

      {err && <div style={{ color: 'red' }}>{err}</div>}
      {resp && <pre style={{ textAlign: 'left', marginTop: 8 }}>{JSON.stringify(resp, null, 2)}</pre>}
    </section>
  );
}
