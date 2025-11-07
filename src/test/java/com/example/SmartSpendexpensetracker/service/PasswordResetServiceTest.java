package com.example.SmartSpendexpensetracker.service;

import com.example.SmartSpendexpensetracker.model.PasswordResetToken;
import com.example.SmartSpendexpensetracker.model.UserSpend;
import com.example.SmartSpendexpensetracker.repository.PasswordResetTokenRepository;
import com.example.SmartSpendexpensetracker.repository.UserspendRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PasswordResetServiceTest {

    @Mock
    private PasswordResetTokenRepository prRepo;

    @Mock
    private UserspendRepository userRepo;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PasswordResetService passwordResetService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ---------------------- createPasswordResetTokenForUser ----------------------
    @Test
    void testCreatePasswordResetTokenForUser_ShouldSaveTokenAndSendEmail() {
        UserSpend user = new UserSpend();
        user.setId(1L);
        user.setEmail("test@example.com");

        passwordResetService.createPasswordResetTokenForUser(user);

        ArgumentCaptor<PasswordResetToken> tokenCaptor = ArgumentCaptor.forClass(PasswordResetToken.class);
        verify(prRepo, times(1)).save(tokenCaptor.capture());
        verify(emailService, times(1)).sendPasswordResetEmail(eq("test@example.com"), anyString());

        PasswordResetToken savedToken = tokenCaptor.getValue();
        assertEquals(user.getId(), savedToken.getUserId());
        assertNotNull(savedToken.getToken());
    }

    // ---------------------- isValidPasswordResetToken ----------------------
    @Test
    void testIsValidPasswordResetToken_ShouldReturnTrue_WhenValid() {
        PasswordResetToken token = new PasswordResetToken("abc123", 1L, Instant.now().plusSeconds(3600));
        when(prRepo.findByToken("abc123")).thenReturn(Optional.of(token));

        assertTrue(passwordResetService.isValidPasswordResetToken("abc123"));
        verify(prRepo, never()).delete(any());
    }

    @Test
    void testIsValidPasswordResetToken_ShouldReturnFalse_WhenExpired() {
        PasswordResetToken token = new PasswordResetToken("abc123", 1L, Instant.now().minusSeconds(3600));
        when(prRepo.findByToken("abc123")).thenReturn(Optional.of(token));

        assertFalse(passwordResetService.isValidPasswordResetToken("abc123"));
        verify(prRepo, times(1)).delete(token);
    }

    @Test
    void testIsValidPasswordResetToken_ShouldReturnFalse_WhenNotFound() {
        when(prRepo.findByToken("notfound")).thenReturn(Optional.empty());

        assertFalse(passwordResetService.isValidPasswordResetToken("notfound"));
    }

    // ---------------------- resetPassword ----------------------
    @Test
    void testResetPassword_ShouldReturnTrue_WhenValid() {
        PasswordResetToken token = new PasswordResetToken("token123", 1L, Instant.now().plusSeconds(3600));
        UserSpend user = new UserSpend();
        user.setId(1L);
        user.setPassword("oldPass");

        when(prRepo.findByToken("token123")).thenReturn(Optional.of(token));
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newPass")).thenReturn("encodedPass");

        boolean result = passwordResetService.resetPassword("token123", "newPass");

        assertTrue(result);
        assertEquals("encodedPass", user.getPassword());
        verify(userRepo, times(1)).save(user);
        verify(prRepo, times(1)).delete(token);
    }

    @Test
    void testResetPassword_ShouldReturnFalse_WhenTokenExpired() {
        PasswordResetToken token = new PasswordResetToken("token123", 1L, Instant.now().minusSeconds(3600));
        when(prRepo.findByToken("token123")).thenReturn(Optional.of(token));

        boolean result = passwordResetService.resetPassword("token123", "newPass");

        assertFalse(result);
        verify(prRepo, times(1)).delete(token);
        verify(userRepo, never()).save(any());
    }

    @Test
    void testResetPassword_ShouldReturnFalse_WhenUserNotFound() {
        PasswordResetToken token = new PasswordResetToken("token123", 1L, Instant.now().plusSeconds(3600));
        when(prRepo.findByToken("token123")).thenReturn(Optional.of(token));
        when(userRepo.findById(1L)).thenReturn(Optional.empty());

        boolean result = passwordResetService.resetPassword("token123", "newPass");

        assertFalse(result);
        verify(userRepo, times(1)).findById(1L);
        verify(prRepo, never()).delete(token);
    }

    @Test
    void testResetPassword_ShouldReturnFalse_WhenTokenNotFound() {
        when(prRepo.findByToken("notfound")).thenReturn(Optional.empty());

        boolean result = passwordResetService.resetPassword("notfound", "newPass");

        assertFalse(result);
        verify(userRepo, never()).save(any());
        verify(prRepo, never()).delete(any());
    }
}
