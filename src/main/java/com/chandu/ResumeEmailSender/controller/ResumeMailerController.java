package com.chandu.ResumeEmailSender.controller;

import com.chandu.ResumeEmailSender.service.EmailService;
import com.chandu.ResumeEmailSender.service.ExcelReaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ResumeMailerController {

    @Autowired
    private ExcelReaderService excelReaderService;

    @Autowired
    private EmailService emailService;

    @GetMapping("/send-emails")
    public String sendEmails() {
        var hrDetailsList = excelReaderService.readHrDetails();

        // Path to your resume file
        String resumePath = "\"C:\\Users\\chand\\eclipse-workspace\\ResumeEmailSender\\src\\main\\resources\\ChanduResume.pdf\""; // Update this path

        for (var hrDetails : hrDetailsList) {
            String subject = "Application for Opportunities at " + hrDetails.getCompanyName();
            String body = "Dear " + hrDetails.getHrName() + ",\n\n" +
                    "I hope this message finds you well. My name is Chandu Raparthi, and I am reaching out to express my interest in potential opportunities at " + hrDetails.getCompanyName() + ".\n\n" +
                    "I have over 2.5 years of experience in IT development, specializing in advanced Java technologies. My expertise includes Java SE, Java EE, and contemporary frameworks such as Spring and Hibernate, which I have used to develop robust and scalable applications.\n\n" +
                    "Thank you for your time and consideration.\n\n" +
                    "Best regards,\n" +
                    "Chandu Raparthi\n" +
                    "+91 94523 01058 | chanduraparthi21@gmail.com";

            // Send email with attachment
            emailService.sendEmailWithAttachment(hrDetails.getHrEmail(), subject, body, resumePath);
        }

        return "Emails with attachments sent successfully!";
    }
}