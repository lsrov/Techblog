package com.techblog.techblog_api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

// Tags dos artigos
@Entity
@Table(name = "tags")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tag {

    @Id // Chave primária
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false, unique = true) // unique = true evita duplicata
    private String name;

    // Lista de artigos que usam essa tag
    @ManyToMany(mappedBy = "tags") // Uma tag pode estar em vários artigos
    @ToString.Exclude
    @EqualsAndHashCode.Exclude

    @JsonIgnore // Não precisa buscar todos os artigos, faz uma consulta específica e não joga pro JSON
    private Set<Article> articles; // Usa Set pra não ter artigo repetido

}