import { useState } from 'react';
import { buscarUsuarioPorEmail } from '../api';

export default function UsuarioLookup() {
  const [email, setEmail] = useState('teste@ex.com');
  const [result, setResult] = useState(null);
  const [error, setError] = useState(null);

  const buscar = async (e) => {
    e.preventDefault();
    setError(null);
    setResult(null);
    try {
      const data = await buscarUsuarioPorEmail(email);
      setResult(data);
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <section className="card">
      <h2>Buscar Usuário por E-mail</h2>
      <form onSubmit={buscar}>
        <input value={email} onChange={(e) => setEmail(e.target.value)} placeholder="E-mail" />
        <button type="submit">Buscar</button>
      </form>

      {error && <div style={{ color: 'red' }}>{error}</div>}

      {result ? (
        <div style={{ textAlign: 'left', marginTop: 8 }}>
          <div><strong>ID:</strong> {result.id}</div>
          <div><strong>Nome:</strong> {result.nome ?? '(não informado)'}</div>
          <div><strong>E-mail:</strong> {result.email ?? '(não informado)'}</div>
          <div><strong>Status:</strong> {result.status}</div>
          <div>
            <strong>OAuth2:</strong>{' '}
            {result.oauth2 && result.oauth2 !== 'FALSE'
              ? result.oauth2
              : 'Nenhum'}
          </div>
        </div>
      ) : (
        <div style={{ marginTop: 8 }}>Nenhum resultado</div>
      )}
    </section>
  );
}
