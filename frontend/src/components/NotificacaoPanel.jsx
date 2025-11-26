import { useState } from 'react';
import { listarNotificacoes, listarNotificacoesNaoLidas, criarNotificacao, marcarNotificacaoLida } from '../api';

export default function NotificacaoPanel() {
  const [userId, setUserId] = useState('');
  const [notifs, setNotifs] = useState(null);
  const [naoLidas, setNaoLidas] = useState(null);
  const [error, setError] = useState(null);
  const [titulo, setTitulo] = useState('');
  const [mensagem, setMensagem] = useState('');
  const [tipo, setTipo] = useState('INFO');

  const carregar = async () => {
    setError(null);
    setNotifs(null);
    try {
      const data = await listarNotificacoes(userId);
      setNotifs(data);
    } catch (e) {
      setError(e.message);
    }
  };

  const carregarNaoLidas = async () => {
    setError(null);
    setNaoLidas(null);
    try {
      const data = await listarNotificacoesNaoLidas(userId);
      setNaoLidas(data);
    } catch (e) {
      setError(e.message);
    }
  };

  const criar = async (e) => {
    e?.preventDefault?.();
    setError(null);
    try {
      const created = await criarNotificacao(userId, { tipo, titulo, mensagem });
      setTitulo('');
      setMensagem('');
      await carregar();
      await carregarNaoLidas();
    } catch (e) {
      setError(e.message);
    }
  };

  const marcar = async (id) => {
    setError(null);
    try {
      await marcarNotificacaoLida(id);
      await carregar();
      await carregarNaoLidas();
    } catch (e) {
      setError(e.message);
    }
  };

  return (
    <section className="card">
      <h2>Notificações</h2>

      <div style={{ display: 'grid', gap: 8 }}>
        <input placeholder="user id" value={userId} onChange={e => setUserId(e.target.value)} />
        <div style={{ display: 'flex', gap: 8 }}>
          <button onClick={carregar} disabled={!userId}>Listar todas</button>
          <button onClick={carregarNaoLidas} disabled={!userId}>Listar não lidas</button>
        </div>

        <form onSubmit={criar} style={{ display: 'grid', gap: 8, marginTop: 8 }}>
          <h4>Criar notificação</h4>
          <select value={tipo} onChange={e => setTipo(e.target.value)}>
            <option>INFO</option>
            <option>WARNING</option>
            <option>ALERT</option>
            <option>CONFIRMACAO</option>
          </select>
          <input placeholder="Título" value={titulo} onChange={e => setTitulo(e.target.value)} />
          <textarea placeholder="Mensagem" value={mensagem} onChange={e => setMensagem(e.target.value)} />
          <button type="submit" disabled={!userId || !titulo || !mensagem}>Criar</button>
        </form>

        {error && <div style={{ color: 'red' }}>{error}</div>}

        {naoLidas && (
          <>
            <h4>Não lidas</h4>
            <ul>
              {naoLidas.map(n => (
                <li key={n.id} style={{ textAlign: 'left' }}>
                  <strong>{n.titulo}</strong> — {n.mensagem} <br />
                  <small>{n.criadaEm}</small>
                  <div><button onClick={() => marcar(n.id)}>Marcar como lida</button></div>
                </li>
              ))}
            </ul>
          </>
        )}

        {notifs && (
          <>
            <h4>Todas</h4>
            <ul>
              {notifs.map(n => (
                <li key={n.id} style={{ textAlign: 'left' }}>
                  <strong>{n.titulo}</strong> [{n.tipo}] — {n.mensagem} <br />
                  <small>{n.criadaEm} — lida: {n.lida ? 'sim' : 'não'}</small>
                  {!n.lida && <div><button onClick={() => marcar(n.id)}>Marcar como lida</button></div>}
                </li>
              ))}
            </ul>
          </>
        )}
      </div>
    </section>
  );
}
