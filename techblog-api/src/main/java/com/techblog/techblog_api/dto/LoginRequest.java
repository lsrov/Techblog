package com.techblog.techblog_api.dto;

import lombok.Data;

// Recebe o email e senha quando alguém loga
@Data
public class LoginRequest {
    private String email;    // Email
    private String password; // Senha
    
    // TODO: Validar o email e a senha
}