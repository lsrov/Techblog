import React from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { isLoggedIn } from '../services/apiService';

// Desenho do botão de sair
function LogoutIcon() {
  return (
    <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
      <path d="M17 16L21 12M21 12L17 8M21 12H9M15 4V3H5V21H15V20" stroke="#333" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
    </svg>
  );
}

export function Header() {
  // Para pegar informações sobre a página atual
  const location = useLocation();
  const navigate = useNavigate();
  const userIsLoggedIn = isLoggedIn();

  // Para verificar em qual página estamos
  const isHomePage = location.pathname === '/';
  const isLoginPage = location.pathname === '/login';

  // Função que faz o logout
  const handleLogout = () => {
    localStorage.removeItem('authToken'); // Remove o token de login
    navigate('/login'); // Manda pra página de login
  };

  // Para decidir o que vai mostrar no lado direito do cabeçalho, se o "Entrar" ou o ícone ou nada
  let rightSideContent = null;

  // Lógica para mostrar o botão certo
  if (!isLoginPage) {
    if (userIsLoggedIn) {
      // Se tiver logado, mostra botão de sair
      rightSideContent = (
        <button onClick={handleLogout} className="header-action-icon header-logout-button">
          <LogoutIcon />
        </button>
      );
    } else if (isHomePage) {
      // Se não tiver logado e estiver na página inicial, mostra botão de entrar
      rightSideContent = (
        <Link to="/login" className="header-login-link">Entrar</Link>
      );
    }
    // Se não estiver logado e não estiver na página inicial (na página de login), não mostra nada
  }

  return (
    <header className="header">
      <div className="container">
        <Link to="/" className="logo">TechBlog</Link>
        <nav>
          {rightSideContent}
        </nav>
      </div>
    </header>
  );
}