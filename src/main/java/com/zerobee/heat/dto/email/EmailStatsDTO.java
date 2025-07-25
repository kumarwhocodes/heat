package com.zerobee.heat.dto.email;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailStatsDTO {
    private int totalEmails;
    private int unreadCount;
    private int starredCount;
    private int todayCount;
    private int attachmentCount;
    private long storageUsed; // in bytes
}