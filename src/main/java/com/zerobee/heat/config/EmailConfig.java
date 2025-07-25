package com.zerobee.heat.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@Configuration
@Getter
public class EmailConfig {
    
    // Getters
    @Value("${mail.imap.host}")
    private String imapHost;
    
    @Value("${mail.imap.port}")
    private String imapPort;
    
    @Value("${mail.imap.username}")
    private String username;
    
    @Value("${mail.imap.password}")
    private String password;
    
    @Value("${mail.imap.folder}")
    private String folder;
    
    @Value("${mail.imap.poll-rate}")
    private long pollRate;
    
    public String getImapUrl() {
        // URL encode the @ symbol in email
        String encodedUsername = username.replace("@", "%40");
        return String.format("imaps://%s:%s@%s:%s/%s",
                encodedUsername, password, imapHost, imapPort, folder);
    }
    
}
