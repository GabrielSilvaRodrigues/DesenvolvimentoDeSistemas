import { useState } from 'react';
import { criarEndereco } from '../api';

export default function EnderecoForm() {
  const [cep, setCep] = useState('01001000');
  const [numero, setNumero] = useState('100');
  const [complemento, setComplemento] = useState('');
  const [result, setResult] = useState(null);
  const [error, setError] = useState(null);

  const enviar = async (e) => {
    e.preventDefault();
    setError(null);
    setResult(null);
    try {
      const data = await criarEndereco(cep.replace(/\D/g, ''), numero, complemento);
      setResult(data);
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <section className="card">
      <h2>Criar Endereço</h2>
      <form onSubmit={enviar} style={{ display: 'grid', gap: 8 }}>
        <input value={cep} onChange={(e) => setCep(e.target.value)} placeholder="CEP" />
        <input value={numero} onChange={(e) => setNumero(e.target.value)} placeholder="Número" />
        <input value={complemento} onChange={(e) => setComplemento(e.target.value)} placeholder="Complemento (opcional)" />
        <button type="submit">Criar</button>
      </form>

      {error && <div style={{ color: 'red' }}>{error}</div>}

      {result && (
        <div style={{ textAlign: 'left', marginTop: 8 }}>
          <div><strong>ID:</strong> {result.endereco?.id ?? result.id}</div>
          <div><strong>CEP:</strong> {result.endereco?.cep ?? result.cep}</div>
        </div>
      )}
    </section>
  );
}
