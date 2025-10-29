package com.techblog.techblog_api.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.techblog.techblog_api.model.Comment;

// Mexe com os comentários no banco
public interface CommentRepository extends JpaRepository<Comment, Long> {
    
    // Pega todos os comentários de um artigo
    @Query("SELECT c FROM Comment c " + "JOIN FETCH c.user " + "WHERE c.article.id = :articleId " +
           "ORDER BY c.createdAt DESC")
           // Puxa os comentários com o usuário que fez o comentário, com o id específico
    List<Comment> findByArticleIdWithUser(Long articleId);
    
    // Deleta todos os comentários de um artigo específico
    void deleteByArticleId(Long articleId);

}