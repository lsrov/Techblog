import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';

// Página de login
export function LoginPage() {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState(null); 
    
    // Para navegar para outra página
    const navigate = useNavigate(); 

    // Função para logar
    const handleSubmit = (event) => {
        event.preventDefault(); // Evita que a página recarregue
        
        setError(null); // Limpa erros anteriores

        // Tenta fazer login no backend
        fetch('http://localhost:8080/api/auth/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ email: email, password: password }),
        })
        .then(response => {
            // Verifica se deu tudo certo
            if (!response.ok) { 
                throw new Error('Email ou senha inválidos');
            }
            return response.json(); 
        })
        .then(data => {
            // Se deu certo, guarda o token e vai pra página de artigos
            console.log('Login bem-sucedido!');
            localStorage.setItem('authToken', data.token);
            navigate('/articles');
        })
        .catch(err => {
            // Se deu erro, mostra mensagem
            console.error('Falha no login:', err);
            setError(err.message);
        });
    };

    return (
        <main className="login-container">
            <div className="login-box">
                <h1 className="login-title">Bem-vindo de volta</h1>
                
                <form className="login-form" onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label htmlFor="email">Email</label>
                        <input 
                            type="email" 
                            id="email" 
                            placeholder="Email" 
                            value={email} 
                            onChange={e => setEmail(e.target.value)} 
                            required
                        />
                    </div>
                    <div className="form-group">
                        <label htmlFor="password">Senha</label>
                        <input 
                            type="password" 
                            id="password" 
                            placeholder="Senha" 
                            value={password} 
                            onChange={e => setPassword(e.target.value)} 
                            required
                        />
                    </div>
                    
                    {error && <p className="login-error">{error}</p>}
                    
                    <button type="submit" className="cta-button full-width">Entrar</button>
                </form>
            </div>
        </main>
    );
}