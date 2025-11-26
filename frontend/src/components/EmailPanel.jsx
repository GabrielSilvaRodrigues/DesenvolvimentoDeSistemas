import { useState } from 'react';
import { enviarEmail } from '../api';

const TOKEN_TYPES = ['CADASTRO','RECUPERACAO_SENHA','DENUNCIA','ALTERACAO_SENHA','AUTENTICACAO','LOGIN','OUTRO'];

export default function EmailPanel() {
  const [tipo, setTipo] = useState('LOGIN');
  const [tokenValor, setTokenValor] = useState('token123');
  const [para, setPara] = useState('');
  const [assunto, setAssunto] = useState('Assunto teste');
  const [fromAddress, setFromAddress] = useState('fatecmeets@gmail.com');
  const [fromName, setFromName] = useState('Fatec Meets');
  const [resp, setResp] = useState(null);
  const [err, setErr] = useState(null);

  const enviar = async (e) => {
    e.preventDefault();
    setErr(null);
    setResp(null);
    try {
      const data = await enviarEmail({ tipo, tokenValor, para, assunto, fromAddress, fromName });
      setResp(data);
    } catch (error) {
      setErr(error.message);
    }
  };

  return (
    <section className="card">
      <h2>Enviar E-mail</h2>
      <form onSubmit={enviar} style={{ display: 'grid', gap: 8 }}>
        <label>
          Tipo:
          <select value={tipo} onChange={e => setTipo(e.target.value)}>
            {TOKEN_TYPES.map(t => <option key={t} value={t}>{t}</option>)}
          </select>
        </label>
        <input value={tokenValor} onChange={e => setTokenValor(e.target.value)} placeholder="tokenValor" />
        <input value={para} onChange={e => setPara(e.target.value)} placeholder="destinatário (para)" />
        <input value={assunto} onChange={e => setAssunto(e.target.value)} placeholder="assunto" />
        <input value={fromAddress} onChange={e => setFromAddress(e.target.value)} placeholder="fromAddress" />
        <input value={fromName} onChange={e => setFromName(e.target.value)} placeholder="fromName" />
        <button type="submit">Enviar</button>
      </form>

      {err && <div style={{ color: 'red' }}>{err}</div>}
      {resp && <pre style={{ textAlign: 'left', marginTop: 8 }}>{JSON.stringify(resp, null, 2)}</pre>}
    </section>
  );
}
