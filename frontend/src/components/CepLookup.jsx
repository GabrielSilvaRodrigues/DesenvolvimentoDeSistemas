import { useState } from 'react';
import { fetchCep } from '../api';

export default function CepLookup() {
  const [cep, setCep] = useState('01001000');
  const [result, setResult] = useState(null);
  const [error, setError] = useState(null);

  const buscar = async (e) => {
    e.preventDefault();
    setError(null);
    setResult(null);
    try {
      const data = await fetchCep(cep.replace(/\D/g, ''));
      setResult(data);
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <section className="card">
      <h2>Buscar CEP</h2>
      <form onSubmit={buscar}>
        <input value={cep} onChange={(e) => setCep(e.target.value)} placeholder="CEP" />
        <button type="submit">Buscar</button>
      </form>

      {error && <div style={{ color: 'red' }}>{error}</div>}

      {result && (
        <div style={{ textAlign: 'left', marginTop: 8 }}>
          <div><strong>CEP:</strong> {result.cep}</div>
          <div><strong>Logradouro:</strong> {result.logradouro}</div>
          <div><strong>Bairro:</strong> {result.bairro}</div>
          <div><strong>Cidade:</strong> {result.localidade} - {result.uf}</div>
        </div>
      )}
    </section>
  );
}
