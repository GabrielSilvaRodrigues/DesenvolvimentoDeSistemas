import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import { BrowserRouter } from 'react-router-dom';
import './index.css';
import App from './App.jsx';

// Suppress noisy React Router "Future Flag" warnings in dev console.
// This filters warnings that contain "React Router Future Flag Warning".
// Keeps other warnings intact.
if (typeof console !== 'undefined' && console.warn) {
  const _origWarn = console.warn.bind(console);
  console.warn = (...args) => {
    try {
      const first = args[0];
      const text = typeof first === 'string' ? first : JSON.stringify(first);
      if (text && text.includes('React Router Future Flag Warning')) {
        return;
      }
    } catch (e) {
      // fall through to original warn on error
    }
    _origWarn(...args);
  };
}

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <BrowserRouter>
      <App />
    </BrowserRouter>
  </StrictMode>
);
