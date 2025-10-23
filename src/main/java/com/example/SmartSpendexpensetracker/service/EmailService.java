package com.example.SmartSpendexpensetracker.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender mailSender;
    private final String appBaseUrl;

    public EmailService(JavaMailSender mailSender, @Value("${app.base-url}") String appBaseUrl) {
        this.mailSender = mailSender;
        this.appBaseUrl = appBaseUrl;
    }

    public void sendVerificationEmail(String to, String token) {
        String subject = "SmartSpend - Verify your email";
        String verifyUrl = appBaseUrl + "/verify?token=" + token;
        String text = "Welcome to SmartSpend!\n\nPlease verify your email by clicking the link:\n" + verifyUrl +
                "\n\nThis link expires in 24 hours.\n\nThanks,\nSmartSpend Team";

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(text);
        mailSender.send(msg);
    }

    public void sendPasswordResetEmail(String to, String token) {
        String subject = "SmartSpend - Password reset";
        String resetUrl = appBaseUrl + "/reset-password?token=" + token;
        String text = "Click to reset your password:\n" + resetUrl + "\nThis link expires in 1 hour.";
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(text);
        mailSender.send(msg);
    }
}
