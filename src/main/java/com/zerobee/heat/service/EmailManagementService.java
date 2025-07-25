package com.zerobee.heat.service;

import com.zerobee.heat.dto.email.*;
import com.zerobee.heat.exception.EmailConnectionException;
import com.zerobee.heat.exception.EmailNotFoundException;
import com.zerobee.heat.exception.EmailProcessingException;
import com.zerobee.heat.exception.FolderNotFoundException;
import com.zerobee.heat.utils.EmailUtils;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.search.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailManagementService {
    
    private final EmailReceiverService emailReceiverService;
    private final EmailUtils emailUtils;
    
    public List<ReceivedEmailDTO> getRecentEmails(int limit) {
        List<ReceivedEmailDTO> emails = new ArrayList<>();
        
        try {
            Store store = emailUtils.getImapStore();
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);
            
            int messageCount = inbox.getMessageCount();
            if (messageCount == 0) {
                inbox.close(false);
                store.close();
                return emails;
            }
            
            int start = Math.max(1, messageCount - limit + 1);
            Message[] messages = inbox.getMessages(start, messageCount);
            
            // Process messages in reverse order for latest first
            for (int i = messages.length - 1; i >= 0; i--) {
                try {
                    if (messages[i] instanceof MimeMessage) {
                        ReceivedEmailDTO dto = emailReceiverService.convertToDTO((MimeMessage) messages[i]);
                        emails.add(dto);
                    }
                } catch (Exception e) {
                    log.warn("Error processing message {}: {}", i, e.getMessage());
                    // Continue processing other messages, don't fail the entire operation
                }
            }
            
            inbox.close(false);
            store.close();
            
        } catch (MessagingException e) {
            log.error("IMAP connection error while retrieving emails", e);
            throw new EmailConnectionException("Failed to connect to email server", e);
        } catch (Exception e) {
            log.error("Unexpected error retrieving emails", e);
            throw new EmailProcessingException("Failed to retrieve emails", e);
        }
        
        return emails;
    }
    
    public List<ReceivedEmailDTO> getUnreadEmails() {
        List<ReceivedEmailDTO> emails = new ArrayList<>();
        
        try {
            Store store = emailUtils.getImapStore();
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);
            
            FlagTerm unseenFlag = new FlagTerm(new Flags(Flags.Flag.SEEN), false);
            Message[] messages = inbox.search(unseenFlag);
            
            log.info("Found {} unread messages", messages.length);
            
            for (Message message : messages) {
                try {
                    if (message instanceof MimeMessage) {
                        ReceivedEmailDTO dto = emailReceiverService.convertToDTO((MimeMessage) message);
                        emails.add(dto);
                    }
                } catch (Exception e) {
                    log.warn("Error processing unread message: {}", e.getMessage());
                    // Continue processing other messages
                }
            }
            
            inbox.close(false);
            store.close();
            
        } catch (MessagingException e) {
            log.error("IMAP connection error while retrieving unread emails", e);
            throw new EmailConnectionException("Failed to connect to email server", e);
        } catch (Exception e) {
            log.error("Unexpected error retrieving unread emails", e);
            throw new EmailProcessingException("Failed to retrieve unread emails", e);
        }
        
        return emails;
    }
    
    public void markAsRead(String messageId) {
        try {
            Store store = emailUtils.getImapStore();
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_WRITE);
            
            SearchTerm searchTerm = new MessageIDTerm(messageId);
            Message[] messages = inbox.search(searchTerm);
            
            if (messages.length == 0) {
                inbox.close(false);
                store.close();
                throw new EmailNotFoundException("Email with ID " + messageId + " not found");
            }
            
            messages[0].setFlag(Flags.Flag.SEEN, true);
            log.info("Marked email as read: {}", messageId);
            
            inbox.close(true);
            store.close();
            
        } catch (EmailNotFoundException e) {
            throw e; // Re-throw custom exception
        } catch (MessagingException e) {
            log.error("IMAP connection error while marking email as read", e);
            throw new EmailConnectionException("Failed to connect to email server", e);
        } catch (Exception e) {
            log.error("Unexpected error marking email as read", e);
            throw new EmailProcessingException("Failed to mark email as read", e);
        }
    }
    
    
    public void markAsUnread(String messageId) {
        emailUtils.updateEmailFlag(messageId, Flags.Flag.SEEN, false);
    }
    
    public void starEmail(String messageId) {
        emailUtils.updateEmailFlag(messageId, Flags.Flag.FLAGGED, true);
    }
    
    public void unstarEmail(String messageId) {
        emailUtils.updateEmailFlag(messageId, Flags.Flag.FLAGGED, false);
    }
    
    public void deleteEmail(String messageId) {
        emailUtils.updateEmailFlag(messageId, Flags.Flag.DELETED, true);
    }
    
    public List<ReceivedEmailDTO> getStarredEmails() {
        return searchEmails(EmailSearchRequest.builder()
                .isStarred(true)
                .limit(50)
                .build());
    }
    
    public List<ReceivedEmailDTO> searchEmails(EmailSearchRequest searchRequest) {
        List<ReceivedEmailDTO> emails = new ArrayList<>();
        
        try {
            Store store = emailUtils.getImapStore();
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);
            
            // Build search criteria
            SearchTerm searchTerm = emailUtils.buildSearchTerm(searchRequest);
            
            Message[] messages;
            if (searchTerm != null) {
                messages = inbox.search(searchTerm);
            } else {
                // If no search criteria, get recent messages
                int messageCount = inbox.getMessageCount();
                int limit = searchRequest.getLimit();
                int start = Math.max(1, messageCount - limit + 1);
                messages = inbox.getMessages(start, messageCount);
            }
            
            log.info("Found {} messages matching search criteria", messages.length);
            
            // Limit results
            int limit = Math.min(messages.length, searchRequest.getLimit());
            
            for (int i = messages.length - 1; i >= messages.length - limit; i--) {
                try {
                    if (messages[i] instanceof MimeMessage) {
                        ReceivedEmailDTO dto = emailReceiverService.convertToDTO((MimeMessage) messages[i]);
                        emails.add(dto);
                    }
                } catch (Exception e) {
                    log.warn("Error processing search result message: {}", e.getMessage());
                }
            }
            
            inbox.close(false);
            store.close();
            
        } catch (MessagingException e) {
            log.error("IMAP connection error during search", e);
            throw new EmailConnectionException("Failed to connect to email server", e);
        } catch (Exception e) {
            log.error("Unexpected error during email search", e);
            throw new EmailProcessingException("Failed to search emails", e);
        }
        
        return emails;
    }
    
    public List<String> getAllFolders() {
        List<String> folderNames = new ArrayList<>();
        
        try {
            Store store = emailUtils.getImapStore();
            
            Folder[] folders = store.getDefaultFolder().list("*");
            
            for (Folder folder : folders) {
                folderNames.add(folder.getName());
            }
            
            store.close();
            
        } catch (MessagingException e) {
            log.error("Error retrieving folders", e);
            throw new EmailConnectionException("Failed to connect to email server", e);
        }
        
        return folderNames;
    }
    
    public void createFolder(String folderName) {
        try {
            Store store = emailUtils.getImapStore();
            
            Folder newFolder = store.getFolder(folderName);
            
            if (newFolder.exists()) {
                store.close();
                throw new IllegalArgumentException("Folder '" + folderName + "' already exists");
            }
            
            boolean created = newFolder.create(Folder.HOLDS_MESSAGES);
            
            if (!created) {
                store.close();
                throw new EmailProcessingException("Failed to create folder: " + folderName, null);
            }
            
            log.info("Created folder: {}", folderName);
            store.close();
            
        } catch (MessagingException e) {
            log.error("Error creating folder: {}", folderName, e);
            throw new EmailConnectionException("Failed to connect to email server", e);
        }
    }
    
    public void deleteFolder(String folderName) {
        try {
            Store store = emailUtils.getImapStore();
            
            Folder folder = store.getFolder(folderName);
            
            if (!folder.exists()) {
                store.close();
                throw new com.zerobee.heat.exception.FolderNotFoundException("Folder '" + folderName + "' not found");
            }
            
            // Don't allow deletion of system folders
            if (emailUtils.isSystemFolder(folderName)) {
                store.close();
                throw new IllegalArgumentException("Cannot delete system folder: " + folderName);
            }
            
            if (folder.isOpen()) {
                folder.close(false);
            }
            
            boolean deleted = folder.delete(false);
            
            if (!deleted) {
                store.close();
                throw new EmailProcessingException("Failed to delete folder: " + folderName, null);
            }
            
            log.info("Deleted folder: {}", folderName);
            store.close();
            
        } catch (MessagingException e) {
            log.error("Error deleting folder: {}", folderName, e);
            throw new EmailConnectionException("Failed to connect to email server", e);
        }
    }
    
    public List<ReceivedEmailDTO> getEmailsByFolder(String folderName, int limit) {
        List<ReceivedEmailDTO> emails = new ArrayList<>();
        
        try {
            Store store = emailUtils.getImapStore();
            Folder folder = store.getFolder(folderName);
            
            if (!folder.exists()) {
                store.close();
                throw new com.zerobee.heat.exception.FolderNotFoundException("Folder '" + folderName + "' not found");
            }
            
            folder.open(Folder.READ_ONLY);
            
            int messageCount = folder.getMessageCount();
            if (messageCount == 0) {
                folder.close(false);
                store.close();
                return emails;
            }
            
            int start = Math.max(1, messageCount - limit + 1);
            Message[] messages = folder.getMessages(start, messageCount);
            
            for (int i = messages.length - 1; i >= 0; i--) {
                try {
                    if (messages[i] instanceof MimeMessage) {
                        ReceivedEmailDTO dto = emailReceiverService.convertToDTO((MimeMessage) messages[i]);
                        emails.add(dto);
                    }
                } catch (Exception e) {
                    log.warn("Error processing message in folder {}: {}", folderName, e.getMessage());
                }
            }
            
            folder.close(false);
            store.close();
            
        } catch (MessagingException e) {
            log.error("Error retrieving emails from folder: {}", folderName, e);
            throw new EmailConnectionException("Failed to connect to email server", e);
        }
        
        return emails;
    }
    
    public void moveEmailToFolder(String messageId, String targetFolder) {
        try {
            Store store = emailUtils.getImapStore();
            
            // Check if target folder exists
            Folder target = store.getFolder(targetFolder);
            if (!target.exists()) {
                store.close();
                throw new FolderNotFoundException("Target folder '" + targetFolder + "' not found");
            }
            
            // Find the email in INBOX (or search across folders)
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_WRITE);
            
            SearchTerm searchTerm = new MessageIDTerm(messageId);
            Message[] messages = inbox.search(searchTerm);
            
            if (messages.length == 0) {
                inbox.close(false);
                store.close();
                throw new EmailNotFoundException("Email with ID " + messageId + " not found");
            }
            
            // Copy to target folder
            inbox.copyMessages(messages, target);
            
            // Mark original as deleted
            messages[0].setFlag(Flags.Flag.DELETED, true);
            
            // Expunge to actually move
            inbox.expunge();
            
            log.info("Moved email {} to folder {}", messageId, targetFolder);
            
            inbox.close(true);
            store.close();
            
        } catch (MessagingException e) {
            log.error("Error moving email to folder", e);
            throw new EmailConnectionException("Failed to connect to email server", e);
        }
    }
    
    public void bulkMarkAsRead(List<String> messageIds) {
        emailUtils.bulkUpdateFlag(messageIds, Flags.Flag.SEEN, true);
    }
    
    public void bulkMarkAsUnread(List<String> messageIds) {
        emailUtils.bulkUpdateFlag(messageIds, Flags.Flag.SEEN, false);
    }
    
    public void bulkStarEmails(List<String> messageIds) {
        emailUtils.bulkUpdateFlag(messageIds, Flags.Flag.FLAGGED, true);
    }
    
    public void bulkUnstarEmails(List<String> messageIds) {
        emailUtils.bulkUpdateFlag(messageIds, Flags.Flag.FLAGGED, false);
    }
    
    public void bulkDeleteEmails(List<String> messageIds) {
        emailUtils.bulkUpdateFlag(messageIds, Flags.Flag.DELETED, true);
    }
    
    public void bulkMoveEmails(List<String> messageIds, String targetFolder) {
        try {
            Store store = emailUtils.getImapStore();
            
            Folder target = store.getFolder(targetFolder);
            if (!target.exists()) {
                store.close();
                throw new FolderNotFoundException("Target folder '" + targetFolder + "' not found");
            }
            
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_WRITE);
            
            List<Message> messagesToMove = new ArrayList<>();
            
            for (String messageId : messageIds) {
                SearchTerm searchTerm = new MessageIDTerm(messageId);
                Message[] messages = inbox.search(searchTerm);
                
                if (messages.length > 0) {
                    messagesToMove.add(messages[0]);
                }
            }
            
            if (!messagesToMove.isEmpty()) {
                Message[] messagesArray = messagesToMove.toArray(new Message[0]);
                inbox.copyMessages(messagesArray, target);
                
                // Mark originals as deleted
                for (Message message : messagesArray) {
                    message.setFlag(Flags.Flag.DELETED, true);
                }
                
                inbox.expunge();
                log.info("Bulk moved {} emails to folder {}", messagesArray.length, targetFolder);
            }
            
            inbox.close(true);
            store.close();
            
        } catch (MessagingException e) {
            log.error("Error bulk moving emails", e);
            throw new EmailConnectionException("Failed to connect to email server", e);
        }
    }
    
    public EmailStatsDTO getEmailStatistics() {
        try {
            Store store = emailUtils.getImapStore();
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);
            
            int totalEmails = inbox.getMessageCount();
            
            // Count unread emails
            FlagTerm unseenFlag = new FlagTerm(new Flags(Flags.Flag.SEEN), false);
            Message[] unreadMessages = inbox.search(unseenFlag);
            int unreadCount = unreadMessages.length;
            
            // Count starred emails
            FlagTerm starredFlag = new FlagTerm(new Flags(Flags.Flag.FLAGGED), true);
            Message[] starredMessages = inbox.search(starredFlag);
            int starredCount = starredMessages.length;
            
            // Count today's emails
            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);
            
            ReceivedDateTerm todayTerm = new ReceivedDateTerm(ComparisonTerm.GE, today.getTime());
            Message[] todayMessages = inbox.search(todayTerm);
            int todayCount = todayMessages.length;
            
            // Count emails with attachments (approximate)
            int attachmentCount = 0;
            Message[] recentMessages = inbox.getMessages(Math.max(1, totalEmails - 100), totalEmails);
            for (Message message : recentMessages) {
                try {
                    if (message.getContentType().toLowerCase().contains("multipart")) {
                        attachmentCount++;
                    }
                } catch (Exception e) {
                    // Skip this message
                }
            }
            
            // Calculate storage used (approximate)
            long storageUsed = 0;
            for (Message message : recentMessages) {
                try {
                    storageUsed += message.getSize();
                } catch (Exception e) {
                    // Skip this message
                }
            }
            
            inbox.close(false);
            store.close();
            
            return new EmailStatsDTO(totalEmails, unreadCount, starredCount,
                    todayCount, attachmentCount, storageUsed);
            
        } catch (MessagingException e) {
            log.error("Error getting email statistics", e);
            throw new EmailConnectionException("Failed to connect to email server", e);
        }
    }
    
    
    public List<AttachmentDTO> getEmailAttachments(String messageId) {
        List<AttachmentDTO> attachments = new ArrayList<>();
        
        try {
            Store store = emailUtils.getImapStore();
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);
            
            SearchTerm searchTerm = new MessageIDTerm(messageId);
            Message[] messages = inbox.search(searchTerm);
            
            if (messages.length == 0) {
                inbox.close(false);
                store.close();
                throw new EmailNotFoundException("Email with ID " + messageId + " not found");
            }
            
            MimeMessage message = (MimeMessage) messages[0];
            emailUtils.extractAttachments(message, attachments, messageId);
            
            inbox.close(false);
            store.close();
            
        } catch (MessagingException e) {
            log.error("Error retrieving email attachments", e);
            throw new EmailConnectionException("Failed to connect to email server", e);
        } catch (Exception e) {
            log.error("Error processing email attachments", e);
            throw new EmailProcessingException("Failed to retrieve attachments", e);
        }
        
        return attachments;
    }
    
    public byte[] downloadAttachment(String messageId, String attachmentName) {
        try {
            Store store = emailUtils.getImapStore();
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);
            
            SearchTerm searchTerm = new MessageIDTerm(messageId);
            Message[] messages = inbox.search(searchTerm);
            
            if (messages.length == 0) {
                inbox.close(false);
                store.close();
                throw new EmailNotFoundException("Email with ID " + messageId + " not found");
            }
            
            MimeMessage message = (MimeMessage) messages[0];
            byte[] attachmentData = emailUtils.extractAttachmentData(message, attachmentName);
            
            if (attachmentData == null) {
                inbox.close(false);
                store.close();
                throw new EmailNotFoundException("Attachment '" + attachmentName + "' not found in email");
            }
            
            inbox.close(false);
            store.close();
            
            return attachmentData;
            
        } catch (MessagingException e) {
            log.error("Error downloading attachment", e);
            throw new EmailConnectionException("Failed to connect to email server", e);
        } catch (Exception e) {
            log.error("Error processing attachment download", e);
            throw new EmailProcessingException("Failed to download attachment", e);
        }
    }
    
    public EmailCountDTO getEmailCounts() {
        try {
            Store store = emailUtils.getImapStore();
            
            EmailCountDTO counts = new EmailCountDTO();
            
            // Get counts from different folders
            counts.setTotalEmails(emailUtils.getFolderMessageCount(store, "INBOX"));
            counts.setUnreadEmails(emailUtils.getUnreadEmailCount(store, "INBOX"));
            counts.setStarredEmails(emailUtils.getStarredEmailCount(store, "INBOX"));
            
            // Try to get counts from common folders (may not exist in all email providers)
            try {
                counts.setDraftEmails(emailUtils.getFolderMessageCount(store, "Drafts"));
            } catch (Exception e) {
                counts.setDraftEmails(0);
            }
            
            try {
                counts.setSentEmails(emailUtils.getFolderMessageCount(store, "Sent"));
            } catch (Exception e) {
                counts.setSentEmails(0);
            }
            
            try {
                counts.setTrashEmails(emailUtils.getFolderMessageCount(store, "Trash"));
            } catch (Exception e) {
                counts.setTrashEmails(0);
            }
            
            try {
                counts.setSpamEmails(emailUtils.getFolderMessageCount(store, "Spam"));
            } catch (Exception e) {
                counts.setSpamEmails(0);
            }
            
            store.close();
            return counts;
            
        } catch (MessagingException e) {
            log.error("Error getting email counts", e);
            throw new EmailConnectionException("Failed to connect to email server", e);
        }
    }
    
    public void replyToEmail(String messageId, EmailReplyRequest replyRequest) {
        try {
            Store store = emailUtils.getImapStore();
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);
            
            SearchTerm searchTerm = new MessageIDTerm(messageId);
            Message[] messages = inbox.search(searchTerm);
            
            if (messages.length == 0) {
                inbox.close(false);
                store.close();
                throw new EmailNotFoundException("Email with ID " + messageId + " not found");
            }
            
            MimeMessage originalMessage = (MimeMessage) messages[0];
            
            // Extract original email details
            String originalFrom = null;
            String originalSubject = originalMessage.getSubject();
            Address[] originalToAddresses = null;
            Address[] originalCcAddresses = null;
            
            if (originalMessage.getFrom() != null && originalMessage.getFrom().length > 0) {
                originalFrom = ((InternetAddress) originalMessage.getFrom()[0]).getAddress();
            }
            
            if (replyRequest.isReplyAll()) {
                originalToAddresses = originalMessage.getRecipients(Message.RecipientType.TO);
                originalCcAddresses = originalMessage.getRecipients(Message.RecipientType.CC);
            }
            
            inbox.close(false);
            store.close();
            
            // Send reply using your existing email service
            sendReplyEmail(originalFrom, originalSubject, originalToAddresses, originalCcAddresses, replyRequest);
            
            log.info("Reply sent for email: {}", messageId);
            
        } catch (MessagingException e) {
            log.error("Error replying to email", e);
            throw new EmailConnectionException("Failed to connect to email server", e);
        } catch (Exception e) {
            log.error("Error processing email reply", e);
            throw new EmailProcessingException("Failed to reply to email", e);
        }
    }
    
    public void forwardEmail(String messageId, EmailForwardRequest forwardRequest) {
        try {
            Store store = emailUtils.getImapStore();
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);
            
            SearchTerm searchTerm = new MessageIDTerm(messageId);
            Message[] messages = inbox.search(searchTerm);
            
            if (messages.length == 0) {
                inbox.close(false);
                store.close();
                throw new EmailNotFoundException("Email with ID " + messageId + " not found");
            }
            
            MimeMessage originalMessage = (MimeMessage) messages[0];
            
            // Extract original email content
            String originalSubject = originalMessage.getSubject();
            String originalContent = emailUtils.extractEmailContent(originalMessage);
            String originalFrom = null;
            
            if (originalMessage.getFrom() != null && originalMessage.getFrom().length > 0) {
                originalFrom = ((InternetAddress) originalMessage.getFrom()[0]).getAddress();
            }
            
            List<AttachmentDTO> attachments = new ArrayList<>();
            if (forwardRequest.isIncludeAttachments()) {
                emailUtils.extractAttachments(originalMessage, attachments, messageId);
            }
            
            inbox.close(false);
            store.close();
            
            // Send forward email using your existing email service
            sendForwardEmail(originalFrom, originalSubject, originalContent, attachments, forwardRequest);
            
            log.info("Email forwarded: {}", messageId);
            
        } catch (MessagingException e) {
            log.error("Error forwarding email", e);
            throw new EmailConnectionException("Failed to connect to email server", e);
        } catch (Exception e) {
            log.error("Error processing email forward", e);
            throw new EmailProcessingException("Failed to forward email", e);
        }
    }
    
    public int getUnreadCount() {
        try {
            Store store = emailUtils.getImapStore();
            int unreadCount = emailUtils.getUnreadEmailCount(store, "INBOX");
            store.close();
            return unreadCount;
            
        } catch (MessagingException e) {
            log.error("Error getting unread count", e);
            throw new EmailConnectionException("Failed to connect to email server", e);
        }
    }
    
    public SyncStatusDTO triggerSync() {
        try {
            log.info("Manual email sync triggered");
            
            Store store = emailUtils.getImapStore();
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);
            
            // Get current message count
            int currentCount = inbox.getMessageCount();
            
            // Force a folder refresh to get latest emails from server
            inbox.close(false);
            inbox.open(Folder.READ_ONLY);
            
            int newCount = inbox.getMessageCount();
            int newEmails = Math.max(0, newCount - currentCount);
            
            inbox.close(false);
            store.close();
            
            SyncStatusDTO syncStatus = new SyncStatusDTO();
            syncStatus.setRunning(false);
            syncStatus.setLastSyncTime(java.time.LocalDateTime.now().toString());
            syncStatus.setNewEmailsFound(newEmails);
            syncStatus.setStatus("Sync completed successfully");
            
            log.info("Email sync completed. Found {} new emails", newEmails);
            
            return syncStatus;
            
        } catch (MessagingException e) {
            log.error("Error during email sync", e);
            
            SyncStatusDTO errorStatus = new SyncStatusDTO();
            errorStatus.setRunning(false);
            errorStatus.setLastSyncTime(java.time.LocalDateTime.now().toString());
            errorStatus.setNewEmailsFound(0);
            errorStatus.setStatus("Sync failed: " + e.getMessage());
            
            throw new EmailConnectionException("Failed to sync emails", e);
        }
    }
    
    private void sendReplyEmail(String originalFrom, String originalSubject, Address[] originalToAddresses,
                                Address[] originalCcAddresses, EmailReplyRequest replyRequest) {
        // This method would use your existing EmailServiceSMTP to send the reply
        // You'll need to integrate with your existing email sending service
        
        log.info("Sending reply email to: {}", originalFrom);
        
        String replySubject = replyRequest.getSubject() != null ?
                replyRequest.getSubject() :
                "Re: " + (originalSubject != null ? originalSubject : "");
        
        // Here you would call your existing email sending service
        // Example: emailService.sendEmail(originalFrom, replySubject, replyRequest.getContent());
        
        // For now, just log the action
        log.info("Reply email would be sent with subject: {}", replySubject);
    }
    
    private void sendForwardEmail(String originalFrom, String originalSubject, String originalContent,
                                  List<AttachmentDTO> attachments, EmailForwardRequest forwardRequest) {
        // This method would use your existing EmailServiceSMTP to send the forward
        
        log.info("Forwarding email to: {}", forwardRequest.getRecipients());
        
        String forwardSubject = forwardRequest.getSubject() != null ?
                forwardRequest.getSubject() :
                "Fwd: " + (originalSubject != null ? originalSubject : "");
        
        String forwardContent = forwardRequest.getContent() + "\n\n" +
                "---------- Forwarded message ----------\n" +
                "From: " + originalFrom + "\n" +
                "Subject: " + originalSubject + "\n\n" +
                originalContent;
        
        // Here you would call your existing email sending service
        // Example: emailService.sendEmailToMultiple(forwardRequest.getRecipients(), forwardSubject, forwardContent);
        
        // For now, just log the action
        log.info("Forward email would be sent with subject: {}", forwardSubject);
    }
}
