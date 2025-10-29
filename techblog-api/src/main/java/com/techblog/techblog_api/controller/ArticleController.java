package com.techblog.techblog_api.controller;

import org.springframework.transaction.annotation.Transactional;
import com.techblog.techblog_api.dto.ArticleDTO;
import com.techblog.techblog_api.dto.CreateArticleRequest;
import com.techblog.techblog_api.model.Article;
import com.techblog.techblog_api.repository.CommentRepository;
import com.techblog.techblog_api.model.Tag;
import com.techblog.techblog_api.model.User;
import com.techblog.techblog_api.repository.ArticleRepository;
import com.techblog.techblog_api.repository.TagRepository;
import com.techblog.techblog_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// Essa classe controla todas as operações relacionadas aos artigos do blog
@RestController // Diz que essa classe vai responder requisições REST (cliente -> servidor)
@RequestMapping("/api/articles")
public class ArticleController {

    // Preciso desses repositories para acessar o banco de dados
    // @Autowired faz a injeção automática, não precisa criar objetos
    @Autowired
    private ArticleRepository articleRepository; // Para mexer nos artigos
    @Autowired
    private UserRepository userRepository; // Para verificar os usuários
    @Autowired
    private TagRepository tagRepository; // Para mexer com as tags dos artigos

    // Busca todos os artigos do blog
    // Não sei bem para que serve o @Transactional, mas resolveu meu problema com leitura no Banco de Dados
    @Transactional(readOnly = true)
    @GetMapping // Responde a GET em /api/articles
    public List<ArticleDTO> getAllArticles(Principal principal) { // Principal tem info do usuário logado
        // Busca todos os artigos com os detalhes
        List<Article> articles = articleRepository.findAllWithDetails();
        // Converte os artigos para DTOs (Data Transfer Objects) que são "pacotes" que transitam entre o front e o back
        return convertToDTOs(articles, principal);
    }

    // Busca artigos que contêm um texto específico no título ou conteúdo
    @GetMapping("/search") // Responde a GET em /api/articles/search?q=texto
    @Transactional(readOnly = true)
    public List<ArticleDTO> searchArticles(@RequestParam String q, Principal principal) {
        // @RequestParam pega o parâmetro 'q' da URL (?q=texto)
        List<Article> articles = articleRepository.searchByTitleOrContent(q);
        return convertToDTOs(articles, principal);
    }

    // Busca artigos por uma tag específica
    @GetMapping("/tag") // Responde a GET em /api/articles/tag?name=nometag
    @Transactional(readOnly = true)
    public List<ArticleDTO> getArticlesByTag(@RequestParam("name") String tagName, Principal principal) {
        // Busca todos os artigos que têm a tag especificada
        List<Article> articles = articleRepository.findByTagName(tagName);
        return convertToDTOs(articles, principal);
    }

    // Método auxiliar que converte uma lista de Articles para ArticleDTOs
    // É private porque só é usado dentro dessa classe
    private List<ArticleDTO> convertToDTOs(List<Article> articles, Principal principal) {
        // Usa stream para processar a lista de forma mais elegante
        // TODO: Talvez usar um for normal seja mais fácil de entender?
        return articles.stream()
                .map(article -> {
                    // Verifica se o usuário logado pode editar o artigo
                    boolean canEdit = isUserOwner(principal, article);
                    // Converte para DTO com a informação se pode editar
                    return ArticleDTO.fromEntity(article, canEdit);
                })
                .collect(Collectors.toList()); // Transforma o stream de volta em lista
    }
    
    // Busca um artigo específico pelo ID
    @GetMapping("/{id}") // O {id} na URL vira o parâmetro do método
    @Transactional(readOnly = true)
    public ResponseEntity<ArticleDTO> getArticleById(@PathVariable Long id, Principal principal) {
        // Tenta achar o artigo, se não achar lança exceção
        Article article = articleRepository.findWithDetailsById(id)
                .orElseThrow(() -> new RuntimeException("Artigo não encontrado com ID: " + id));
        // Checa se o usuário pode editar
        boolean canEdit = isUserOwner(principal, article);
        // Retorna 200 OK com o artigo convertido para DTO
        return ResponseEntity.ok(ArticleDTO.fromEntity(article, canEdit));
    }

