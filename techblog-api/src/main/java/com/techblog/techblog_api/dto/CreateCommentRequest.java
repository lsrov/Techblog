package com.techblog.techblog_api.dto;

import lombok.Data;

// Só recebe o texto do comentário
@Data // Lombok faz tudo (não precisa de construtor, getters e setters)
public class CreateCommentRequest {
    private String content;
    
    // TODO: Validação também
}