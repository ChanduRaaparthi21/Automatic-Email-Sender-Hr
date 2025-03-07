package com.chandu.ResumeEmailSender.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class EmailService {
	private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

	@Autowired
	private JavaMailSender mailSender;

	public void sendEmailWithAttachment(String to, String subject, String body, String attachmentPath) {
		try {
			// Validate email before sending
			if (to == null || to.trim().isEmpty() || !to.contains("@")) {
				logger.warn("Invalid email address: {}", to);
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
				logger.debug("Resume attached: {}", file.getName());
			} else {
				logger.error("Attachment file not found: {}", attachmentPath);
				throw new IllegalStateException("Resume file not found at: " + attachmentPath);
			}

			mailSender.send(message);
			logger.info("Email sent successfully to: {}", to);

		} catch (MessagingException e) {
			logger.error("Error while sending email to {}: {}", to, e.getMessage());
			throw new RuntimeException("Failed to send email", e);
		}
	}
}