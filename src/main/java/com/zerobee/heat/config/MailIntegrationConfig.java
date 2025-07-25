package com.zerobee.heat.config;

import com.zerobee.heat.service.EmailReceiverService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.mail.ImapMailReceiver;
import org.springframework.integration.mail.MailReceivingMessageSource;
import org.springframework.messaging.Message;

import java.util.Properties;

@Slf4j
@Configuration
@EnableIntegration
@RequiredArgsConstructor
public class MailIntegrationConfig {
    
    private final EmailConfig emailConfig;
    private final EmailReceiverService emailReceiverService;
    
    @Bean
    public ImapMailReceiver imapMailReceiver() {
        ImapMailReceiver receiver = new ImapMailReceiver(emailConfig.getImapUrl());
        receiver.setShouldDeleteMessages(false);
        receiver.setShouldMarkMessagesAsRead(true);
        receiver.setJavaMailProperties(javaMailProperties());
        
        return receiver;
    }
    
    @Bean
    @InboundChannelAdapter(channel = "emailInputChannel",
            poller = @Poller(fixedDelay = "10000"))
    public MailReceivingMessageSource mailReceivingMessageSource() {
        return new MailReceivingMessageSource(imapMailReceiver());
    }
    
    @Bean
    public Properties javaMailProperties() {
        Properties props = new Properties();
        props.put("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.imap.socketFactory.fallback", "false");
        props.put("mail.store.protocol", "imaps");
        props.put("mail.debug", "false");
        props.put("mail.imap.ssl.enable", "true");
        props.put("mail.imap.connectiontimeout", "30000");
        props.put("mail.imap.timeout", "30000");
        return props;
    }
    
    @ServiceActivator(inputChannel = "emailInputChannel")
    public void handleIncomingEmail(Message<MimeMessage> message) {
        try {
            MimeMessage mimeMessage = message.getPayload();
            log.info("New email received via polling: {}", mimeMessage.getSubject());
            
            // Process the email
            emailReceiverService.processReceivedEmail(mimeMessage);
            
        } catch (Exception e) {
            log.error("Error handling incoming email", e);
        }
    }
}
