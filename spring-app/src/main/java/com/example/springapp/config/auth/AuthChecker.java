package com.example.springapp.config.auth;

import org.springframework.stereotype.Component;

@Component
public class AuthChecker {
    public void isAdmin(String principal) {
        if (!"admin".equals(principal)) {
            throw new IllegalAuthException(principal);
        }
    }
}
