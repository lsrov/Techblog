package com.techblog.techblog_api.controller;

import com.techblog.techblog_api.dto.LoginRequest;
import com.techblog.techblog_api.dto.LoginResponse;
import com.techblog.techblog_api.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

// Controla tudo que tem a ver com login e autenticação
@RestController // Diz que essa classe vai responder requisições REST
@RequestMapping("/api/auth")
public class AuthController {

    // Preciso dessas coisas pra fazer o login funcionar
    private final AuthenticationManager authenticationManager; // Cuida da parte de verificar senha
    private final JwtUtil jwtUtil; // Faz aquele token JWT que deixa o usuário logado

    // O Spring injeta essas coisas automaticamente
    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    // Faz o login do usuário
    @PostMapping("/login") // Responde a POST em /api/auth/login
    public LoginResponse login(@RequestBody LoginRequest loginRequest) {
        // Tenta fazer login com email e senha
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(), 
                    loginRequest.getPassword()
                )
        );
        
        // Mostra no console que deu certo (debug)
        System.out.println("==> Login do usuário " + loginRequest.getEmail() + " funcionou!");
        
        // Cria o token pra manter o usuário logado
        String token = jwtUtil.generateToken(authentication.getName()); 
        
        // Devolve o token pro frontend usar depois
        return new LoginResponse(token);
    }
}