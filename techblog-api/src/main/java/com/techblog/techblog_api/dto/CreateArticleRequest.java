package com.techblog.techblog_api.dto;

import lombok.Data;
import java.util.List;

// Recebe os dados quando alguém cria um artigo novo
@Data //Lombok cria as outras coisas, então é só passar os atributos
public class CreateArticleRequest {
    private String title;    // Nome
    private String content;  // Texto
    private String imageUrl; // Imagem
    private List<String> tags; // Tags do artigo
    
}