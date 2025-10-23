// package com.example.SmartSpendexpensetracker.service;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.mail.SimpleMailMessage;
// import org.springframework.mail.javamail.JavaMailSender;
// import org.springframework.messaging.simp.SimpMessagingTemplate;
// import org.springframework.stereotype.Service;

// @Service
// public class NotificationService {

//     @Autowired private JavaMailSender mailSender;
//     @Autowired private SimpMessagingTemplate messagingTemplate;

//     public void sendBudgetAlert(String email, String category, String type) {
//         String message = "Your spending in category '" + category + "' has " + type + "!";
//         sendEmail(email, "Budget Alert", message);
//         messagingTemplate.convertAndSend("/topic/notifications/" + email, message);
//     }

//     private void sendEmail(String to, String subject, String text) {
//         SimpleMailMessage mail = new SimpleMailMessage();
//         mail.setTo(to);
//         mail.setSubject(subject);
//         mail.setText(text);
//         mailSender.send(mail);
//     }
// }
