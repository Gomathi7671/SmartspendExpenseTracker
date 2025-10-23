package com.example.SmartSpendexpensetracker.service;

import com.example.SmartSpendexpensetracker.model.UserSpend;
import com.example.SmartSpendexpensetracker.model.VerificationToken;
import com.example.SmartSpendexpensetracker.repository.UserspendRepository;
import com.example.SmartSpendexpensetracker.repository.VerificationTokenRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserspendRepository userRepo;
    private final VerificationTokenRepository tokenRepo;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public UserService(UserspendRepository userRepo, VerificationTokenRepository tokenRepo,
                       PasswordEncoder passwordEncoder, EmailService emailService) {
        this.userRepo = userRepo;
        this.tokenRepo = tokenRepo;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    // Register new user
    public UserSpend registerUser(String email, String password, String fullName) {
        if (userRepo.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already in use");
        }

        UserSpend u = new UserSpend();
        u.setEmail(email);
        u.setPassword(passwordEncoder.encode(password));
        u.setFullName(fullName);
        u.getRoles().add("ROLE_USER");
        u.setEnabled(true);

        // Save user first
        UserSpend saved = userRepo.save(u);

        // Create verification token using the saved user
        String token = UUID.randomUUID().toString();
        VerificationToken vt = new VerificationToken(token, saved, Instant.now().plus(1, ChronoUnit.HOURS));
        tokenRepo.save(vt);

        // Send verification email
        emailService.sendVerificationEmail(saved.getEmail(), token);

        return saved;
    }

    // Verify token from email
    public boolean verifyToken(String token) {
        Optional<VerificationToken> found = tokenRepo.findByToken(token);
        if (found.isEmpty()) return false;

        VerificationToken vt = found.get();
        if (vt.getExpiryDate().isBefore(Instant.now())) {
            tokenRepo.delete(vt);
            return false;
        }

        // Get the associated user directly from token
        UserSpend user = vt.getUser();
        if (user != null) {
            user.setEnabled(true);
            userRepo.save(user);
            tokenRepo.delete(vt);
            return true;
        }

        return false;
    }

    // Validate login credentials (for non-Spring Security login)
    public boolean validateLogin(String email, String password) {
        Optional<UserSpend> userOpt = userRepo.findByEmail(email);
        if (userOpt.isPresent()) {
            UserSpend user = userOpt.get();
            // Compare passwords using encoder
            return passwordEncoder.matches(password, user.getPassword()) && user.isEnabled();
        }
        return false;
    }

    // Optional: fetch user by email
    public UserSpend findByEmail(String email) {
        return userRepo.findByEmail(email).orElse(null);
    }
}
