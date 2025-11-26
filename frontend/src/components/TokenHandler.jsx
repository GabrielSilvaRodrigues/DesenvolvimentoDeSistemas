import { useEffect, useState } from 'react';
import { setUserId } from '../api';

export default function TokenHandler() {
  const [status, setStatus] = useState('loading');
  const [message, setMessage] = useState('');
  const [token, setToken] = useState('');
  const [dispositivo, setDispositivo] = useState('web-1');
  const [resp, setResp] = useState(null);
  const [err, setErr] = useState(null);

  useEffect(() => {
    const params = new URLSearchParams(window.location.search);
    const t = params.get('token');
    const d = params.get('dispositivo') || 'web-1';

    console.log('[TokenHandler] Token da URL:', t);
    console.log('[TokenHandler] Dispositivo da URL ou fallback:', d);

    if (!t) {
      setStatus('error');
      setErr('Token não encontrado na URL.');
      console.error('[TokenHandler] Token não encontrado!');
      return;
    }

    setToken(t);
    setDispositivo(d);

    const ativar = async () => {
      setStatus('loading');
      setErr(null);
      setResp(null);

      try {
        const url = `http://localhost:8080/auth/ativar?token=${encodeURIComponent(
          t
        )}&dispositivo=${encodeURIComponent(d)}`;
        console.log('[TokenHandler] Chamando backend para ativação:', url);

        const res = await fetch(url, { method: 'POST' });
        console.log('[TokenHandler] Resposta do fetch:', res.status, res.ok);

        if (!res.ok) {
          const text = await res.text();
          console.error('[TokenHandler] Erro do backend:', text);
          throw new Error(`${res.status} ${text}`);
        }

        const data = await res.json();
        console.log('[TokenHandler] Dados recebidos:', data);

        setResp(data);
        // salva user id retornado após ativação (quando houver)
        if (data?.id) {
          try { setUserId(data.id); } catch {}
        }

        setStatus('success');
        setMessage(`Conta ativada com sucesso! ID usuário: ${data?.id ?? 'N/A'}`);
      } catch (error) {
        console.error('[TokenHandler] Exceção capturada:', error);
        setStatus('error');
        setErr(error.message);
      }
    };

    ativar();
  }, []);

  return (
    <section className="card">
      <h2>Processando ativação de token</h2>

      {status === 'loading' && <div>Processando token...</div>}
      {status === 'success' && <div style={{ color: 'green' }}>{message}</div>}
      {status === 'error' && <div style={{ color: 'red' }}>{err}</div>}

      {resp && (
        <pre style={{ textAlign: 'left', marginTop: 8 }}>
          {JSON.stringify(resp, null, 2)}
        </pre>
      )}

      <div style={{ marginTop: 12 }}>
        <a href="/">Voltar para a aplicação</a>
      </div>
    </section>
  );
}
