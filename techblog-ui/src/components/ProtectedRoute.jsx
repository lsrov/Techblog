import React from 'react';
import { Navigate, Outlet } from 'react-router-dom';
import { isLoggedIn } from '../services/apiService';

// Só quem é logado vê o que está aqui
export function ProtectedRoute() {
    // O usuário está logado?
    if (isLoggedIn()) {
        // Se tiver logado, deixa passar pra página
        return <Outlet />;
    } else {
        // Senão, manda pra página de login
        return <Navigate to="/login" replace />;
    }
}