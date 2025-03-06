package com.chandu.ResumeEmailSender.controller;

import com.chandu.ResumeEmailSender.model.HrDetails;
import com.chandu.ResumeEmailSender.service.EmailService;
import com.chandu.ResumeEmailSender.service.ExcelReaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ResumeMailerController {

    @Autowired
    private ExcelReaderService excelReaderService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ApplicationContext applicationContext;

    @Value("${resume.file.path}")
    private String resumePath; // Load from application.properties

    @GetMapping("/send-emails")
    public String sendEmails() {
        List<HrDetails> hrDetailsList = excelReaderService.readHrDetails();

        if (hrDetailsList.isEmpty()) {
            return "No HR details found in the Excel file.";
        }

        sendEmailsInBatches(hrDetailsList);

        return "Emails are being sent in batches with delays. Check logs for progress.";
    }

    @Async // Runs email sending in a background thread
    public void sendEmailsInBatches(List<HrDetails> hrDetailsList) {
        for (HrDetails hrDetails : hrDetailsList) {
            String hrEmail = hrDetails.getHrEmail();

            // Validate email
            if (hrEmail == null || hrEmail.trim().isEmpty() || !hrEmail.contains("@")) {
                System.out.println("Skipping invalid email: " + hrEmail);
                continue;
            }

            String subject = "Java Backend Developer | Opportunities at " + hrDetails.getCompanyName();

            String body = "Dear " + hrDetails.getHrName() + ",\n\n" +
                    "I hope you’re doing well. I’m Chandu Raparthi, an Associate Java Developer at Edgerock Software Solutions, looking for Java Backend opportunities at " + hrDetails.getCompanyName() + ".\n\n" +
                    "I have 2.3 years of experience in Java, Spring Boot, Hibernate, and Microservices, specializing in REST APIs, PostgreSQL/MySQL optimization, and security (JWT, OAuth2).\n\n" +
                    "Please find my resume attached. Looking forward to your response.\n\n" +
                    "Best regards,\n" +
                    "Chandu Raparthi\n" +
                    "+91 94523 01058 | chanduraparthi21@gmail.com";

            // Send email with attachment
            emailService.sendEmailWithAttachment(hrEmail, subject, body, resumePath);
            System.out.println("Email sent to: " + hrEmail);

            try {
                // Delay of 2 minutes before sending the next email
                Thread.sleep(120000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Email sending interrupted: " + e.getMessage());
            }
        }

        System.out.println("All emails sent. Shutting down application...");
        int exitCode = SpringApplication.exit(applicationContext, () -> 0);
        System.exit(exitCode);
    }
}
