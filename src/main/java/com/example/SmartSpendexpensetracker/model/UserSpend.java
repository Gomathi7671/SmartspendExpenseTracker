package com.example.SmartSpendexpensetracker.model;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
@Getter
@Setter

@Entity
public class UserSpend {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    
    private Long id;
    private String email;
    private String password; // BCrypt hashed
    private String fullName;
    private boolean enabled =true; // becomes true after email verification
    private Set<String> roles = new HashSet<>();
    private Instant createdAt = Instant.now();

    // getters and setters, constructors
    public UserSpend() {}
    public UserSpend(String email, String password, String fullName) {
        this.email = email; this.password = password; this.fullName = fullName;
    }

    // ... getters & setters
}

