package com.astroverse.backend.component;

import com.astroverse.backend.model.User;
import lombok.*;
import org.springframework.stereotype.Component;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Component
public class ChangeUserRequest {
    private User user;
    private String confermaPassword;
    private String vecchiaPassword;

}