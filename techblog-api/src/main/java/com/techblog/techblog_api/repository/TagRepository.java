package com.techblog.techblog_api.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.techblog.techblog_api.model.Tag;

import java.util.Optional;

// Só mexe com as tags no banco
public interface TagRepository extends JpaRepository<Tag, Long> {
    
    // Acha uma tag pelo nome dela
    Optional<Tag> findByName(String name); // O Spring já cria a query sozinho
    
}