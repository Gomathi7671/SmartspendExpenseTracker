package com.example.SmartSpendexpensetracker.service;


import com.example.SmartSpendexpensetracker.model.UserSpend;
import com.example.SmartSpendexpensetracker.repository.UserspendRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserspendRepository repo;

    public CustomUserDetailsService(UserspendRepository repo) { this.repo = repo; }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserSpend u = repo.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (!u.isEnabled()) {
            // allow login only if enabled; optionally throw exception or show message
        }
        return new org.springframework.security.core.userdetails.User(
                u.getEmail(),
                u.getPassword(),
                u.isEnabled(),
                true, true, true,
                u.getRoles().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet())
        );
    }
}
