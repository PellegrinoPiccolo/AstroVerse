package com.astroverse.backend.service;

import com.astroverse.backend.model.User;
import com.astroverse.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User saveUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email già in uso");
        }
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username già in uso");
        }
        return userRepository.save(user);
    }

    public User getUser(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("Utente non esiste");
        }
        return user;
    }

    public User changeUserData(long id, String email, String username, String name, String lastName) {
        if(userRepository.existsByEmailAndIdNot(email, id)) {
            throw new IllegalArgumentException("Email già in uso");
        } else if(userRepository.existsByUsernameAndIdNot(username, id)) {
            throw new IllegalArgumentException("Username già in uso");
        }
        User user = new User();
        if (userRepository.updateUserById(id, name, lastName, email, username) == 0) {
            throw new RuntimeException("Errore nella modifica dell'utente ");
        }
        user.setEmail(email);
        user.setUsername(username);
        user.setNome(name);
        user.setCognome(lastName);
        return user;
    }

    public void changePassword(String username, String password) {
        if (!userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Utente non esistente");
        }
        if (userRepository.updatePasswordByUsername(username, password) == 0) {
            throw new RuntimeException("Errore nella modifica della password");
        }
    }
}