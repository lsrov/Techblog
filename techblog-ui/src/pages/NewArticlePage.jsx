import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { apiFetch } from '../services/apiService';

// Página de criar novo artigo
export function NewArticlePage() {
    const [title, setTitle] = useState('');
    const [content, setContent] = useState('');
    const [imageUrl, setImageUrl] = useState('');
    const [tags, setTags] = useState('');
    const [error, setError] = useState(null);
    const navigate = useNavigate();

    // Função que cria o artigo
    const handleSubmit = async (event) => {
        event.preventDefault();
        setError(null);

        // Organiza as tags num array "js, react, dev" -> ["js", "react", "dev"]
        const tagsArray = tags.split(',').map(tag => tag.trim()).filter(tag => tag.length > 0);
        // Junta tudo
        const articleData = {
            title,
            content,
            imageUrl,
            tags: tagsArray
        };

        try {
            // Tenta criar o artigo no backend
            const response = await apiFetch('/articles', {
                method: 'POST',
                body: JSON.stringify(articleData),
            });

            if (!response.ok) {
                // Se der algum erro
                const errorData = await response.json();
                throw new Error(errorData.message || 'Falha ao criar o artigo');
            }

            // Senão
            console.log('Artigo criado com sucesso!');
            navigate('/articles');

        } catch (err) {
            console.error('Erro ao criar artigo:', err);
            setError(err.message);
        }
    };

    return (
        <main className="form-container">
            <div className="form-box">
                <h1 className="form-title">Criar novo artigo</h1>
                
                <form className="article-form" onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label htmlFor="title">Título</label>
                        <input 
                            type="text" 
                            id="title" 
                            placeholder="Título do artigo" 
                            value={title}
                            onChange={e => setTitle(e.target.value)}
                            required
                        />
                    </div>
                    <div className="form-group">
                        <label htmlFor="imageUrl">URL da Imagem (Opcional)</label>
                        <input 
                            type="text" 
                            id="imageUrl" 
                            placeholder="https://exemplo.com/imagem.png"
                            value={imageUrl}
                            onChange={e => setImageUrl(e.target.value)}
                        />
                    </div>
                    <div className="form-group">
                        <label htmlFor="tags">Tags (separadas por vírgula)</label>
                        <input 
                            type="text" 
                            id="tags" 
                            placeholder="ex: frontend, react, devops"
                            value={tags}
                            onChange={e => setTags(e.target.value)}
                        />
                    </div>
                    <div className="form-group">
                        <label htmlFor="content">Conteúdo</label>
                        <textarea 
                            id="content" 
                            placeholder="Escreva seu artigo aqui..." 
                            rows="10"
                            value={content}
                            onChange={e => setContent(e.target.value)}
                            required
                        />
                    </div>
                    
                    {error && <p className="form-error">{error}</p>}
                    
                    <button type="submit" className="cta-button full-width">Publicar</button>
                </form>
            </div>
        </main>
    );
}