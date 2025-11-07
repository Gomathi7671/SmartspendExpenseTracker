package com.example.SmartSpendexpensetracker.service;

import com.example.SmartSpendexpensetracker.model.UserSpend;
import com.example.SmartSpendexpensetracker.model.VerificationToken;
import com.example.SmartSpendexpensetracker.repository.UserspendRepository;
import com.example.SmartSpendexpensetracker.repository.VerificationTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserspendRepository userRepo;

    @Mock
    private VerificationTokenRepository tokenRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

   

    @Test
    void testRegisterUser_ShouldThrow_WhenEmailExists() {
        String email = "test@example.com";
        when(userRepo.findByEmail(email)).thenReturn(Optional.of(new UserSpend()));

        assertThrows(IllegalArgumentException.class,
                () -> userService.registerUser(email, "pass", "Full Name"));

        verify(userRepo, never()).save(any());
        verify(tokenRepo, never()).save(any());
        verify(emailService, never()).sendVerificationEmail(any(), any());
    }

   
    @Test
    void testVerifyToken_ShouldReturnFalse_WhenTokenExpired() {
        UserSpend user = new UserSpend();
        VerificationToken token = new VerificationToken("token123", user, Instant.now().minusSeconds(1));

        when(tokenRepo.findByToken("token123")).thenReturn(Optional.of(token));
        doNothing().when(tokenRepo).delete(any());

        boolean result = userService.verifyToken("token123");

        assertFalse(result);
        verify(tokenRepo, times(1)).delete(token);
        verify(userRepo, never()).save(any());
    }

    @Test
    void testVerifyToken_ShouldReturnFalse_WhenTokenNotFound() {
        when(tokenRepo.findByToken("token123")).thenReturn(Optional.empty());
        boolean result = userService.verifyToken("token123");
        assertFalse(result);
        verify(userRepo, never()).save(any());
        verify(tokenRepo, never()).delete(any());
    }

    // ---------------------- validateLogin ----------------------
    @Test
    void testValidateLogin_ShouldReturnTrue_WhenPasswordMatchesAndEnabled() {
        String rawPassword = "pass";
        String encodedPassword = "encoded";

        UserSpend user = new UserSpend();
        user.setPassword(encodedPassword);
        user.setEnabled(true);

        when(userRepo.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);

        boolean result = userService.validateLogin("test@example.com", rawPassword);
        assertTrue(result);
    }

    @Test
    void testValidateLogin_ShouldReturnFalse_WhenPasswordDoesNotMatch() {
        UserSpend user = new UserSpend();
        user.setPassword("encoded");
        user.setEnabled(true);

        when(userRepo.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "encoded")).thenReturn(false);

        boolean result = userService.validateLogin("test@example.com", "wrong");
        assertFalse(result);
    }

    @Test
    void testValidateLogin_ShouldReturnFalse_WhenUserDisabled() {
        UserSpend user = new UserSpend();
        user.setPassword("encoded");
        user.setEnabled(false);

        when(userRepo.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("pass", "encoded")).thenReturn(true);

        boolean result = userService.validateLogin("test@example.com", "pass");
        assertFalse(result);
    }

    @Test
    void testValidateLogin_ShouldReturnFalse_WhenUserNotFound() {
        when(userRepo.findByEmail("notfound@example.com")).thenReturn(Optional.empty());
        boolean result = userService.validateLogin("notfound@example.com", "pass");
        assertFalse(result);
    }

    // ---------------------- findByEmail ----------------------
    @Test
    void testFindByEmail_ShouldReturnUser() {
        UserSpend user = new UserSpend();
        when(userRepo.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        assertEquals(user, userService.findByEmail("test@example.com"));
    }

    @Test
    void testFindByEmail_ShouldReturnNull_WhenNotFound() {
        when(userRepo.findByEmail("notfound@example.com")).thenReturn(Optional.empty());
        assertNull(userService.findByEmail("notfound@example.com"));
    }
}
