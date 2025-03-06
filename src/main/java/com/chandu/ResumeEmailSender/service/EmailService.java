package com.chandu.ResumeEmailSender.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
public class EmailService {

	@Autowired
	private JavaMailSender mailSender;

	@Async // Runs in a separate thread
	public void sendEmailsWithDelay(List<String> recipients, String subject, String body, String attachmentPath) {
		for (String recipient : recipients) {
			try {
				sendEmailWithAttachment(recipient, subject, body, attachmentPath);
				System.out.println("Email sent to: " + recipient);

				// Delay of 2 minutes before sending the next email
				Thread.sleep(120000); // 120,000 milliseconds = 2 minutes

			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				System.err.println("Email sending interrupted: " + e.getMessage());
			} catch (Exception e) {
				System.err.println("Failed to send email to " + recipient + ": " + e.getMessage());
			}
		}
	}

	public void sendEmailWithAttachment(String to, String subject, String body, String attachmentPath) {
		try {
			// Validate email before sending
			if (to == null || to.trim().isEmpty() || !to.contains("@")) {
				System.out.println("Invalid email address: " + to);
				return;
			}

			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true);

			helper.setTo(to);
			helper.setSubject(subject);
			helper.setText(body);

			// Attach the file
			File file = new File(attachmentPath);
			if (file.exists()) {
				helper.addAttachment(file.getName(), file);
			} else {
				System.out.println("Attachment file not found: " + attachmentPath);
			}

			mailSender.send(message);
			System.out.println("Email sent successfully to: " + to);

		} catch (MessagingException e) {
			System.out.println("Error while sending email to " + to + ": " + e.getMessage());
			e.printStackTrace();
		}
	}
}
