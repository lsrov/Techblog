package com.techblog.techblog_api.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.techblog.techblog_api.model.Article;
import com.techblog.techblog_api.model.Tag;
import com.techblog.techblog_api.model.User;
import com.techblog.techblog_api.repository.ArticleRepository;
import com.techblog.techblog_api.repository.TagRepository;
import com.techblog.techblog_api.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

// Essa classe serve para colocar dados iniciais no banco quando o sistema inicia
@Configuration
public class DataSeeder {
    
    // Método que vai rodar quando o Spring Boot iniciar
    // Coloca um usuário admin e alguns artigos no banco de dados
    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository,TagRepository tagRepository,
                                   ArticleRepository articleRepository, PasswordEncoder passwordEncoder) {
        return args -> { // args "não é usado" mas precisa estar aqui
            
            // Primeiro vou criar o admin se ele não existir
            if (userRepository.findByEmail("admin@techblog.com").isEmpty()) {
                // Mudar essa senha depois pq tá muito simples
                User admin = new User(); // jeito mais simples de criar, sem usar builder
                // Mas, quais são os benefícios do builder?
                // Dá pra criar objetos imutáveis, evitar erros com muitos parâmetros,
                // e deixar o códigoi mais legível
                admin.setName("Admin TechBlog");
                admin.setEmail("admin@techblog.com");
                admin.setPassword(passwordEncoder.encode("123456")); 
                
                userRepository.save(admin);
                System.out.println("==> Admin criado com sucesso!");
            }

            // Checa se já tem artigos no banco
            // Se tiver, não precisa fazer nada
            if (articleRepository.count() > 0) { // Não sei se esse if ficou eficiente
                System.out.println("==> Artigos já existem no banco!");
                return;
            }

            System.out.println("==> Colocando os artigos do arquivo JSON...");
            
            // Esse ObjectMapper ajuda a ler o JSON e transforma em objeto
            ObjectMapper mapper = new ObjectMapper();
            TypeReference<List<Map<String, Object>>> tipoLista = new TypeReference<>() {};
            
            // Pega o arquivo data.json que tá na pasta resources
            InputStream arquivoJson = new ClassPathResource("data.json").getInputStream();

            try { 
                // Lê o JSON e transforma numa lista de Maps
                List<Map<String, Object>> artigos = mapper.readValue(arquivoJson, tipoLista);

                // Percorre cada artigo do JSON
                for (Map<String, Object> artigo : artigos) { 
                    // Pega o nome do autor do artigo
                    String nomeAutor = (String) artigo.get("author");
                    
                    // Procura o autor no banco ou cria um novo se não existir
                    User autor = userRepository.findByName(nomeAutor).orElseGet(() -> {
                        // Cria um email fake pro autor baseado no nome dele
                        String emailFake = nomeAutor.toLowerCase().replace(" ", ".") + "@techblog.com";
                        
                        // Cria o autor novo
                        User novoAutor = new User();
                        novoAutor.setName(nomeAutor);
                        novoAutor.setEmail(emailFake);
                        novoAutor.setPassword(passwordEncoder.encode("senha123")); // senha padrão
                        
                        return userRepository.save(novoAutor);
                    });
                    
                    // Agora vou pegar as tags do artigo
                    Set<Tag> tags = new HashSet<>(); // uso Set pra não ter tag repetida
                    
                    // O JSON tem tag1, tag2 e tag3
                    String[] camposTags = {"tag1", "tag2", "tag3"}; // array com os nomes dos campos
                    for (String campoTag : camposTags) {
                        // Só adiciona a tag se ela existir no JSON
                        if (artigo.get(campoTag) != null) {
                            String nomeTag = (String) artigo.get(campoTag);
                            
                            // Procura a tag no banco ou cria uma nova
                            Tag tag = tagRepository.findByName(nomeTag).orElseGet(() -> {
                                Tag novaTag = new Tag();
                                novaTag.setName(nomeTag);
                                return tagRepository.save(novaTag);
                            });
                            
                            tags.add(tag);
                        }
                    }

                    // Finalmente cria o artigo
                    Article novoArtigo = new Article();
                    novoArtigo.setTitle((String) artigo.get("title"));
                    novoArtigo.setContent((String) artigo.get("content"));
                    novoArtigo.setAuthor(autor);
                    novoArtigo.setTags(tags);
                    novoArtigo.setImageUrl((String) artigo.get("imageUrl"));

                    // Salva o artigo no banco
                    articleRepository.save(novoArtigo);
                }
                
                System.out.println("==> Banco populado com sucesso!");
            } catch (Exception erro) {
                // Se der algum erro, mostra na tela
                System.out.println("==> ERRO!! Algo deu errado: " + erro.getMessage());
                erro.printStackTrace(); // isso ajuda a ver o erro todo
                // o printStackTrace mostra as chamadas que levaram ao erro
            }
        };
    }
}