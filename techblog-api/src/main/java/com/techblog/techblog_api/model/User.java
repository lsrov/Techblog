package com.techblog.techblog_api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

// Dados dos usuários do blog (nome, email, etc)
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id // Chave primária
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true) // Não pode ter dois usuários com o mesmo email
    private String email;

    // Senha do usuário
    private String password; // Mesmo que esteja em String, vai virar hash e não pode ir pro bd

    // Lista de artigos que o usuário escreveu
    @OneToMany(mappedBy = "author") // Um usuário pode ter muitos artigos
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private List<Article> articles;
    
}