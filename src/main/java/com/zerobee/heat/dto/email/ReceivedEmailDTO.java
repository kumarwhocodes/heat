package com.zerobee.heat.dto.email;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReceivedEmailDTO {
    // Getters and Setters
    private String messageId;
    private String subject;
    private String fromEmail;
    private String fromName;
    private List<String> toEmails;
    private String textContent;
    private String htmlContent;
    private LocalDateTime receivedDate;
    private LocalDateTime sentDate;
    private boolean hasAttachments;
    private List<String> attachmentNames;
    
}
