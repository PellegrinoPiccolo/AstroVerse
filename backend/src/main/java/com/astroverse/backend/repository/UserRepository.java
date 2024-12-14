package com.astroverse.backend.repository;

import com.astroverse.backend.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    boolean existsByEmailAndIdNot(String email, Long id);
    boolean existsByUsernameAndIdNot(String username, Long id);
    boolean existsByUsername(String username);
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.cognome = :lastName WHERE u.username = :username")
    boolean updatePasswordByUsername(String username, String password);
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.nome = :nome, u.cognome = :cognome, u.email = :email, u.username = :username WHERE u.id = :id")
    int updateUserById(@Param("id") long id,
                       @Param("nome") String nome,
                       @Param("cognome") String cognome,
                       @Param("email") String email,
                       @Param("username") String username);
    User findByEmail(String email);
}