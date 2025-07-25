package com.zerobee.heat.controller;

import com.zerobee.heat.dto.email.*;
import com.zerobee.heat.service.EmailManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/emails")
@RequiredArgsConstructor
public class EmailController {
    
    private final EmailManagementService emailManagementService;
    
    @GetMapping("/inbox")
    public ResponseEntity<List<ReceivedEmailDTO>> getInboxEmails(
            @RequestParam(defaultValue = "10") int limit) {
        List<ReceivedEmailDTO> emails = emailManagementService.getRecentEmails(limit);
        return ResponseEntity.ok(emails);
    }
    
    @GetMapping("/unread")
    public ResponseEntity<List<ReceivedEmailDTO>> getUnreadEmails() {
        List<ReceivedEmailDTO> emails = emailManagementService.getUnreadEmails();
        return ResponseEntity.ok(emails);
    }
    
    @PostMapping("/mark-read/{messageId}")
    public ResponseEntity<String> markAsRead(@PathVariable String messageId) {
        emailManagementService.markAsRead(messageId);
        return ResponseEntity.ok("Email marked as read");
    }
    
    // Email Status Management
    @PostMapping("/mark-unread/{messageId}")
    public ResponseEntity<String> markAsUnread(@PathVariable String messageId) {
        emailManagementService.markAsUnread(messageId);
        return ResponseEntity.ok("Email marked as unread");
    }
    
    @PostMapping("/star/{messageId}")
    public ResponseEntity<String> starEmail(@PathVariable String messageId) {
        emailManagementService.starEmail(messageId);
        return ResponseEntity.ok("Email starred");
    }
    
    @PostMapping("/unstar/{messageId}")
    public ResponseEntity<String> unstarEmail(@PathVariable String messageId) {
        emailManagementService.unstarEmail(messageId);
        return ResponseEntity.ok("Email unstarred");
    }
    
    @GetMapping("/starred")
    public ResponseEntity<List<ReceivedEmailDTO>> getStarredEmails() {
        List<ReceivedEmailDTO> emails = emailManagementService.getStarredEmails();
        return ResponseEntity.ok(emails);
    }
    
    @PostMapping("/delete/{messageId}")
    public ResponseEntity<String> deleteEmail(@PathVariable String messageId) {
        emailManagementService.deleteEmail(messageId);
        return ResponseEntity.ok("Email deleted");
    }
    
    // Search Functionality
    @PostMapping("/search")
    public ResponseEntity<List<ReceivedEmailDTO>> searchEmails(@RequestBody EmailSearchRequest searchRequest) {
        List<ReceivedEmailDTO> emails = emailManagementService.searchEmails(searchRequest);
        return ResponseEntity.ok(emails);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<ReceivedEmailDTO>> searchEmailsGet(
            @RequestParam(required = false) String subject,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) Boolean hasAttachments,
            @RequestParam(required = false) Boolean isUnread,
            @RequestParam(required = false) Boolean isStarred,
            @RequestParam(defaultValue = "20") Integer limit) {
        
        EmailSearchRequest searchRequest = new EmailSearchRequest();
        searchRequest.setSubject(subject);
        searchRequest.setFrom(from);
        searchRequest.setTo(to);
        searchRequest.setContent(content);
        searchRequest.setStartDate(startDate);
        searchRequest.setEndDate(endDate);
        searchRequest.setHasAttachments(hasAttachments);
        searchRequest.setIsUnread(isUnread);
        searchRequest.setIsStarred(isStarred);
        searchRequest.setLimit(limit);
        
        List<ReceivedEmailDTO> emails = emailManagementService.searchEmails(searchRequest);
        return ResponseEntity.ok(emails);
    }
    
    // Folder Management
    @GetMapping("/folders")
    public ResponseEntity<List<String>> getAllFolders() {
        List<String> folders = emailManagementService.getAllFolders();
        return ResponseEntity.ok(folders);
    }
    
    @PostMapping("/folders/{folderName}")
    public ResponseEntity<String> createFolder(@PathVariable String folderName) {
        emailManagementService.createFolder(folderName);
        return ResponseEntity.ok("Folder '" + folderName + "' created successfully");
    }
    
    @DeleteMapping("/folders/{folderName}")
    public ResponseEntity<String> deleteFolder(@PathVariable String folderName) {
        emailManagementService.deleteFolder(folderName);
        return ResponseEntity.ok("Folder '" + folderName + "' deleted successfully");
    }
    
    @GetMapping("/folders/{folderName}/emails")
    public ResponseEntity<List<ReceivedEmailDTO>> getEmailsByFolder(
            @PathVariable String folderName,
            @RequestParam(defaultValue = "10") int limit) {
        List<ReceivedEmailDTO> emails = emailManagementService.getEmailsByFolder(folderName, limit);
        return ResponseEntity.ok(emails);
    }
    
