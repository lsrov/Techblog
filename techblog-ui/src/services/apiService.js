const API_BASE_URL = 'http://localhost:8080/api'; // Endereço do backend

// Função que pega o token de login salvo no navegador (aparece no Network do DevTools)
const getToken = () => {
    return localStorage.getItem('authToken');
};

export const apiFetch = async (endpoint, options = {}) => { // Serve para fazer chamadas, ou seja, comunicar com o backend
    // Pega o token de login
    const token = getToken();

    // Configura o cabeçalho da requisição
    const headers = {
        'Content-Type': 'application/json',
        ...options.headers,
    };

    // Se tiver token, coloca ele no cabeçalho
    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }

    // Tenta fazer a requisição
    try {
        const response = await fetch(`${API_BASE_URL}${endpoint}`, {
            ...options,
            headers: headers,
        });

        // Se der erro de autorização, desloga o usuário
        // Isso estava acontecendo quando o software não conseguia carregar os comentários, por exemplo. Não sei porquê
        if ((response.status === 401 || response.status === 403) && endpoint !== '/auth/login') {
            console.warn('Erro de autorização. Deslogando...');
            localStorage.removeItem('authToken');
            window.location.href = '/login';
        }

        return response;

    } catch (error) {
        console.error('Erro na chamada da API', error);
        throw error;
    }
};

// Checa se já tem alguém logado
export const isLoggedIn = () => {
    return getToken() !== null;
};