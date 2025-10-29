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

// Representa os comentários dos artigos no banco
@Entity // Esses comentários se tornam entidade
@Table(name = "comments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment {

    @Id // Chave primária
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // O texto do comentário
    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    // Se o comentário foi feito, aparece a data/hora junto
    @CreationTimestamp
    private LocalDateTime createdAt;

    // O artigo que recebeu o comentário
    @ManyToOne // Um artigo pode ter vários comentários
    @JoinColumn(name = "article_id", nullable = false)
    @ToString.Exclude // Só pra evitar os erros anteriores no Lombok
    @EqualsAndHashCode.Exclude
    private Article article;

    // O usuário que fez o comentário
    @ManyToOne // Um usuário pode fazer muitos comentários
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User user;

}