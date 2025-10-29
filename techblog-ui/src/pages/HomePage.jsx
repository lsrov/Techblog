import React from 'react';
import { Link } from 'react-router-dom';

// Página inicial do site
export function HomePage() {

  return (
    <main className="hero-container">
      <div className="container text-center">
        {/* Título principal */}
        <h1 className="hero-title">Insights & Learning</h1>
        {/* Subtítulo */}
        <p className="hero-subtitle">Explorando tendências Tech, um post por vez</p>
        {/* Botão que leva para os artigos */}
        <Link to="/articles" className="cta-button">Começar a ler</Link>
      </div>
    </main>
  );
}