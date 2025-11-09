package com.techblog.techblog_api.security;

import com.techblog.techblog_api.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

// Configura tudo de segurança do sistema (login, senha, etc)
// Bem parecido com o projeto anterior que eu fiz
@Configuration
@EnableWebSecurity // Liga a segurança web para eu configurar do meu jeito
public class SecurityConfig {

    private final UserRepository userRepository; // Pra buscar usuários no banco
    private final JwtTokenFilter jwtTokenFilter; // Filtro que valida o token JWT

    // Construtor que recebe as dependências
    public SecurityConfig(UserRepository userRepository, JwtTokenFilter jwtTokenFilter) {
        this.userRepository = userRepository;
        this.jwtTokenFilter = jwtTokenFilter;
    }

    // Configura como as senhas são codificadas
    @Bean
    public PasswordEncoder passwordEncoder() {
        // Usa BCrypt pra fazer hash das senhas
        return new BCryptPasswordEncoder();
    }

    // Configura como os usuários são carregados. Ele é o intermediário entre o UserRepository e o Spring Security
    @Bean
    public UserDetailsService userDetailsService() {
        // Quando alguém tenta logar, essa função é chamada pra carregar o usuário
        return email -> userRepository.findByEmail(email).map(user -> {
                    // Converte o User para o User do Spring Security
                    return org.springframework.security.core.userdetails.User
                              .builder()
                              .username(user.getEmail()) // Email é o username
                              .password(user.getPassword()) // Senha já está com hash
                              .authorities("USER") // No caso, não tem diferença de papéis
                              .build();
                })
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
    }

    // Configura o gerenciador de autenticação. É aqui que ocorre o processo da autenticação, por exemplo, na comparação do hash de senha
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Isso permite o frontend acessara API
                // O CORS nada mais é do que um tipo de segurança dos navegadores
                .cors(cors -> cors.configurationSource( security -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.addAllowedOrigin("http://localhost:5173"); // URL do frontend
                    config.addAllowedMethod("*"); // Permite todos os métodos HTTP (GET, POST, etc)
                    config.addAllowedHeader("*"); // Permite todos os headers - um campo de uma requisição ou resposta HTTP que passa informações adicionais
                    return config;
                }))
                // Por algum motivo deu erro se não usar esses dois disables. Conflito? Se bem que é necessário fazer os dois já que eu estou usando o JWT
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) //Sem guardar sessão porque eu quero usar o JWT para autenticar
                // Configura as regras de acesso às URLs
                .authorizeHttpRequests(authorize -> authorize
                        // Essas URLs são públicas, então não precisaria estar logado, a não ser a dos artigos
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/articles").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/articles/**").permitAll()
                        .requestMatchers("/h2-console/**").permitAll() // Console do H2, que usei só pra testar antes de passar pro Postgres, deixei essa linha aqui errado
                        // O resto precisa de autenticação
                        .anyRequest().authenticated() 
                )
                // Algumas configs que usei no H2, mas não uso, já que passei pro Postgres
                .headers(headers -> headers.frameOptions(frame -> frame.disable())
                                           .cacheControl(cache -> cache.disable()))
                // Adiciona o filtro que valida o token antes do filtro de login
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}