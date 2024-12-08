//Codifica delle passworcon Spring Security
package com.astroverse.backend.model;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class Hash {
    private static final BCryptPasswordEncoder passEncoder = new BCryptPasswordEncoder();

    public static String hashPassword(String password) {
        return passEncoder.encode(password);
    }

    public static boolean checkPassword(String password, String hashedPassword) {
        return passEncoder.matches(password, hashedPassword);
    }
}