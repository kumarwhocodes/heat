package com.zerobee.heat.utils;

import com.zerobee.heat.dto.UserDTO;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;

@Service
@RequiredArgsConstructor
public class EmailService {
    
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    @Value("${app.email.logo-path:/static/logo.png}")
    private String logoPath;
    
    public void sendWelcomeEmail(UserDTO userDTO) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            InternetAddress from = new InternetAddress(fromEmail, "Heat Support");
            helper.setFrom(from);
            
            helper.setTo(userDTO.getEmail());
            helper.setSubject("Welcome to HEAT!");
            helper.setReplyTo("support@zerobee.com");
            
            // Prepare context for Thymeleaf template
            Context context = new Context();
            context.setVariable("name", userDTO.getName());
            context.setVariable("username", userDTO.getEmail());
            context.setVariable("password", userDTO.getPassword());
            
            // Process the HTML template with Thymeleaf
            String emailContent = templateEngine.process("welcome-email", context);
            helper.setText(emailContent, true);
            
            // Add the logo as an inline attachment
            helper.addInline("logo", new ClassPathResource(logoPath));
            
            mailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException("Failed to send welcome email", e);
        }
    }
}
