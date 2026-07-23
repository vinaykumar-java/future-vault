package com.vinay.futurevault.controller;

import com.vinay.futurevault.service.EmailService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmailTestController {

    private final EmailService emailService;

    public EmailTestController(EmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping("/send-test-email")
    public String sendTestEmail() {

        emailService.sendEmail(
                "vinaykumar333b@gmail.com",
                "Future Vault Test",
                "Congratulations! Your Spring Boot email is working."
        );

        return "Email Sent Successfully!";
    }
}