package com.astroverse.backend.component;

import com.astroverse.backend.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChangeUserRequest {
    private User user;
    private String confermaPassword;

}