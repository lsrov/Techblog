import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { apiFetch } from '../services/apiService';

// Página de editar artigo
export function EditArticlePage() {
    // Guardando as informações do formulário
    const [title, setTitle] = useState('');
    const [content, setContent] = useState('');
    const [imageUrl, setImageUrl] = useState('');
    const [tags, setTags] = useState('');
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const navigate = useNavigate();
    const { id } = useParams();

    // Busca os dados do artigo quando a página carrega
    useEffect(() => {
        const fetchArticle = async () => {
            setLoading(true);
            try {
                // Pega os dados do artigo
                const response = await apiFetch(`/articles/${id}`); 
                if (!response.ok) {
                    throw new Error('Não foi possível carregar o artigo para editar.');
                }
                const data = await response.json();

                // Coloca os dados no formulário
                setTitle(data.title);
                setContent(data.content);
                setImageUrl(data.imageUrl || ''); // Se não tiver imagem, deixa vazio
                // Transforma array de tags em texto: [{name: "js"}, {name: "react"}] -> "js, react"
                setTags(data.tags.map(tag => tag.name).join(', '));
                
            } catch (err) {
                setError(err.message);
            } finally {
                setLoading(false);
            }
        };
        fetchArticle();
    }, [id]);

    // Salva as alterações
    const handleSubmit = async (event) => {
        event.preventDefault();
        setError(null);

        const tagsArray = tags.split(',').map(tag => tag.trim()).filter(Boolean);
        const articleData = { title, content, imageUrl, tags: tagsArray };

        try {
            // Tenta salvar no backend
            const response = await apiFetch(`/articles/${id}`, {
                method: 'PUT',
                body: JSON.stringify(articleData),
            });

            if (!response.ok) { // Se der errado, lança o erro
                throw new Error('Falha ao atualizar o artigo');
            }
            
            // Se salvou, volta pra página do artigo
            navigate(`/article/${id}`);

        } catch (err) {
            setError(err.message);
        }
    };

    // Excluir o artigo
    const handleDelete = async () => {
        if (!window.confirm('Tem certeza que deseja excluir este artigo? Esta ação não pode ser desfeita.')) {
            return;
        }
        
        setError(null);
        try {
            // Tenta excluir no backend
            const response = await apiFetch(`/articles/${id}`, {
                method: 'DELETE',
            });

            if (!response.ok) {
                throw new Error('Falha ao excluir o artigo');
            }

            // Se não deu erro, volta pra página de artigos
            navigate('/articles');

        } catch (err) {
            setError(err.message);
        }
    };

    // Carregar a página de edição
    if (loading) {
        return <main className="container"><p>Carregando editor...</p></main>;
    }
    
    // Mesma lógica da criação, mas agora é para editar
    return (
        <main className="form-container">
            <div className="form-box">
                <h1 className="form-title">Editar artigo</h1>
                
                <form className="article-form" onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label htmlFor="title">Título</label>
                        <input 
                            type="text" id="title" value={title}
                            onChange={e => setTitle(e.target.value)} required
                        />
                    </div>
                    <div className="form-group">
                        <label htmlFor="imageUrl">URL da Imagem (Opcional)</label>
                        <input 
                            type="text" id="imageUrl" value={imageUrl}
                            onChange={e => setImageUrl(e.target.value)}
                        />
                    </div>
                    <div className="form-group">
                        <label htmlFor="tags">Tags (separadas por vírgula)</label>
                        <input 
                            type="text" id="tags" value={tags}
                            onChange={e => setTags(e.target.value)}
                        />
                    </div>
                    <div className="form-group">
                        <label htmlFor="content">Conteúdo</label>
                        <textarea 
                            id="content" rows="10" value={content}
                            onChange={e => setContent(e.target.value)} required
                        />
                    </div>
                    
                    {error && <p className="form-error">{error}</p>}
                    
                    {/* Botões de Ação */}
                    <div className="form-actions">
                        <button type="submit" className="cta-button">Salvar Alterações</button>
                        <button type="button" className="cta-button-delete" onClick={handleDelete}>Excluir Artigo</button>
                    </div>
                </form>
            </div>
        </main>
    );
}