    // Checa se o usuário logado é o dono do artigo
    // Tem várias verificações de null pra evitar erros
    private boolean isUserOwner(Principal principal, Article article) {
        // Se não tem ninguém logado, não pode editar
        if (principal == null) {
            return false;
        }

        // Se o artigo não tem autor (!?), não pode editar
        if (article.getAuthor() == null) {
            return false;
        }

        // Se o autor não tem email (!?), não pode editar
        if (article.getAuthor().getEmail() == null) {
            return false;
        }

        // Compara o email do usuário logado com o email do autor
        return principal.getName().equals(article.getAuthor().getEmail());
    }

    // Verifica se o usuário tem permissão para mexer no artigo
    // Lança exceção se não tiver permissão
    private void verifyOwnership(Article article, Principal principal) {
        // Primeiro verifica se tem alguém logado
        if (principal == null) {
            throw new AccessDeniedException("Acesso negado. Você precisa estar logado.");
        }

        // Depois busca o usuário no banco pelo email
        // Se não achar o usuário, algo muito errado aconteceu
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + principal.getName()));

        // Log para debug
        System.out.println("Verificando propriedade do artigo:");
        System.out.println("- ID do artigo: " + article.getId());
        System.out.println("- Título do artigo: " + article.getTitle());
        System.out.println("- Autor do artigo: " + (article.getAuthor() != null ? article.getAuthor().getEmail() : "null"));
        System.out.println("- Usuário logado: " + principal.getName());
        System.out.println("- ID do usuário: " + user.getId());

        // Finalmente verifica se o usuário é o autor do artigo
        if (article.getAuthor() == null) {
            throw new AccessDeniedException("Erro: O artigo não tem autor definido.");
        }

        if (!article.getAuthor().getId().equals(user.getId())) {
            throw new AccessDeniedException(
                "Acesso negado. Você (" + principal.getName() + 
                ") não é o autor deste artigo (autor: " + article.getAuthor().getEmail() + ")");
        }
    }

    // Cria um novo artigo
    @PostMapping // Responde a POST em /api/articles
    @Transactional // Precisa ser transactional porque vamos alterar o banco
    public ResponseEntity<ArticleDTO> createArticle(@RequestBody CreateArticleRequest request, Principal principal) {
        // Log para debug do início da criação
        System.out.println("Iniciando criação de artigo para usuário: " + principal.getName());
        
        // Primeiro pega o autor que é o usuário logado
        User author = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + principal.getName()));
        
        // Log do autor encontrado
        System.out.println("Autor encontrado: ID=" + author.getId() + ", Nome=" + author.getName());
        
        // Cria um conjunto para guardar as tags do artigo
        Set<Tag> tagObjects = new HashSet<>(); // Usa HashSet pra não ter tag repetida
        
        // Se o request veio com tags
        if (request.getTags() != null) {
            // Para cada nome de tag no request
            for (String tagName : request.getTags()) {
                // Limpa o nome da tag (tira espaços e põe em minúsculo)
                String processedTagName = tagName.trim().toLowerCase();
                if (processedTagName.isEmpty()) continue; // Pula tags vazias
                
                // Procura a tag no banco ou cria uma nova
                Tag tag = tagRepository.findByName(processedTagName)
                        .orElseGet(() -> {
                            Tag novaTag = new Tag();
                            novaTag.setName(processedTagName);
                            return tagRepository.save(novaTag);
                        });
                tagObjects.add(tag);
                System.out.println("Tag adicionada: " + tag.getName());
            }
        }
        
        // Cria o novo artigo com os dados do request
        Article newArticle = new Article();
        newArticle.setTitle(request.getTitle());
        newArticle.setContent(request.getContent());
        newArticle.setImageUrl(request.getImageUrl());
        newArticle.setAuthor(author);
        newArticle.setTags(tagObjects);
        newArticle.setCreatedAt(LocalDateTime.now()); // Marca a data/hora atual
        
        // Log antes de salvar
        System.out.println("Artigo pronto para salvar:");
        System.out.println("- Título: " + newArticle.getTitle());
        System.out.println("- Autor ID: " + newArticle.getAuthor().getId());
        System.out.println("- Número de tags: " + newArticle.getTags().size());
        
        // Salva o artigo no banco
        Article savedArticle = articleRepository.save(newArticle);
        
        // Log depois de salvar
        System.out.println("Artigo salvo com ID: " + savedArticle.getId());
        
        // Busca o artigo completo do banco para garantir que tudo foi salvo
        Article articleWithDetails = articleRepository.findWithDetailsById(savedArticle.getId())
                .orElseThrow(() -> new RuntimeException("Erro ao recuperar artigo salvo"));
                
        // Converte para DTO e retorna
        ArticleDTO articleDTO = ArticleDTO.fromEntity(articleWithDetails, true);
        
        // Retorna 201 (Created) com o artigo salvo como DTO
        return ResponseEntity.status(HttpStatus.CREATED).body(articleDTO);
    }

    // Atualiza um artigo existente
    @PutMapping("/{id}") // Responde a PUT em /api/articles/{id}
    @Transactional
    public ResponseEntity<ArticleDTO> updateArticle(@PathVariable Long id,
                                                @RequestBody CreateArticleRequest request,
                                                Principal principal) {

        // Primeiro busca o artigo que vai ser atualizado
        Article articleToUpdate = articleRepository.findWithDetailsById(id)
                .orElseThrow(() -> new RuntimeException("Artigo não encontrado"));

        // Verifica se o usuário pode editar esse artigo
        verifyOwnership(articleToUpdate, principal);

        // Processo das tags é igual ao de criar artigo
        Set<Tag> tagObjects = new HashSet<>();
        if (request.getTags() != null) {
            for (String tagName : request.getTags()) {
                // Limpa o nome da tag
                String processedTagName = tagName.trim().toLowerCase();
                if (processedTagName.isEmpty()) continue;
                
                // Busca ou cria a tag
                Tag tag = tagRepository.findByName(processedTagName)
                        .orElseGet(() -> {
                            Tag novaTag = new Tag();
                            novaTag.setName(processedTagName);
                            return tagRepository.save(novaTag);
                        });
                tagObjects.add(tag);
            }
        }


        // Atualiza os dados do artigo com os novos valores
        articleToUpdate.setTitle(request.getTitle());
        articleToUpdate.setContent(request.getContent());
        articleToUpdate.setImageUrl(request.getImageUrl());
        articleToUpdate.setTags(tagObjects);

        // Salva as alterações no banco
        Article savedArticle = articleRepository.save(articleToUpdate);

        // Retorna 200 (OK) com o artigo atualizado
        // true no fromEntity porque já verificamos que o usuário pode editar
        return ResponseEntity.ok(ArticleDTO.fromEntity(savedArticle, true));
    }

    // Deleta um artigo
    @Autowired
    private CommentRepository commentRepository;
    
    @DeleteMapping("/{id}") // Responde a DELETE em /api/articles/{id}
    @Transactional
    public ResponseEntity<Void> deleteArticle(@PathVariable Long id, Principal principal) {
        // Primeiro busca o artigo que vai ser deletado com TODOS os detalhes
        Article articleToDelete = articleRepository.findWithDetailsById(id)
                .orElseThrow(() -> new RuntimeException("Artigo não encontrado para exclusão"));

        // Verifica se o usuário pode deletar esse artigo
        verifyOwnership(articleToDelete, principal);

        // Primeiro deleta todos os comentários associados ao artigo
        commentRepository.deleteByArticleId(id);
        
        // Limpa as referências para evitar problemas de constraint
        articleToDelete.setTags(new HashSet<>()); // Remove todas as tags
        articleRepository.save(articleToDelete); // Salva sem as tags

        // Agora sim deleta o artigo do banco
        articleRepository.delete(articleToDelete);

        // Retorna 204 (No Content) porque deu certo mas não tem nada pra retornar
        return ResponseEntity.noContent().build();
    }
}