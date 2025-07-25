package com.zerobee.heat.service;

import com.zerobee.heat.dto.email.ReceivedEmailDTO;
import com.zerobee.heat.mapper.EmailMapper;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailReceiverService {
    
    private final EmailMapper emailMapper;
    
    public void processReceivedEmail(MimeMessage message) {
        try {
            ReceivedEmailDTO emailDTO = emailMapper.toDTO(message);
            handleIncomingEmail(emailDTO);
            
        } catch (Exception e) {
            log.error("Error processing received email", e);
        }
    }
    
    public ReceivedEmailDTO convertToDTO(MimeMessage message) {
        return emailMapper.toDTO(message);
    }
    
    private void handleIncomingEmail(ReceivedEmailDTO email) {
        
        if (email.getSubject() != null &&
                email.getSubject().toLowerCase().contains("support")) {
            handleSupportEmail(email);
        }
        
        if (email.getFromEmail() != null &&
                email.getFromEmail().contains("noreply")) {
            handleSystemEmail(email);
        }
    }
    
    private void handleSupportEmail(ReceivedEmailDTO email) {
        log.info("Processing support email from: {}", email.getFromEmail());
    }
    
    private void handleSystemEmail(ReceivedEmailDTO email) {
        log.info("Processing system email: {}", email.getSubject());
    }
}
