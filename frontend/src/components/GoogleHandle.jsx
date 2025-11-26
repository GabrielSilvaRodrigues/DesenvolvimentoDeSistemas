import React, { useState, useEffect } from 'react';
import { setAuthToken, setDeviceId, saveUserFromToken } from '../api';

export default function GoogleHandle({ onSuccess }) {
  const [popup, setPopup] = useState(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    function handler(e) {
      // Em ambiente dev usamos '*' — em produção restrinja origin
      if (!e?.data) return;
      const payload = e.data;
      if (payload?.type === 'oauth_token' && payload?.token) {
        const token = payload.token;
        const device = payload.device || 'oauth-google';
        console.info('[GoogleHandle] token recebido via postMessage (prefix):', token?.substring?.(0,16));
        setAuthToken(token);
        setDeviceId(device);
        // salva id do usuário se presente no token
        try { saveUserFromToken(token); } catch (err) {}
        setLoading(false);
        // fecha popup se ainda aberta
        try { popup?.close?.(); } catch {}
        setPopup(null);
        if (typeof onSuccess === 'function') onSuccess({ token, device });
        // opcionalmente redirecionar para raiz
        window.location.replace('/');
      }
    }

    window.addEventListener('message', handler);
    return () => window.removeEventListener('message', handler);
  }, [popup, onSuccess]);

  const startOAuth = () => {
    setLoading(true);
    // abre popup para backend iniciar OAuth2
    const left = window.screenX + (window.innerWidth - 600) / 2;
    const top = window.screenY + (window.innerHeight - 700) / 2;
    const features = `popup=yes,toolbar=no,location=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=600,height=700,left=${left},top=${top}`;
    // adiciona prompt=select_account para forçar o seletor de conta do Google
    const url = 'http://localhost:8080/oauth2/authorization/google?prompt=select_account';
    const w = window.open(url, 'oauth2_google', features);
    setPopup(w);
    // fallback: se popup for bloqueado, instrua usuário
    if (!w) {
      setLoading(false);
      alert('Popup bloqueado. Permita popups ou use o botão abrir link diretamente.');
    }
  };

  return (
    <div style={{ display: 'flex', gap: 8, alignItems: 'center' }}>
      <button onClick={startOAuth} disabled={loading}>
        {loading ? 'Abrindo Google...' : 'Entrar com Google'}
      </button>
      {popup && <span style={{ fontSize: 12, color: '#888' }}>Janela de login aberta...</span>}
    </div>
  );
}
