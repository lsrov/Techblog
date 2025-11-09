package com.techblog.techblog_api.dto;

import com.techblog.techblog_api.model.Article;
import com.techblog.techblog_api.model.Tag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

// Classe que empacota os dados do artigo pra mandar pro frontend
@Data // Lombok cria getters, setters e outros direto
@NoArgsConstructor // Cria construtor padrão
@AllArgsConstructor // Cria construtor com todos os campos
public class ArticleDTO {
    private Long id;
    private String title;
    private String content;
    private String imageUrl;
    private LocalDateTime createdAt;
    private Set<Tag> tags;

    // Informações do autor
    private Long authorId;
    private String authorName;

    // Controle de permissão
    private boolean canEdit; // Se o usuário logado pode editar esse artigo

    // Método que converte um Article (entidade) para ArticleDTO
    // É static porque não precisa de uma instância de ArticleDTO pra funcionar
    // Instância é um objeto criado da classe. Mas aqui só quero transformar dados
    public static ArticleDTO fromEntity(Article article, boolean canEdit) {
        // Cria um novo ArticleDTO
        ArticleDTO dto = new ArticleDTO();
        
        // Copia todos os dados do Article pro DTO
        dto.setId(article.getId());
        dto.setTitle(article.getTitle());
        dto.setContent(article.getContent());
        dto.setImageUrl(article.getImageUrl());
        dto.setCreatedAt(article.getCreatedAt());
        dto.setTags(article.getTags());
        
        // Dados do autor - com verificação de null
        if (article.getAuthor() != null) {
            dto.setAuthorId(article.getAuthor().getId());
            dto.setAuthorName(article.getAuthor().getName());
        } else {
            System.out.println("Artigo " + article.getId() + " não tem autor");
        }
        
        // Se o usuário pode editar
        dto.setCanEdit(canEdit);
        
        return dto;
    }
}