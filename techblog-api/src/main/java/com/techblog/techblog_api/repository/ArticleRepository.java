package com.techblog.techblog_api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.techblog.techblog_api.model.Article;

// Aqui é onde mexe com os artigos no banco
public interface ArticleRepository extends JpaRepository<Article, Long> {
    // Pega todos os artigos com os detalhes (autor e tags)
    @Query("SELECT DISTINCT a FROM Article a " + "JOIN FETCH a.author " +
           "LEFT JOIN FETCH a.tags " + "ORDER BY a.createdAt DESC")
    // Puxo todos os artigos com autor e tags (LEFT JOIN porque tag pode ser = null) e organizo pela data
    List<Article> findAllWithDetails();

    // Busca UM artigo específico pelo ID, trazendo autor e tags junto
    // O distinct evita duplicata
    @Query("SELECT DISTINCT a FROM Article a " + "LEFT JOIN FETCH a.author " +
           "LEFT JOIN FETCH a.tags " + "WHERE a.id = :id")
    // Puxa o artigo com autor e com as tags com o id idêntico ao que foi passado
    Optional<Article> findWithDetailsById(Long id);

    // Busca artigos por tag
    @Query("SELECT DISTINCT a FROM Article a " + "JOIN FETCH a.author " +
           "LEFT JOIN FETCH a.tags t " + "WHERE t.name = :tagName " + "ORDER BY a.createdAt DESC")
    // Puxa os artigos com autor e tags, com o nome da tag igual ao que foi passado (esse t é uma "abreviação" para a tag, assim como o a é para o artigo)
    List<Article> findByTagName(String tagName);

    // Busca artigos por texto no título ou conteúdo
    @Query("SELECT DISTINCT a FROM Article a " + "JOIN FETCH a.author " +
           "LEFT JOIN FETCH a.tags " + "WHERE LOWER(a.title) LIKE LOWER(CONCAT('%', :query, '%')) "
           + "OR a.content LIKE CONCAT('%', :query, '%') " + "ORDER BY a.createdAt DESC")
           // Puxa os artigos, com o título ou conteúdo contendo o texto passado (case insensitive)
    List<Article> searchByTitleOrContent(String query);
    
}
