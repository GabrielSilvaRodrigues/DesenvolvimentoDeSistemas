import { useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';

export default function OAuthCallback() {
  const [searchParams] = useSearchParams();

  useEffect(() => {
    const token = searchParams.get('token');
    const device = searchParams.get('dispositivo') || 'oauth-google';

    if (token && window.opener) {
      try {
        // Em dev usamos '*' — em produção forneça origin restrito
        window.opener.postMessage({ type: 'oauth_token', token, device }, '*');
      } catch (e) {
        console.error('Erro postMessage para opener', e);
      }
      // tenta fechar a popup
      try { window.close(); } catch (e) { /* ignore */ }
    } else if (token) {
      // se não há opener (abrir direto), redireciona SPA para tratamento padrão
      window.location.replace(`/token/cadastro?token=${encodeURIComponent(token)}&dispositivo=${encodeURIComponent(device)}`);
    } else {
      // sem token, volta para aplicação raiz
      window.location.replace('/');
    }
  }, [searchParams]);

  return (
    <div style={{ padding: 24 }}>
      <h3>Finalizando autenticação...</h3>
      <div>Aguarde, você pode fechar esta janela.</div>
    </div>
  );
}
