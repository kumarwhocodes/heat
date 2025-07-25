package com.zerobee.heat.service;

import com.zerobee.heat.dto.UserDTO;
import com.zerobee.heat.dto.email.EmailRequest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceSMTP {
    
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    @Value("${app.name:HEAT}")
    private String appName;
    
    @Value("${app.email.logo-path:/static/logo.png}")
    private String logoPath;
    
    // Generic method to send simple text emails
    public void sendSimpleEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            
            mailSender.send(message);
            log.info("Simple email sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send simple email to: {}", to, e);
            throw new RuntimeException("Failed to send email", e);
        }
    }
    
    // Generic method to send HTML emails with template
    public void sendTemplateEmail(EmailRequest emailRequest) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            // Set basic email properties
            setupBasicEmailProperties(helper, emailRequest);
            
            // Process template if provided
            String emailContent = processTemplate(emailRequest.getTemplateName(), emailRequest.getTemplateVariables());
            helper.setText(emailContent, true);
            
            // Add inline attachments (like logos)
            addInlineAttachments(helper, emailRequest.getInlineAttachments());
            
            // Add file attachments
            addFileAttachments(helper, emailRequest.getAttachments());
            
            mailSender.send(message);
            log.info("Template email sent successfully to: {}", emailRequest.getTo());
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("Failed to send template email to: {}", emailRequest.getTo(), e);
            throw new RuntimeException("Failed to send email", e);
        }
    }
    
    // Async version for better performance
    @Async
    public CompletableFuture<Void> sendTemplateEmailAsync(EmailRequest emailRequest) {
        try {
            sendTemplateEmail(emailRequest);
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }
    
    // Send email to multiple recipients
    public void sendBulkEmail(EmailRequest emailRequest) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            setupBasicEmailProperties(helper, emailRequest);
            
            // Set multiple recipients
            if (emailRequest.getToList() != null && !emailRequest.getToList().isEmpty()) {
                helper.setTo(emailRequest.getToList().toArray(new String[0]));
            }
            if (emailRequest.getCcList() != null && !emailRequest.getCcList().isEmpty()) {
                helper.setCc(emailRequest.getCcList().toArray(new String[0]));
            }
            if (emailRequest.getBccList() != null && !emailRequest.getBccList().isEmpty()) {
                helper.setBcc(emailRequest.getBccList().toArray(new String[0]));
            }
            
            String emailContent = processTemplate(emailRequest.getTemplateName(), emailRequest.getTemplateVariables());
            helper.setText(emailContent, true);
            
            addInlineAttachments(helper, emailRequest.getInlineAttachments());
            addFileAttachments(helper, emailRequest.getAttachments());
            
            mailSender.send(message);
            log.info("Bulk email sent successfully to {} recipients", emailRequest.getToList().size());
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("Failed to send bulk email", e);
            throw new RuntimeException("Failed to send bulk email", e);
        }
    }
    
    // Helper methods
    private void setupBasicEmailProperties(MimeMessageHelper helper, EmailRequest emailRequest)
            throws MessagingException, UnsupportedEncodingException {
        
        InternetAddress from = new InternetAddress(fromEmail,
                emailRequest.getFromName() != null ? emailRequest.getFromName() : appName);
        helper.setFrom(from);
        
        if (emailRequest.getTo() != null) {
            helper.setTo(emailRequest.getTo());
        }
        
        helper.setSubject(emailRequest.getSubject());
        
        if (emailRequest.getReplyTo() != null) {
            helper.setReplyTo(emailRequest.getReplyTo());
        }
        
        // Set priority if specified
        if (emailRequest.getPriority() != null) {
            helper.setPriority(emailRequest.getPriority());
        }
    }
    
    private String processTemplate(String templateName, Map<String, Object> variables) {
        if (templateName == null) {
            return variables != null ? variables.get("content").toString() : "";
        }
        
        Context context = new Context();
        if (variables != null) {
            variables.forEach(context::setVariable);
        }
        
        return templateEngine.process(templateName, context);
    }
    
    private void addInlineAttachments(MimeMessageHelper helper, Map<String, String> inlineAttachments)
            throws MessagingException {
        if (inlineAttachments != null) {
            for (Map.Entry<String, String> entry : inlineAttachments.entrySet()) {
                helper.addInline(entry.getKey(), new ClassPathResource(entry.getValue()));
            }
        }
    }
    
    private void addFileAttachments(MimeMessageHelper helper, List<File> attachments)
            throws MessagingException {
        if (attachments != null) {
            for (File file : attachments) {
                helper.addAttachment(file.getName(), file);
            }
        }
    }
    
    // Business-specific email methods using the generic function
    public void sendWelcomeEmail(UserDTO userDTO, String password) {
        Map<String, Object> variables = Map.of(
                "name", userDTO.getName(),
                "username", userDTO.getEmail(),
                "password", password
        );
        
        Map<String, String> inlineAttachments = Map.of("logo", logoPath);
        
        EmailRequest emailRequest = EmailRequest.builder()
                .to(userDTO.getEmail())
                .subject("Welcome to " + appName + "!")
                .templateName("welcome-email")
                .templateVariables(variables)
                .inlineAttachments(inlineAttachments)
                .replyTo("support@zerobee.com")
                .fromName(appName + " Support")
                .build();
        
        sendTemplateEmailAsync(emailRequest);
    }
}
