package com.zerobee.heat.dto.email;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SyncStatusDTO {
    private boolean isRunning;
    private String lastSyncTime;
    private int newEmailsFound;
    private String status;
}