package com.techblog.techblog_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

// Devolve o token quando o login dá certo
@Data
@AllArgsConstructor // Cria construtor com os atributos (token)
public class LoginResponse {
    private String token; // Token JWT que tá no application.properties
    
    // O frontend usa esse token pra "provar" que tá logado
}