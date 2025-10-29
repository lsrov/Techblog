package com.techblog.techblog_api.controller;

import com.techblog.techblog_api.dto.CommentDTO;
import com.techblog.techblog_api.dto.CreateCommentRequest; 
import com.techblog.techblog_api.model.Article; 
import com.techblog.techblog_api.model.Comment;
import com.techblog.techblog_api.model.User; 
import com.techblog.techblog_api.repository.ArticleRepository; 
import com.techblog.techblog_api.repository.CommentRepository;
import com.techblog.techblog_api.repository.UserRepository; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus; 
import org.springframework.http.ResponseEntity; 
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import com.techblog.techblog_api.model.Comment;
import com.techblog.techblog_api.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

// Cuida dos comentários dos artigos do blog
@RestController // Diz que essa classe vai responder requisições REST
@RequestMapping("/api/articles/{articleId}/comments")
public class CommentController {

    // Preciso desses repositories pra mexer no banco
    @Autowired
    private CommentRepository commentRepository; // Pra mexer nos comentários

    @Autowired
    private UserRepository userRepository; // Pra achar quem fez o comentário

    @Autowired
    private ArticleRepository articleRepository; // Pra achar o artigo comentado

    // Pega todos os comentários de um artigo
    @GetMapping
    @Transactional // Não sei bem pra que serve, mas precisa pra funcionar
    public List<CommentDTO> getCommentsForArticle(@PathVariable Long articleId) {
        // Busca comentários do artigo com info de quem comentou
        List<Comment> comments = commentRepository.findByArticleIdWithUser(articleId);

        // Transforma em DTO pra mandar pro frontend
        return comments.stream()
                .map(CommentDTO::fromEntity) 
                .collect(Collectors.toList());
    }

    // Cria um comentário novo
    @PostMapping
    public ResponseEntity<CommentDTO> createComment(
            @PathVariable Long articleId,
            @RequestBody CreateCommentRequest request,
            Principal principal) {
        
        // Acha o usuário que tá comentando
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + principal.getName()));

        // Acha o artigo que vai receber o comentário
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("Artigo não encontrado com ID: " + articleId));

        // Faz o comentário novo
        Comment newComment = new Comment();
        newComment.setContent(request.getContent());
        newComment.setArticle(article);
        newComment.setUser(user);
        
        // Salva no banco
        Comment savedComment = commentRepository.save(newComment);

        // Devolve 201 (Created) com o comentário
        return ResponseEntity.status(HttpStatus.CREATED)
                           .body(CommentDTO.fromEntity(savedComment));
    }

}