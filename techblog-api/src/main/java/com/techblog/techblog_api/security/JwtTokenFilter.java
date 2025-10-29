package com.techblog.techblog_api.security;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.context.annotation.Lazy;

import java.io.IOException;

// Checa se o usuário pode acessar cada página usando o token JWT
@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil; // Classe que tem os métodos pra lidar com JWT
    private final UserDetailsService userDetailsService; // Serviço que busca os usuários

    // Construtor que recebe as dependências
    // O @Lazy foi usado para resolver um problema de "Lazy"
    public JwtTokenFilter(JwtUtil jwtUtil, @Lazy UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    // Esse método roda pra cada requisição que chega
    // Tentar entender melhor como funciona essa parte de filtros do Spring
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        
        // Tenta pegar o header de autorização da requisição
        final String authHeader = request.getHeader("Authorization");
        String username = null;
        String token = null;

        // Se tem o header e começa com "Bearer", ou seja, se tem esse token
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            // Pega só a parte do token (tira o "Bearer " do início)
            token = authHeader.substring(7);
            try {
                // Tenta extrair o username do token
                username = jwtUtil.getUsernameFromToken(token);
            } catch (IllegalArgumentException e) {
                // Se der erro ao processar o token
                logger.warn("Não foi possível pegar o token JWT");
            } catch (ExpiredJwtException e) {
                // Se o token estiver expirado
                logger.warn("Token JWT expirou");
            }
        }

        // Se conseguiu pegar o username do token e o usuário ainda não está autenticado
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Carrega os detalhes do usuário do banco de dados
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // Verifica se o token é válido pra esse usuário
            if (jwtUtil.validateToken(token, userDetails)) {
                // Se for válido, cria um token de autenticação do Spring, diferente do JWT
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                
                // Adiciona alguns detalhes da requisição no token
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                // Marca o usuário como autenticado no sistema
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        
        // Se chegou até aqui, o usuário está autenticado
        filterChain.doFilter(request, response);
    }
}