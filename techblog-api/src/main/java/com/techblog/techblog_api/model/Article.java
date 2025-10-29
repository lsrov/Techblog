package com.techblog.techblog_api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

// Representa um artigo do blog no banco
@Entity // "Crie uma entidade, ou seja, uma tabela"
@Table(name = "articles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Article {

    // Identificador único do artigo
    @Id // Chave primária
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Título do artigo (não pode ser nulo)
    @Column(nullable = false)
    private String title;

    // Conteúdo do artigo
    @Lob // "Lob" é "Large Object", uso para avisar que o conteúdo vai ser grande
    @Column(nullable = false, columnDefinition = "TEXT") // Não pode ser nulo também
    private String content;

    // URL da imagem de capa que nesse aso eu deixei como opcional
    private String imageUrl;

    // Data de criação (preenchida automaticamente)
    @CreationTimestamp
    private LocalDateTime createdAt;

    // Quem escreveu o artigo
    @ManyToOne(cascade = { CascadeType.MERGE }) // Um autor pode ter vários artigos
    @JoinColumn(name = "author_id", nullable = false)
    @ToString.Exclude // Se não colocar deu erro no Lombok
    @EqualsAndHashCode.Exclude // Outro erro no Lombok
    private User author;

    // Tags do artigo
    @ManyToMany(cascade = { CascadeType.MERGE }) // Um artigo -> várias tags; Várias tags -> vários artigos
    @JoinTable( // Essa tabela "liga" artigos com as tags
            name = "article_tags",
            joinColumns = @JoinColumn(name = "article_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Tag> tags = new HashSet<>(); // Usa Set pra não ter tag repetida
}