package com.chandu.ResumeEmailSender.schedulars;

import com.chandu.ResumeEmailSender.model.HrDetails;
import com.chandu.ResumeEmailSender.service.EmailService;
import com.chandu.ResumeEmailSender.service.ExcelReaderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class EmailSchedulerService {
    private static final Logger logger = LoggerFactory.getLogger(EmailSchedulerService.class);

    @Autowired
    private ExcelReaderService excelReaderService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ApplicationContext applicationContext;

    @Value("${resume.file.path}")
    private String resumePath;

    private List<HrDetails> hrDetailsList;
    private AtomicInteger currentIndex = new AtomicInteger(0);
    private boolean initialized = false;

    // Initialize the HR details list before starting the schedule
    public void initialize() {
        if (!initialized) {
            hrDetailsList = excelReaderService.readHrDetails();
            logger.info("Initialized with {} HR contacts", hrDetailsList.size());
            initialized = true;
        }
    }

    // Schedule to run every 2 minutes
    @Scheduled(fixedDelayString = "${email.send.interval:120000}")
    public void sendNextBatchOfEmails() {
        if (!initialized) {
            initialize();
        }

        if (hrDetailsList == null || hrDetailsList.isEmpty()) {
            logger.warn("No HR details found. Stopping scheduler.");
            shutdownApplication();
            return;
        }

        int index = currentIndex.getAndIncrement();

        if (index >= hrDetailsList.size()) {
            logger.info("All emails have been sent. Shutting down application...");
            shutdownApplication();
            return;
        }

        HrDetails hrDetails = hrDetailsList.get(index);
        sendEmailToHr(hrDetails);

        logger.info("Progress: {}/{} emails sent", index + 1, hrDetailsList.size());
    }

    private void sendEmailToHr(HrDetails hrDetails) {
        String hrEmail = hrDetails.getHrEmail();

        // Validate email
        if (hrEmail == null || hrEmail.trim().isEmpty() || !hrEmail.contains("@")) {
            logger.warn("Skipping invalid email: {}", hrEmail);
            return;
        }

        String subject = "Java Backend Developer | Opportunities at " + hrDetails.getCompanyName();

        String body = "Dear " + hrDetails.getHrName() + ",\n\n" +
                "I hope you're doing well. I'm Chandu Raparthi, an Associate Java Developer at Edgerock Software Solutions, looking for Java Backend opportunities at " + hrDetails.getCompanyName() + ".\n\n" +
                "I have 2.3 years of experience in Java, Spring Boot, Hibernate, and Microservices, specializing in REST APIs, PostgreSQL/MySQL optimization, and security (JWT, OAuth2).\n\n" +
                "Please find my resume attached. Looking forward to your response.\n\n" +
                "Best regards,\n" +
                "Chandu Raparthi\n" +
                "+91 94523 01058 | chanduraparthi21@gmail.com";

        // Send email with attachment
        try {
            emailService.sendEmailWithAttachment(hrEmail, subject, body, resumePath);
            logger.info("Email sent to: {} at {}", hrEmail, hrDetails.getCompanyName());
        } catch (Exception e) {
            logger.error("Failed to send email to {}: {}", hrEmail, e.getMessage());
        }
    }

    private void shutdownApplication() {
        logger.info("Email sending process completed. Shutting down application...");
        new Thread(() -> {
            try {
                Thread.sleep(5000); // Give logging time to complete
                int exitCode = SpringApplication.exit(applicationContext, () -> 0);
                System.exit(exitCode);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }
}