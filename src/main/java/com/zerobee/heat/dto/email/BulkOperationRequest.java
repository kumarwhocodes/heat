package com.zerobee.heat.dto.email;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BulkOperationRequest {
    private List<String> messageIds;
    private String targetFolder; // for move operations
}