package com.example.SmartSpendexpensetracker.service;


import com.example.SmartSpendexpensetracker.model.PasswordResetToken;
import com.example.SmartSpendexpensetracker.model.UserSpend;
import com.example.SmartSpendexpensetracker.repository.PasswordResetTokenRepository;
import com.example.SmartSpendexpensetracker.repository.UserspendRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetService {
    private final PasswordResetTokenRepository prRepo;
    private final UserspendRepository userRepo;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    public PasswordResetService(PasswordResetTokenRepository prRepo, UserspendRepository userRepo,
                                EmailService emailService, PasswordEncoder passwordEncoder) {
        this.prRepo = prRepo; this.userRepo = userRepo; this.emailService = emailService; this.passwordEncoder = passwordEncoder;
    }

    public void createPasswordResetTokenForUser(UserSpend user) {
        String token = UUID.randomUUID().toString();
        PasswordResetToken prt = new PasswordResetToken(token, user.getId(), Instant.now().plus(1, ChronoUnit.HOURS));
        prRepo.save(prt);
        emailService.sendPasswordResetEmail(user.getEmail(), token);
    }

    public boolean isValidPasswordResetToken(String token) {
        Optional<PasswordResetToken> p = prRepo.findByToken(token);
        if (p.isEmpty()) return false;
        if (p.get().getExpiryDate().isBefore(Instant.now())) {
            prRepo.delete(p.get());
            return false;
        }
        return true;
    }

    public boolean resetPassword(String token, String newPassword) {
    Optional<PasswordResetToken> p = prRepo.findByToken(token);
    if (p.isEmpty()) return false;

    PasswordResetToken prt = p.get();
    if (prt.getExpiryDate().isBefore(Instant.now())) {
        prRepo.delete(prt);
        return false;
    }

    Optional<UserSpend> userOpt = userRepo.findById(prt.getUserId()); // ✅ Fixed line

    if (userOpt.isEmpty()) return false;
    UserSpend u = userOpt.get();
    u.setPassword(passwordEncoder.encode(newPassword));
    userRepo.save(u);
    prRepo.delete(prt);
    return true;
}

}
