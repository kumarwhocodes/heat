package com.zerobee.heat.dto.email;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailCountDTO {
    private int totalEmails;
    private int unreadEmails;
    private int starredEmails;
    private int draftEmails;
    private int sentEmails;
    private int trashEmails;
    private int spamEmails;
}