    @PostMapping("/move/{messageId}/to/{folderName}")
    public ResponseEntity<String> moveEmailToFolder(
            @PathVariable String messageId,
            @PathVariable String folderName) {
        emailManagementService.moveEmailToFolder(messageId, folderName);
        return ResponseEntity.ok("Email moved to folder '" + folderName + "'");
    }
    
    // Bulk Operations
    @PostMapping("/bulk/mark-read")
    public ResponseEntity<String> bulkMarkAsRead(@RequestBody BulkOperationRequest request) {
        emailManagementService.bulkMarkAsRead(request.getMessageIds());
        return ResponseEntity.ok("Bulk marked " + request.getMessageIds().size() + " emails as read");
    }
    
    @PostMapping("/bulk/mark-unread")
    public ResponseEntity<String> bulkMarkAsUnread(@RequestBody BulkOperationRequest request) {
        emailManagementService.bulkMarkAsUnread(request.getMessageIds());
        return ResponseEntity.ok("Bulk marked " + request.getMessageIds().size() + " emails as unread");
    }
    
    @PostMapping("/bulk/star")
    public ResponseEntity<String> bulkStarEmails(@RequestBody BulkOperationRequest request) {
        emailManagementService.bulkStarEmails(request.getMessageIds());
        return ResponseEntity.ok("Bulk starred " + request.getMessageIds().size() + " emails");
    }
    
    @PostMapping("/bulk/unstar")
    public ResponseEntity<String> bulkUnstarEmails(@RequestBody BulkOperationRequest request) {
        emailManagementService.bulkUnstarEmails(request.getMessageIds());
        return ResponseEntity.ok("Bulk unstarred " + request.getMessageIds().size() + " emails");
    }
    
    @PostMapping("/bulk/delete")
    public ResponseEntity<String> bulkDeleteEmails(@RequestBody BulkOperationRequest request) {
        emailManagementService.bulkDeleteEmails(request.getMessageIds());
        return ResponseEntity.ok("Bulk deleted " + request.getMessageIds().size() + " emails");
    }
    
    @PostMapping("/bulk/move")
    public ResponseEntity<String> bulkMoveEmails(@RequestBody BulkOperationRequest request) {
        if (request.getTargetFolder() == null || request.getTargetFolder().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Target folder is required for bulk move operation");
        }
        
        emailManagementService.bulkMoveEmails(request.getMessageIds(), request.getTargetFolder());
        return ResponseEntity.ok("Bulk moved " + request.getMessageIds().size() +
                " emails to folder '" + request.getTargetFolder() + "'");
    }
    
    // Email Statistics
    @GetMapping("/stats")
    public ResponseEntity<EmailStatsDTO> getEmailStatistics() {
        EmailStatsDTO stats = emailManagementService.getEmailStatistics();
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/{messageId}/attachments")
    public ResponseEntity<List<AttachmentDTO>> getEmailAttachments(@PathVariable String messageId) {
        List<AttachmentDTO> attachments = emailManagementService.getEmailAttachments(messageId);
        return ResponseEntity.ok(attachments);
    }
    
    @GetMapping("/{messageId}/attachments/{attachmentName}/download")
    public ResponseEntity<byte[]> downloadAttachment(@PathVariable String messageId,
                                                     @PathVariable String attachmentName) {
        byte[] attachmentData = emailManagementService.downloadAttachment(messageId, attachmentName);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", attachmentName);
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(attachmentData);
    }
    
    @GetMapping("/count")
    public ResponseEntity<EmailCountDTO> getEmailCounts() {
        EmailCountDTO counts = emailManagementService.getEmailCounts();
        return ResponseEntity.ok(counts);
    }
    
    @PostMapping("/reply/{messageId}")
    public ResponseEntity<String> replyToEmail(@PathVariable String messageId,
                                               @RequestBody EmailReplyRequest request) {
        emailManagementService.replyToEmail(messageId, request);
        return ResponseEntity.ok("Reply sent successfully");
    }
    
    @PostMapping("/forward/{messageId}")
    public ResponseEntity<String> forwardEmail(@PathVariable String messageId,
                                               @RequestBody EmailForwardRequest request) {
        emailManagementService.forwardEmail(messageId, request);
        return ResponseEntity.ok("Email forwarded successfully");
    }
    
    @GetMapping("/notifications/unread-count")
    public ResponseEntity<Integer> getUnreadCount() {
        int unreadCount = emailManagementService.getUnreadCount();
        return ResponseEntity.ok(unreadCount);
    }
    
    @PostMapping("/sync/trigger")
    public ResponseEntity<SyncStatusDTO> triggerSync() {
        SyncStatusDTO syncStatus = emailManagementService.triggerSync();
        return ResponseEntity.ok(syncStatus);
    }
}

