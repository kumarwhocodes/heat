package com.zerobee.heat.dto.email;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailReplyRequest {
    private String content;
    private boolean replyAll = false;
    private String subject;
}
