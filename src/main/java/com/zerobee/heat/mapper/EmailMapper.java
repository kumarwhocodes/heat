package com.zerobee.heat.mapper;

import com.zerobee.heat.dto.email.ReceivedEmailDTO;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class EmailMapper {
    
    public ReceivedEmailDTO toDTO(MimeMessage message) {
        try {
            ReceivedEmailDTO dto = new ReceivedEmailDTO();
            
            // Basic information
            dto.setSubject(message.getSubject());
            dto.setMessageId(message.getMessageID());
            
            // From address
            mapFromAddress(message, dto);
            
            // To addresses
            mapToAddresses(message, dto);
            
            // Dates
            mapDates(message, dto);
            
            // Content
            mapContent(message, dto);
            
            return dto;
            
        } catch (Exception e) {
            log.error("Error mapping email to DTO", e);
            return createErrorDTO(message);
        }
    }
    
    private void mapFromAddress(MimeMessage message, ReceivedEmailDTO dto) {
        try {
            Address[] fromAddresses = message.getFrom();
            if (fromAddresses != null && fromAddresses.length > 0) {
                Address fromAddr = fromAddresses[0];
                
                if (fromAddr instanceof InternetAddress) {
                    InternetAddress internetAddr = (InternetAddress) fromAddr;
                    dto.setFromEmail(internetAddr.getAddress());
                    dto.setFromName(internetAddr.getPersonal() != null ?
                            internetAddr.getPersonal() : internetAddr.getAddress());
                } else {
                    // Handle other address types
                    String addressStr = fromAddr.toString();
                    dto.setFromEmail(extractEmailFromString(addressStr));
                    dto.setFromName(addressStr);
                }
            }
        } catch (Exception e) {
            log.warn("Error mapping from address", e);
            dto.setFromEmail("unknown@domain.com");
            dto.setFromName("Unknown Sender");
        }
    }
    
    private void mapToAddresses(MimeMessage message, ReceivedEmailDTO dto) {
        try {
            Address[] toAddresses = message.getRecipients(Message.RecipientType.TO);
            if (toAddresses != null && toAddresses.length > 0) {
                List<String> toEmails = Arrays.stream(toAddresses)
                        .map(addr -> {
                            if (addr instanceof InternetAddress) {
                                return ((InternetAddress) addr).getAddress();
                            } else {
                                return extractEmailFromString(addr.toString());
                            }
                        })
                        .collect(Collectors.toList());
                dto.setToEmails(toEmails);
            } else {
                dto.setToEmails(new ArrayList<>());
            }
        } catch (Exception e) {
            log.warn("Error mapping to addresses", e);
            dto.setToEmails(new ArrayList<>());
        }
    }
    
    private void mapDates(MimeMessage message, ReceivedEmailDTO dto) {
        try {
            if (message.getSentDate() != null) {
                dto.setSentDate(LocalDateTime.ofInstant(
                        message.getSentDate().toInstant(), ZoneId.systemDefault()));
            }
            if (message.getReceivedDate() != null) {
                dto.setReceivedDate(LocalDateTime.ofInstant(
                        message.getReceivedDate().toInstant(), ZoneId.systemDefault()));
            }
        } catch (Exception e) {
            log.warn("Error mapping dates", e);
        }
    }
    
    private void mapContent(MimeMessage message, ReceivedEmailDTO dto) {
        try {
            Object content = message.getContent();
            List<String> attachmentNames = new ArrayList<>();
            
            if (content instanceof String) {
                mapStringContent(message, (String) content, dto);
            } else if (content instanceof MimeMultipart) {
                mapMultipartContent((MimeMultipart) content, dto, attachmentNames);
            } else {
                dto.setTextContent("Unsupported content type: " +
                        (content != null ? content.getClass().getSimpleName() : "null"));
            }
            
            dto.setHasAttachments(!attachmentNames.isEmpty());
            dto.setAttachmentNames(attachmentNames);
            
        } catch (Exception e) {
            log.error("Error mapping content", e);
            dto.setTextContent("Error reading email content");
            dto.setHtmlContent(null);
            dto.setHasAttachments(false);
            dto.setAttachmentNames(new ArrayList<>());
        }
    }
    
    private void mapStringContent(MimeMessage message, String content, ReceivedEmailDTO dto)
            throws MessagingException {
        if (message.isMimeType("text/plain")) {
            dto.setTextContent(content);
        } else if (message.isMimeType("text/html")) {
            dto.setHtmlContent(content);
        } else {
            dto.setTextContent(content);
        }
    }
    
    private void mapMultipartContent(MimeMultipart multipart, ReceivedEmailDTO dto,
                                     List<String> attachmentNames) throws MessagingException, IOException {
        for (int i = 0; i < multipart.getCount(); i++) {
            try {
                BodyPart bodyPart = multipart.getBodyPart(i);
                
                if (isAttachment(bodyPart)) {
                    String fileName = bodyPart.getFileName();
                    if (fileName != null && !fileName.trim().isEmpty()) {
                        attachmentNames.add(fileName);
                    }
                } else if (bodyPart.isMimeType("text/plain") && dto.getTextContent() == null) {
                    Object partContent = bodyPart.getContent();
                    if (partContent != null) {
                        dto.setTextContent(partContent.toString());
                    }
                } else if (bodyPart.isMimeType("text/html") && dto.getHtmlContent() == null) {
                    Object partContent = bodyPart.getContent();
                    if (partContent != null) {
                        dto.setHtmlContent(partContent.toString());
                    }
                } else if (bodyPart.getContent() instanceof MimeMultipart) {
                    // Handle nested multipart
                    mapMultipartContent((MimeMultipart) bodyPart.getContent(), dto, attachmentNames);
                }
            } catch (Exception e) {
                log.warn("Error processing body part {}: {}", i, e.getMessage());
            }
        }
    }
    
    private boolean isAttachment(BodyPart bodyPart) throws MessagingException {
        String disposition = bodyPart.getDisposition();
        String fileName = bodyPart.getFileName();
        
        return Part.ATTACHMENT.equalsIgnoreCase(disposition) ||
                Part.INLINE.equalsIgnoreCase(disposition) ||
                (fileName != null && !fileName.trim().isEmpty());
    }
    
    private String extractEmailFromString(String addressStr) {
        // Simple regex to extract email from string like "Name <email@domain.com>"
        if (addressStr.contains("<") && addressStr.contains(">")) {
            int start = addressStr.indexOf('<') + 1;
            int end = addressStr.indexOf('>');
            if (start < end) {
                return addressStr.substring(start, end);
            }
        }
        // If no angle brackets, assume the whole string is the email
        return addressStr.trim();
    }
    
    private ReceivedEmailDTO createErrorDTO(MimeMessage message) {
        ReceivedEmailDTO dto = new ReceivedEmailDTO();
        try {
            dto.setMessageId(message.getMessageID());
            dto.setSubject("Error reading email");
        } catch (Exception e) {
            dto.setMessageId("unknown");
            dto.setSubject("Error reading email");
        }
        dto.setFromEmail("error@reading.com");
        dto.setFromName("Error Reading");
        dto.setToEmails(new ArrayList<>());
        dto.setTextContent("Error occurred while reading this email");
        dto.setHasAttachments(false);
        dto.setAttachmentNames(new ArrayList<>());
        return dto;
    }
}
