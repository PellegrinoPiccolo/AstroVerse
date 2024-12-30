package com.astroverse.backend.service;

import com.astroverse.backend.model.User;
import com.astroverse.backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class UserService {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    public void testSaveUser() {
        User user = new User();
        user.setEmail("provatest@gmail.com");
        user.setPassword("Prova!22");
        user.setNome("ProvaNome");
        user.setCognome("ProvaCognome");
        user.setUsername("UsernameProva");
        User savedUser = userService.saveUser(user);
    }
}