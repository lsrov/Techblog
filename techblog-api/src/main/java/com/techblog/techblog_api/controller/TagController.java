package com.techblog.techblog_api.controller;

import com.techblog.techblog_api.model.Tag;
import com.techblog.techblog_api.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// Só mostra as tags que existem no blog (bem simples)
@RestController // Diz que essa classe vai responder requisições REST
@RequestMapping("/api/tags")
public class TagController {

    // Preciso pra pegar as tags do banco
    @Autowired
    private TagRepository tagRepository;

    // Devolve todas as tags que existem
    @GetMapping
    public List<Tag> getAllTags() {
        // Só pega tudo que tem no banco e devolve
        return tagRepository.findAll();
    }
}