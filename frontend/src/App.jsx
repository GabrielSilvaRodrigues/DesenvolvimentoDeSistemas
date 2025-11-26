import './App.css';
import { useState, useEffect } from 'react';
import { Routes, Route, Navigate, useLocation, useNavigate } from 'react-router-dom';

import Navbar from './components/Navbar';
import CepLookup from './components/CepLookup';
import EnderecoForm from './components/EnderecoForm';
import UsuarioLookup from './components/UsuarioLookup';
import TokenPanel from './components/TokenPanel';
import EmailPanel from './components/EmailPanel';
import GoogleAuthPanel from './components/GoogleAuthPanel';
import Register from './components/Register';
import TokenHandler from './components/TokenHandler';
import OAuthCallback from './components/OAuthCallback';
import NotificacaoPanel from './components/NotificacaoPanel';
import ProfileImageUpload from './components/ProfileImageUpload';

function App() {
  // estado local usado pela UI (mantém comportamento anterior)
  const [view, setView] = useState('cep');

  const location = useLocation();
  const navigate = useNavigate();

  // sincroniza view local com a rota atual (quando o usuário muda URL / refresh)
  useEffect(() => {
    const path = location.pathname.split('/').filter(Boolean)[0] || 'cep';
    setView(path);
  }, [location.pathname]);

  // onSelect passado para Navbar: atualiza estado local e navega para a rota correspondente
  const handleSelect = (id) => {
    setView(id);
    // mapeia id para rota (padrao: /{id}, exceto ajustes se necessário)
    const route = id === 'cep' ? '/cep' : `/${id}`;
    navigate(route);
  };

  return (
    <div style={{ fontFamily: 'Inter, system-ui, sans-serif', padding: 16 }}>
      <header style={{ display: 'flex', gap: 12, alignItems: 'center' }}>
        <h1>Frontend - Cliente (integração backend)</h1>
      </header>

      <Navbar current={view} onSelect={handleSelect} />

      <Routes>
        <Route path="/" element={<Navigate to="/cep" replace />} />

        <Route
          path="/cep"
          element={
            <main style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 12, marginTop: 12 }}>
              <CepLookup />
              <UsuarioLookup />
            </main>
          }
        />

        <Route path="/usuario" element={<main style={{ marginTop: 12 }}><UsuarioLookup /></main>} />
        <Route path="/endereco" element={<main style={{ marginTop: 12 }}><EnderecoForm /></main>} />
        <Route path="/token" element={<main style={{ marginTop: 12 }}><TokenPanel /></main>} />
        <Route path="/email" element={<main style={{ marginTop: 12 }}><EmailPanel /></main>} />
        <Route path="/google" element={<main style={{ marginTop: 12 }}><GoogleAuthPanel /></main>} />
        <Route path="/register" element={<main style={{ marginTop: 12 }}><Register /></main>} />

        {/* novas rotas que englobam controllers adicionais */}
        <Route path="/notificacao" element={<main style={{ marginTop: 12 }}><NotificacaoPanel /></main>} />
        <Route path="/profile" element={<main style={{ marginTop: 12 }}><ProfileImageUpload /></main>} />

        {/* token flows */}
        <Route path="/token/cadastro" element={<TokenHandler />} />
        <Route path="/token/oauth-callback" element={<OAuthCallback />} />

        {/* fallback */}
        <Route path="*" element={<Navigate to="/cep" replace />} />
      </Routes>
    </div>
  );
}

export default App;
