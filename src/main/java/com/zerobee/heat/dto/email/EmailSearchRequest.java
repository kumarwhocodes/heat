package com.zerobee.heat.dto.email;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class EmailSearchRequest {
    private String subject;
    private String from;
    private String to;
    private String content;
    private String startDate; // YYYY-MM-DD format
    private String endDate;
    private Boolean hasAttachments;
    private Boolean isUnread;
    private Boolean isStarred;
    private Integer limit = 20;
}
