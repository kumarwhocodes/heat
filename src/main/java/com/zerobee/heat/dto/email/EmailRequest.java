package com.zerobee.heat.dto.email;

import lombok.Builder;
import lombok.Data;

import java.io.File;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class EmailRequest {
    private String to;
    private List<String> toList;
    private List<String> ccList;
    private List<String> bccList;
    private String subject;
    private String fromName;
    private String replyTo;
    
    // For simple text emails
    private String textContent;
    
    // For template-based emails
    private String templateName;
    private Map<String, Object> templateVariables;
    
    // Attachments
    private List<File> attachments;
    private Map<String, String> inlineAttachments; // key: cid, value: resource path
    
    // Email properties
    private Integer priority; // 1=High, 3=Normal, 5=Low
    private boolean readReceiptRequested;
    private String contentType;
}
