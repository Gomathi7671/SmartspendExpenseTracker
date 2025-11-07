package com.example.SmartSpendexpensetracker.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.Mockito.*;

class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    private final String appBaseUrl = "http://localhost:8080";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        emailService = new EmailService(mailSender, appBaseUrl);
    }

    // ---------------------- sendVerificationEmail ----------------------
    @Test
    void testSendVerificationEmail_ShouldSendEmailWithCorrectContent() {
        String to = "user@example.com";
        String token = "abc123";

        emailService.sendVerificationEmail(to, token);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(captor.capture());

        SimpleMailMessage sentMessage = captor.getValue();
        assert sentMessage.getTo()[0].equals(to);
        assert sentMessage.getSubject().equals("SmartSpend - Verify your email");
        assert sentMessage.getText().contains(appBaseUrl + "/verify?token=" + token);
    }

    // ---------------------- sendPasswordResetEmail ----------------------
    @Test
    void testSendPasswordResetEmail_ShouldSendEmailWithCorrectContent() {
        String to = "user@example.com";
        String token = "reset456";

        emailService.sendPasswordResetEmail(to, token);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(captor.capture());

        SimpleMailMessage sentMessage = captor.getValue();
        assert sentMessage.getTo()[0].equals(to);
        assert sentMessage.getSubject().equals("SmartSpend - Password reset");
        assert sentMessage.getText().contains(appBaseUrl + "/reset-password?token=" + token);
    }
}
