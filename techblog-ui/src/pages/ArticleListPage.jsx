import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { apiFetch } from '../services/apiService';

// Componente que mostra cada artigo na lista
function ArticleListItem({ article }) {
  const authorName = article.authorName || 'Autor Desconhecido'; // Se não tiver autor, aparece "Desconhecido"
  // Pegando a primeira tag do artigo (coloca "Geral" se não tiver)
  const primaryTag = Array.isArray(article.tags) && article.tags.length > 0 ? article.tags[0].name : 'Geral';

  // Artigo parte visual
  return (
    <div className="article-list-item">
      {/* Imagem do artigo */}
      <img 
        src={article.imageUrl || 'https://via.placeholder.com/80x80?text=Img'} 
        alt={article.title} 
        className="article-item-image" 
      />
      {/* Conteúdo */}
      <div className="article-item-content">
        <Link to={`/article/${article.id}`} className="article-item-title-link">
            <h3 className="article-item-title">{article.title}</h3>
        </Link>
        <p className="article-item-description">{article.content.substring(0, 120)}...</p>
        <div className="article-item-meta">
          <span>{authorName}</span>
          <span className="article-item-tag">{primaryTag}</span>
        </div>
      </div>
      {/* Botão de editar, só se for o autor do artigo */}
      <div className="article-item-actions">
        {article.canEdit && (
          <Link to={`/edit/${article.id}`}>✏️</Link>
        )}
      </div>
    </div>
  );
}

// Página que mostra a lista de artigos
export function ArticleListPage() {
  // Guardando os dados dos artigos
  const [articles, setArticles] = useState([]);
  const [tags, setTags] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  // Guardando os filtros
  const [selectedTag, setSelectedTag] = useState(null);
  const [searchQuery, setSearchQuery] = useState('');

  // Busca as tags quando a página carrega
  useEffect(() => {
    const fetchTags = async () => {
      try {
        const response = await apiFetch('/tags');
        if (!response.ok) {
          throw new Error('Falha ao buscar tags');
        }
        const data = await response.json();
        setTags(data);
      } catch (err) {
        console.error(err.message);
      }
    };
    fetchTags();
  }, []);

  // Busca os artigos quando mudar os filtros
  useEffect(() => {
    // Espera um pouco antes de buscar só pra evitar bugs
    const timerId = setTimeout(() => {
      fetchArticles();
    }, 500);

    // Limpa o timer se mudar os filtros de novo
    return () => clearTimeout(timerId);

  }, [selectedTag, searchQuery]);

  const fetchArticles = async () => { // Busca os artigos com base nesses filtros
    setLoading(true);
    setError(null);
    let url = '/articles'; // URL padrão

    try {
      if (searchQuery) {
        // Se for busca pela caixa de texto
        url = `/articles/search?q=${encodeURIComponent(searchQuery)}`;
      } else if (selectedTag) {
        // Se for busca pelas tags
        url = `/articles/tag?name=${encodeURIComponent(selectedTag)}`;
      }
      // Se não for nenhum dos dois, usa o padrão '/articles'

      const response = await apiFetch(url); // Dá o sinal de que o usuário pode editar
      if (!response.ok) {
        throw new Error('Falha ao buscar artigos');
      }
      const data = await response.json();
      setArticles(data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleTagClick = (tagName) => {
    // Se clicar na tag que já está ativa, desativa (vai para 'Todos')
    if (selectedTag === tagName) {
      setSelectedTag(null);
    } else {
      setSelectedTag(tagName);
    }
    setSearchQuery(''); // Limpa a busca ao clicar na tag
  };

  const handleSearchChange = (event) => {
    setSearchQuery(event.target.value);
    setSelectedTag(null); // Limpa a tag ao digitar na busca
  };

  return (
    <main className="main-content container">
      <div className="article-list-header">
        <h1>Todos os artigos</h1>
        <Link to="/new" className="cta-button">Criar artigo</Link>
      </div>

      {/* --- Seção de Filtros --- */}
      <section className="filter-section">
        <div className="tags-list">
          {/* Tag "Todos" */}
          <span 
            className={`tag ${selectedTag === null ? 'active' : ''}`}
            onClick={() => handleTagClick(null)}
          >
            Todos
          </span>
          
          {/* Tags Dinâmicas */}
          {tags.map(tag => (
            <span 
              key={tag.id}
              className={`tag ${selectedTag === tag.name ? 'active' : ''}`}
              onClick={() => handleTagClick(tag.name)}
            >
              {tag.name}
            </span>
          ))}
        </div>
        
        {/* Barra de Busca */}
        <input 
          type="text" 
          placeholder="Pesquisar por título ou conteúdo..." 
          className="search-bar"
          value={searchQuery}
          onChange={handleSearchChange} 
        />
      </section>

      {/* --- Seção da Lista de Artigos --- */}
      <section className="article-list-new">
        {loading && <p>Carregando artigos...</p>}
        {error && <p className="form-error">{error}</p>}
        
        {!loading && !error && articles.length === 0 && (
          <p>Nenhum artigo encontrado com esses filtros.</p>
        )}

        {!loading && !error && articles.map(article => (
          <ArticleListItem key={article.id} article={article} />
        ))}
      </section>

      {/* Paginação, mesmo que ainda estática */}
      <nav className="pagination">
        <span className="page-number active">1</span>
        <span className="page-number">2</span>
        <span className="page-number">3</span>
      </nav>
    </main>
  );
}