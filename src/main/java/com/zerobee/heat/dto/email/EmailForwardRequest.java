package com.zerobee.heat.dto.email;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailForwardRequest {
    private List<String> recipients;
    private String content;
    private String subject;
    private boolean includeAttachments = true;
}