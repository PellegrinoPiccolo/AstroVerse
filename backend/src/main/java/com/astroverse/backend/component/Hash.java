//Codifica delle passworcon Spring Security
package com.astroverse.backend.component;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class Hash {
    private static final BCryptPasswordEncoder passEncoder = new BCryptPasswordEncoder();

    public static String hashPassword(String password) {
        return passEncoder.encode(password);
    }

    public static boolean checkPassword(String password, String hashedPassword) {
        return passEncoder.matches(password, hashedPassword);
    }
}