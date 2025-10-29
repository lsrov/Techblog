package com.techblog.techblog_api.dto;

import com.techblog.techblog_api.model.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// Classe pros comentários irem pro frontend
@Data // Lombok faz a mesma coisa do ArticleDTO
@NoArgsConstructor 
@AllArgsConstructor
public class CommentDTO {
    private Long id;
    private String content;
    private String authorName;
    private LocalDateTime createdAt;

    // Conversor
    public static CommentDTO fromEntity(Comment comment) {
        // Cria o DTO
        CommentDTO dto = new CommentDTO();
        
        // Copia os dados básicos
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        
        // Se o comentário tiver um usuário, usa o nome dele, senão usa "Anônimo"
        dto.setAuthorName(comment.getUser() != null ? comment.getUser().getName() : "Anônimo");
        
        dto.setCreatedAt(comment.getCreatedAt());
        
        return dto;
    }
}