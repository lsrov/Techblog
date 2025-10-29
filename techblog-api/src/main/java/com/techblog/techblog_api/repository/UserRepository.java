package com.techblog.techblog_api.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.techblog.techblog_api.model.User;

import java.util.Optional;

// Mexe com os usuários no banco
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Acha usuário pelo nome
    Optional<User> findByName(String name);
    
    // Acha usuário pelo email
    Optional<User> findByEmail(String email);

}