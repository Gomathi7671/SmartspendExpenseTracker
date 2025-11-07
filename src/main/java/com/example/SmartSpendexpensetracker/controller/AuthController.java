package com.example.SmartSpendexpensetracker.controller;

import com.example.SmartSpendexpensetracker.exception.InvalidTokenException;
import com.example.SmartSpendexpensetracker.exception.UserNotFoundException;
import com.example.SmartSpendexpensetracker.model.UserSpend;
import com.example.SmartSpendexpensetracker.repository.UserspendRepository;
import com.example.SmartSpendexpensetracker.service.PasswordResetService;
import com.example.SmartSpendexpensetracker.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    private final UserService userService;
    private final UserspendRepository userRepository;
    private final PasswordResetService passwordResetService;

    public AuthController(UserService userService, UserspendRepository userRepository, PasswordResetService passwordResetService) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.passwordResetService = passwordResetService;
    }

    // Home page
    @GetMapping("/")
    public String homePage() {
        return "index";
    }

    // -------------------- Signup --------------------
    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("userForm", new UserSpend());
        return "signup";
    }

    @PostMapping("/signup")
    public String signupSubmit(@ModelAttribute("userForm") @Valid UserSpend user, Model model) {
        userService.registerUser(user.getEmail(), user.getPassword(), user.getFullName());
        model.addAttribute("message", "Registration successful! Check your email to verify your account.");
        return "message";
    }

    // -------------------- Email Verification --------------------
    @GetMapping("/verify")
    public String verify(@RequestParam("token") String token, Model model) {
        boolean ok = userService.verifyToken(token);
        if (!ok) throw new InvalidTokenException("Invalid or expired verification link.");
        model.addAttribute("message", "Email verified! You can now login.");
        return "message";
    }

    // -------------------- Login --------------------
    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) model.addAttribute("error", "Invalid credentials");
        return "login";
    }

    @PostMapping("/login")
    public String handleLogin(@RequestParam("email") String email,
                              @RequestParam("password") String password,
                              HttpSession session) {
        if (!userService.validateLogin(email, password)) {
            throw new UserNotFoundException("Invalid email or password");
        }
        session.setAttribute("userEmail", email);
        return "redirect:/dashboard";
    }

    // -------------------- Forgot Password --------------------
    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String handleForgotPassword(@RequestParam("email") String email, Model model) {
        userRepository.findByEmail(email)
                .ifPresent(user -> passwordResetService.createPasswordResetTokenForUser(user));
        model.addAttribute("message", "If an account exists, a reset link was sent to your email.");
        return "message";
    }

    // -------------------- Reset Password --------------------
    @GetMapping("/reset-password")
    public String resetPasswordPage(@RequestParam("token") String token, Model model) {
        boolean valid = passwordResetService.isValidPasswordResetToken(token);
        if (!valid) throw new InvalidTokenException("Invalid or expired token");
        model.addAttribute("token", token);
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String handleResetPassword(@RequestParam("token") String token,
                                      @RequestParam("password") String password,
                                      Model model) {
        boolean ok = passwordResetService.resetPassword(token, password);
        if (!ok) throw new InvalidTokenException("Could not reset password (invalid/expired token).");
        model.addAttribute("message", "Password reset successfully! You can now login.");
        return "message";
    }

    // -------------------- Dashboard --------------------
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        String email = (String) session.getAttribute("userEmail");
        if (email == null) return "redirect:/login";

        UserSpend user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        model.addAttribute("user", user);
        return "dashboard";
    }

    // -------------------- Logout --------------------
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
