package com.vinay.futurevault.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(String to, String subject, String body) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }

    public void sendFutureNote(String to, String title, String note) {

        String subject = "🎉 Your Future Note is Unlocked!";

        String body = """
                Hello,

                Your Future Note is now available.

                -----------------------------
                Title:
                %s

                Message:
                %s
                -----------------------------

                Thank you for using Future Vault ❤️
                """.formatted(title, note);

        sendEmail(to, subject, body);
    }
}