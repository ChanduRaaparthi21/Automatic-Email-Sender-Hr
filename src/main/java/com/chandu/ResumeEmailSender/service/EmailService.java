package com.chandu.ResumeEmailSender.service;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import java.io.*;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

	@Autowired
	private JavaMailSender mailSender;

	public void sendEmailWithAttachment(String to, String subject, String body, String attachmentPath) {
	    try {
	        MimeMessage message = mailSender.createMimeMessage();
	        MimeMessageHelper helper = new MimeMessageHelper(message, true);

	        helper.setTo(to);
	        helper.setSubject(subject);
	        helper.setText(body);

	        // Attach the resume
	        FileSystemResource file = new FileSystemResource(new File(attachmentPath));
	        helper.addAttachment("ChanduResume.pdf", file);

	        mailSender.send(message);
	        System.out.println("Email with attachment sent to: " + to);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
}