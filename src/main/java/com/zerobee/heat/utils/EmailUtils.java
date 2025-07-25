package com.zerobee.heat.utils;

import com.zerobee.heat.config.EmailConfig;
import com.zerobee.heat.dto.email.AttachmentDTO;
import com.zerobee.heat.dto.email.EmailSearchRequest;
import com.zerobee.heat.exception.EmailConnectionException;
import com.zerobee.heat.exception.EmailNotFoundException;
import com.zerobee.heat.exception.EmailProcessingException;
import jakarta.mail.*;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.search.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailUtils {
    
    private final EmailConfig emailConfig;
    
    public Store getImapStore() throws MessagingException {
        Properties props = new Properties();
        props.put("mail.imap.ssl.enable", "true");
        props.put("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.imap.socketFactory.fallback", "false");
        props.put("mail.store.protocol", "imaps");
        props.put("mail.debug", "false");
        
        Session session = Session.getInstance(props);
        Store store = session.getStore("imaps");
        store.connect(emailConfig.getImapHost(),
                Integer.parseInt(emailConfig.getImapPort()),
                emailConfig.getUsername(),
                emailConfig.getPassword());
        
        return store;
    }
    
    
    public void updateEmailFlag(String messageId, Flags.Flag flag, boolean value) {
        try {
            Store store = getImapStore();
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_WRITE);
            
            SearchTerm searchTerm = new MessageIDTerm(messageId);
            Message[] messages = inbox.search(searchTerm);
            
            if (messages.length == 0) {
                inbox.close(false);
                store.close();
                throw new EmailNotFoundException("Email with ID " + messageId + " not found");
            }
            
            messages[0].setFlag(flag, value);
            log.info("Updated flag {} to {} for email: {}", flag, value, messageId);
            
            inbox.close(true);
            store.close();
            
        } catch (EmailNotFoundException e) {
            throw e;
        } catch (MessagingException e) {
            log.error("IMAP connection error while updating flag", e);
            throw new EmailConnectionException("Failed to connect to email server", e);
        } catch (Exception e) {
            log.error("Unexpected error updating email flag", e);
            throw new EmailProcessingException("Failed to update email flag", e);
        }
    }
    
    public void bulkUpdateFlag(List<String> messageIds, Flags.Flag flag, boolean value) {
        try {
            Store store = getImapStore();
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_WRITE);
            
            int updatedCount = 0;
            
            for (String messageId : messageIds) {
                try {
                    SearchTerm searchTerm = new MessageIDTerm(messageId);
                    Message[] messages = inbox.search(searchTerm);
                    
                    if (messages.length > 0) {
                        messages[0].setFlag(flag, value);
                        updatedCount++;
                    }
                } catch (Exception e) {
                    log.warn("Error updating flag for message {}: {}", messageId, e.getMessage());
                }
            }
            
            log.info("Bulk updated flag {} to {} for {}/{} emails", flag, value, updatedCount, messageIds.size());
            
            inbox.close(true);
            store.close();
            
        } catch (MessagingException e) {
            log.error("Error bulk updating flags", e);
            throw new EmailConnectionException("Failed to connect to email server", e);
        }
    }
    
    public SearchTerm buildSearchTerm(EmailSearchRequest request) {
        List<SearchTerm> terms = new ArrayList<>();
        
        if (request.getSubject() != null && !request.getSubject().trim().isEmpty()) {
            terms.add(new SubjectTerm(request.getSubject()));
        }
        
        if (request.getFrom() != null && !request.getFrom().trim().isEmpty()) {
            terms.add(new FromStringTerm(request.getFrom()));
        }
        
        if (request.getTo() != null && !request.getTo().trim().isEmpty()) {
            terms.add(new RecipientStringTerm(Message.RecipientType.TO, request.getTo()));
        }
        
        if (request.getContent() != null && !request.getContent().trim().isEmpty()) {
            terms.add(new BodyTerm(request.getContent()));
        }
        
        if (request.getIsUnread() != null && request.getIsUnread()) {
            terms.add(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
        }
        
        if (request.getIsStarred() != null && request.getIsStarred()) {
            terms.add(new FlagTerm(new Flags(Flags.Flag.FLAGGED), true));
        }
        
        if (request.getStartDate() != null) {
            try {
                LocalDate startDate = LocalDate.parse(request.getStartDate());
                Date startDateAsDate = Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
                terms.add(new ReceivedDateTerm(ComparisonTerm.GE, startDateAsDate));
            } catch (Exception e) {
                log.warn("Invalid start date format: {}", request.getStartDate());
            }
        }
        
        if (request.getEndDate() != null) {
            try {
                LocalDate endDate = LocalDate.parse(request.getEndDate());
                Date endDateAsDate = Date.from(endDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
                terms.add(new ReceivedDateTerm(ComparisonTerm.LT, endDateAsDate));
            } catch (Exception e) {
                log.warn("Invalid end date format: {}", request.getEndDate());
            }
        }
        
        if (terms.isEmpty()) {
            return null;
        }
        
        if (terms.size() == 1) {
            return terms.get(0);
        }
        
        // Combine all terms with AND
        SearchTerm combinedTerm = terms.get(0);
        for (int i = 1; i < terms.size(); i++) {
            combinedTerm = new AndTerm(combinedTerm, terms.get(i));
        }
        
        return combinedTerm;
    }
    
    public boolean isSystemFolder(String folderName) {
        return Arrays.asList("INBOX", "Sent", "Drafts", "Trash", "Spam", "Junk").contains(folderName);
    }
    
    // HELPER METHODS
    
    public void extractAttachments(MimeMessage message, List<AttachmentDTO> attachments, String messageId)
            throws MessagingException, IOException {
        
        Object content = message.getContent();
        
        if (content instanceof MimeMultipart) {
            MimeMultipart multipart = (MimeMultipart) content;
            
            for (int i = 0; i < multipart.getCount(); i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                
                if (Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition()) ||
                        (bodyPart.getFileName() != null && !bodyPart.getFileName().isEmpty())) {
                    
                    AttachmentDTO attachment = new AttachmentDTO();
                    attachment.setFileName(bodyPart.getFileName());
                    attachment.setContentType(bodyPart.getContentType());
                    attachment.setSize(bodyPart.getSize());
                    attachment.setMessageId(messageId);
                    
                    attachments.add(attachment);
                }
            }
        }
    }
    
    public byte[] extractAttachmentData(MimeMessage message, String attachmentName)
            throws MessagingException, IOException {
        
        Object content = message.getContent();
        
        if (content instanceof MimeMultipart) {
            MimeMultipart multipart = (MimeMultipart) content;
            
            for (int i = 0; i < multipart.getCount(); i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                
                if ((Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition()) ||
                        (bodyPart.getFileName() != null && !bodyPart.getFileName().isEmpty())) &&
                        attachmentName.equals(bodyPart.getFileName())) {
                    
                    try (InputStream inputStream = bodyPart.getInputStream()) {
                        return inputStream.readAllBytes();
                    }
                }
            }
        }
        
        return null;
    }
    
    public int getFolderMessageCount(Store store, String folderName) throws MessagingException {
        try {
            Folder folder = store.getFolder(folderName);
            if (!folder.exists()) {
                return 0;
            }
            
            folder.open(Folder.READ_ONLY);
            int count = folder.getMessageCount();
            folder.close(false);
            
            return count;
        } catch (MessagingException e) {
            log.warn("Could not get count for folder: {}", folderName);
            return 0;
        }
    }
    
    public int getUnreadEmailCount(Store store, String folderName) throws MessagingException {
        try {
            Folder folder = store.getFolder(folderName);
            if (!folder.exists()) {
                return 0;
            }
            
            folder.open(Folder.READ_ONLY);
            
            FlagTerm unseenFlag = new FlagTerm(new Flags(Flags.Flag.SEEN), false);
            Message[] unreadMessages = folder.search(unseenFlag);
            int count = unreadMessages.length;
            
            folder.close(false);
            
            return count;
        } catch (MessagingException e) {
            log.warn("Could not get unread count for folder: {}", folderName);
            return 0;
        }
    }
    
    public int getStarredEmailCount(Store store, String folderName) throws MessagingException {
        try {
            Folder folder = store.getFolder(folderName);
            if (!folder.exists()) {
                return 0;
            }
            
            folder.open(Folder.READ_ONLY);
            
            FlagTerm starredFlag = new FlagTerm(new Flags(Flags.Flag.FLAGGED), true);
            Message[] starredMessages = folder.search(starredFlag);
            int count = starredMessages.length;
            
            folder.close(false);
            
            return count;
        } catch (MessagingException e) {
            log.warn("Could not get starred count for folder: {}", folderName);
            return 0;
        }
    }
    
    public String extractEmailContent(MimeMessage message) throws MessagingException, IOException {
        Object content = message.getContent();
        
        if (content instanceof String) {
            return (String) content;
        } else if (content instanceof MimeMultipart) {
            MimeMultipart multipart = (MimeMultipart) content;
            
            for (int i = 0; i < multipart.getCount(); i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                
                if (bodyPart.isMimeType("text/plain") || bodyPart.isMimeType("text/html")) {
                    return bodyPart.getContent().toString();
                }
            }
        }
        
        return "Content could not be extracted";
    }
}
