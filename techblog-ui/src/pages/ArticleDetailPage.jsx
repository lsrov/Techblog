import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { apiFetch, isLoggedIn } from '../services/apiService';

// Componente que mostra um comentário
// Este é um componente "burro" (dumb component), ele só recebe dados ('comment') e exibe.
function CommentCard({ comment }) {
    // Formata a data no padrão brasileiro
    const formattedDate = new Date(comment.createdAt).toLocaleDateString('pt-BR', {
        day: '2-digit', 
        month: 'long', 
        year: 'numeric'
    });

    // Mostra o comentário na tela - retorno para HTML
    return (
        <div className="comment-card">
            {/* Imagem do autor, se bem que não implementei as imagens de perfil */}
            <div className="comment-author-img">
                <img src={`https://ui-avatars.com/api/?name=${comment.authorName}&background=eef3ee&color=2a4a27`} alt={comment.authorName} />
            </div>
            {/* Conteúdo do comentário */}
            <div className="comment-content">
                <div className="comment-header">
                    <span className="comment-author-name">{comment.authorName}</span>
                    <span className="comment-date">{formattedDate}</span>
                </div>
                <p className="comment-text">{comment.content}</p>
            </div>
        </div>
    );
}

// Página que mostra um artigo específico
export function ArticleDetailPage() {
    // Todas as informações dos artigos e comentários
    // 'article' guarda os dados do artigo, 'setArticle' é a função para atualizá-lo
    const [article, setArticle] = useState(null); // Começa como nulo
    const [comments, setComments] = useState([]); // Começa como lista vazia
    const [loading, setLoading] = useState(true); // Começa como carregando
    const [error, setError] = useState(null);
    const { id } = useParams();
    const [newComment, setNewComment] = useState(''); 
    const [submitError, setSubmitError] = useState(null); 
    
    // Para checar se o usuário está logado
    const userIsLoggedIn = isLoggedIn();

    // Busca os dados do artigo quando a página carrega
    useEffect(() => {
        // Usa async para usar o await 
        const fetchArticleData = async () => {
            setLoading(true);
            try {
                // Busca o artigo e o 'await' pausa a função até o 'apiFetch' (chamada à API) terminar
                const articleResponse = await apiFetch(`/articles/${id}`);
                if (!articleResponse.ok) {
                    throw new Error('Artigo não encontrado');
                }
                const articleData = await articleResponse.json();
                setArticle(articleData);

                // Busca os comentários
                const commentsResponse = await apiFetch(`/articles/${id}/comments`);
                if (!commentsResponse.ok) {
                    throw new Error('Não foi possível carregar os comentários');
                }
                const commentsData = await commentsResponse.json();
                setComments(commentsData);

            } catch (err) {
                setError(err.message);
            } finally {
                setLoading(false);
            }
        };

        fetchArticleData();
    }, [id]);

    // Função que envia um novo comentário
    const handleCommentSubmit = async (event) => {
        event.preventDefault(); 
        setSubmitError(null);

        // Verifica se o comentário não está vazio
        if (newComment.trim().length === 0) {
            setSubmitError('O comentário não pode estar vazio.');
            return;
        }

        try {
            const response = await apiFetch(`/articles/${id}/comments`, {
                method: 'POST',
                body: JSON.stringify({ content: newComment }),
            });

            if (!response.ok) {
                throw new Error('Falha ao publicar o comentário.');
            }

            const createdCommentDTO = await response.json();
            setComments([createdCommentDTO, ...comments]); // Adiciona no topo
            setNewComment(''); // Limpa o formulário

        } catch (err) {
            console.error('Erro ao enviar comentário:', err);
            setSubmitError(err.message);
        }
    };

    
    // Carregamento da página em andamento
    if (loading) {
        return <main className="container"><p>Carregando...</p></main>;
    }

    if (error) {
        return <main className="container"><p className="form-error">{error}</p></main>;
    }

    if (!article) {
        return null; // Não renderiza nada se o artigo for nulo, mas quando deu erro, redireciona para a página de login
    }

    // Carregamento da página
    return (
        <main className="article-detail-container container">
            {/* Artigo */}
            <article className="article-full">
                <h1 className="article-full-title">{article.title}</h1>
                <div className="article-full-meta">
                    Por <span className="author-name">{article.authorName}</span>
                </div>
                
                {article.imageUrl && (
                    <img 
                        src={article.imageUrl} 
                        alt={article.title} 
                        className="article-full-image" 
                    />
                )}
                
                <div 
                    className="article-full-content" 
                    style={{ whiteSpace: 'pre-wrap' }}
                >
                    {article.content}
                </div>
            </article>

            {/* Comentários */}
            <section className="comments-section">
                <h2 className="comments-title">Comentários ({comments.length})</h2>
                
                {/* Formulário de comentários */}
                {userIsLoggedIn ? (
                    <form className="comment-form" onSubmit={handleCommentSubmit}>
                        <textarea 
                            placeholder="Escreva seu comentário..." 
                            value={newComment}
                            onChange={e => setNewComment(e.target.value)}
                            required
                        />
                        {submitError && <p className="form-error-inline">{submitError}</p>}
                        <button type="submit" className="cta-button">Publicar</button>
                    </form>
                ) : (
                    <form className="comment-form-disabled">
                        <textarea placeholder="Deixe seu comentário (login necessário)..." disabled />
                        <button type="submit" className="cta-button" disabled>Publicar</button>
                    </form>
                )}

                {/* Lista dos Comentários */}
                <div className="comments-list">
                    {comments.length > 0 ? (
                        comments.map(comment => (
                            <CommentCard key={comment.id} comment={comment} />
                        ))
                    ) : (
                        <p>Nenhum comentário ainda. Seja o primeiro a comentar</p>
                    )}
                </div>
            </section>
        </main>
    );
}