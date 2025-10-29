package com.techblog.techblog_api.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

    // Pega a chave secreta do application.properties
    @Value("${jwt.secret}")
    private String secret;

    // Tempo de expiração do token: 24 horas
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24;

    // Gera um novo token JWT pro usuário
    public String generateToken(String username) {
        // "Monta" o token
        return Jwts.builder()
                   .setSubject(username) // Username do usuário
                   .setIssuedAt(new Date(System.currentTimeMillis())) // Quando foi gerado
                   .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // Quando expira
                   .signWith(getSigningKey(), SignatureAlgorithm.HS256) // Gera assinatura com uma chave secreta
                   .compact(); // Transforma tudo em uma string
    }

    // Verifica se um token é válido pra um usuário
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        // Precisa ter o mesmo username e não pode estar expirado
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // Pega o username que está dentro do token
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    // Verifica se o token já expirou
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date()); // Compara com a data atual
    }

    // Pega a data de expiração do token
    private Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    // Método genérico pra pegar qualquer informação do token
    // Usa um conceito chamado "Function" do Java 8+
    private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
    // Estudar mais sobre Function<T,R>
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    // Pega todas as informações do token
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                   .setSigningKey(getSigningKey()) // Usa a mesma chave secreta
                   .build()
                   .parseClaimsJws(token) // Decodifica o token
                   .getBody(); // Pega só o conteúdo
    }

    // Gera a chave que é usada pra assinar os tokens
    private Key getSigningKey() {
        // Converte nossa string secreta em bytes
        byte[] keyBytes = this.secret.getBytes(StandardCharsets.UTF_8);
        // Cria uma chave nessa criptografia hs256
        return Keys.hmacShaKeyFor(keyBytes);
    }
}