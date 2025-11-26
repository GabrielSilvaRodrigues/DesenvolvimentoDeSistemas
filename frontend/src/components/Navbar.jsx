import React from 'react';
import { NavLink, useNavigate } from 'react-router-dom';
import { logout as apiLogout, getAuthToken, setAuthToken, setDeviceId } from '../api'; // adiciona setAuthToken/setDeviceId

export default function Navbar({ current, onSelect }) {
  const items = [
    { id: 'cep', label: 'CEP', path: '/cep' },
    { id: 'usuario', label: 'Usuário', path: '/usuario' },
    { id: 'endereco', label: 'Endereço', path: '/endereco' },
    { id: 'token', label: 'Token', path: '/token' },
    { id: 'email', label: 'E-mail', path: '/email' },
    { id: 'google', label: 'Google Auth', path: '/google' },
    { id: 'register', label: 'Registrar', path: '/register' },

    // novos itens para cobrir controllers adicionais
    { id: 'notificacao', label: 'Notificações', path: '/notificacao' },
    { id: 'profile', label: 'Perfil (upload)', path: '/profile' }
  ];

  const navigate = useNavigate();

  const handleLogout = async () => {
    try {
      await apiLogout();
      // volta para a tela inicial
      navigate('/cep');
      onSelect?.('cep');
    } catch (e) {
      alert('Erro ao sair: ' + (e?.message || 'desconhecido'));
    }
  };

  // abre endpoint que inicia logout no Google e retorna ao frontend
  const handleLogoutGoogle = () => {
    // pega token salvo (se houver) para que o backend possa revogá-lo
    const token = getAuthToken();

    // limpa local imediatamente para evitar UI mostrando "logado"
    setAuthToken(null);
    setDeviceId(null);

    // envia returnTo sem encodeURIComponent — o backend já faz URLEncoder
    const returnTo = window.location.origin + '/';

    const url = new URL('http://localhost:8080/auth/logout-google');
    url.searchParams.set('returnTo', returnTo);
    if (token) url.searchParams.set('token', token);

    // abre na mesma janela para que o flow de logout do provedor funcione corretamente
    window.location.href = url.toString();
  };

  return (
    <nav style={{ display: 'flex', gap: 8, marginBottom: 12 }}>
      {items.map(it => (
        <NavLink
          key={it.id}
          to={it.path}
          onClick={() => onSelect?.(it.id)}
          style={({ isActive }) => ({
            padding: '8px 12px',
            border: '1px solid #ccc',
            borderRadius: 6,
            cursor: 'pointer',
            background: (current === it.id) || isActive ? '#646cff' : 'transparent',
            color: (current === it.id) || isActive ? '#fff' : 'inherit'
          })}
        >
          {it.label}
        </NavLink>
      ))}

      {/* botão de logout padrão (revoga token no backend e limpa localStorage) */}
      <button onClick={handleLogout} style={{ marginLeft: 8 }}>
        Sair
      </button>

      {/* botão específico para encerrar sessão no provedor Google */}
      <button onClick={handleLogoutGoogle} style={{ marginLeft: 8 }}>
        Sair Google
      </button>
    </nav>
  );
}
