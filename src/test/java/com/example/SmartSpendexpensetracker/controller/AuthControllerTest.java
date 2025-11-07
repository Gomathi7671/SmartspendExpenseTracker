package com.example.SmartSpendexpensetracker.controller;

import com.example.SmartSpendexpensetracker.exception.InvalidTokenException;
import com.example.SmartSpendexpensetracker.exception.UserNotFoundException;
import com.example.SmartSpendexpensetracker.model.UserSpend;
import com.example.SmartSpendexpensetracker.repository.UserspendRepository;
import com.example.SmartSpendexpensetracker.service.PasswordResetService;
import com.example.SmartSpendexpensetracker.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.ui.Model;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private UserspendRepository userRepository;

    @Mock
    private PasswordResetService passwordResetService;

    @Mock
    private HttpSession session;

    @Mock
    private Model model;

    @InjectMocks
    private AuthController authController;

    private UserSpend user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new UserSpend();
        user.setEmail("test@example.com");
        user.setPassword("password123");
        user.setFullName("Test User");
    }

    // -------------------- Home --------------------
    @Test
    void testHomePage() {
        String view = authController.homePage();
        assertEquals("index", view);
    }

    // -------------------- Signup --------------------
    @Test
    void testSignupForm() {
        String view = authController.signupForm(model);
        assertEquals("signup", view);
        verify(model).addAttribute(eq("userForm"), any(UserSpend.class));
    }

    @Test
    void testSignupSubmit() {
        when(userService.registerUser(anyString(), anyString(), anyString())).thenReturn(user);
        String view = authController.signupSubmit(user, model);
        assertEquals("message", view);
        verify(model).addAttribute(eq("message"), contains("Registration successful"));
    }

    // -------------------- Verify Token --------------------
    @Test
    void testVerifyValidToken() {
        when(userService.verifyToken(eq("valid-token"))).thenReturn(true);
        String view = authController.verify("valid-token", model);
        assertEquals("message", view);
        verify(model).addAttribute(eq("message"), contains("Email verified"));
    }

    @Test
    void testVerifyInvalidToken() {
        when(userService.verifyToken(eq("bad-token"))).thenReturn(false);
        assertThrows(InvalidTokenException.class,
                () -> authController.verify("bad-token", model));
    }

    // -------------------- Login --------------------
    @Test
    void testLoginPage() {
        String view = authController.loginPage(null, model);
        assertEquals("login", view);
    }

    @Test
    void testLoginPageWithError() {
        String view = authController.loginPage("true", model);
        assertEquals("login", view);
        verify(model).addAttribute("error", "Invalid credentials");
    }

    @Test
    void testHandleLoginValidUser() {
        when(userService.validateLogin(eq("test@example.com"), eq("password123"))).thenReturn(true);
        String view = authController.handleLogin("test@example.com", "password123", session);
        assertEquals("redirect:/dashboard", view);
        verify(session).setAttribute("userEmail", "test@example.com");
    }

    @Test
    void testHandleLoginInvalidUser() {
        when(userService.validateLogin(eq("test@example.com"), eq("wrong"))).thenReturn(false);
        assertThrows(UserNotFoundException.class,
                () -> authController.handleLogin("test@example.com", "wrong", session));
    }

    // -------------------- Forgot Password --------------------
    @Test
    void testForgotPasswordPage() {
        String view = authController.forgotPasswordPage();
        assertEquals("forgot-password", view);
    }

    @Test
    void testHandleForgotPassword() {
        when(userRepository.findByEmail(eq("test@example.com"))).thenReturn(Optional.of(user));
        String view = authController.handleForgotPassword("test@example.com", model);
        assertEquals("message", view);
        verify(passwordResetService).createPasswordResetTokenForUser(user);
    }

    // -------------------- Reset Password --------------------
    @Test
    void testResetPasswordPageValidToken() {
        when(passwordResetService.isValidPasswordResetToken(eq("valid"))).thenReturn(true);
        String view = authController.resetPasswordPage("valid", model);
        assertEquals("reset-password", view);
        verify(model).addAttribute("token", "valid");
    }

    @Test
    void testResetPasswordPageInvalidToken() {
        when(passwordResetService.isValidPasswordResetToken(eq("invalid"))).thenReturn(false);
        assertThrows(InvalidTokenException.class,
                () -> authController.resetPasswordPage("invalid", model));
    }

    @Test
    void testHandleResetPasswordValid() {
        when(passwordResetService.resetPassword(eq("valid"), eq("newPass"))).thenReturn(true);
        String view = authController.handleResetPassword("valid", "newPass", model);
        assertEquals("message", view);
        verify(model).addAttribute(eq("message"), contains("Password reset successfully"));
    }

    @Test
    void testHandleResetPasswordInvalid() {
        when(passwordResetService.resetPassword(eq("bad"), eq("newPass"))).thenReturn(false);
        assertThrows(InvalidTokenException.class,
                () -> authController.handleResetPassword("bad", "newPass", model));
    }

    // -------------------- Dashboard --------------------
    @Test
    void testDashboardValidUser() {
        when(session.getAttribute("userEmail")).thenReturn("test@example.com");
        when(userRepository.findByEmail(eq("test@example.com"))).thenReturn(Optional.of(user));

        String view = authController.dashboard(session, model);
        assertEquals("dashboard", view);
        verify(model).addAttribute("user", user);
    }

    @Test
    void testDashboardWithoutSession() {
        when(session.getAttribute("userEmail")).thenReturn(null);
        String view = authController.dashboard(session, model);
        assertEquals("redirect:/login", view);
    }

    @Test
    void testDashboardUserNotFound() {
        when(session.getAttribute("userEmail")).thenReturn("test@example.com");
        when(userRepository.findByEmail(eq("test@example.com"))).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class,
                () -> authController.dashboard(session, model));
    }

    // -------------------- Logout --------------------
    @Test
    void testLogout() {
        String view = authController.logout(session);
        assertEquals("redirect:/", view);
        verify(session).invalidate();
    }
}
