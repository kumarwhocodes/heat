package com.zerobee.heat.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

@Service
public class GmailService {
    private static final String APPLICATION_NAME = "Spring Gmail API";
    private static final List<String> SCOPES = List.of(GmailScopes.GMAIL_SEND, GmailScopes.GMAIL_READONLY);
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    private Gmail gmailService;
    private Credential credential;
    private GoogleAuthorizationCodeFlow flow;

    private void initFlow() throws Exception {
        if (flow != null) return;
        final NetHttpTransport HTTP_TRANSPORT = new NetHttpTransport();
        InputStream in = getClass().getClassLoader().getResourceAsStream("credentials.json");
        if (in == null) throw new FileNotFoundException("credentials.json not found");
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
                JacksonFactory.getDefaultInstance(), new InputStreamReader(in));
        this.flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JacksonFactory.getDefaultInstance(),
                clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
    }

    public String getAuthorizationUrl() throws Exception {
        initFlow();
        return flow.newAuthorizationUrl()
                .setRedirectUri("http://localhost:8080/api/gmail/callback")
                .build();
    }

    public String exchangeCodeForToken(String code) throws Exception {
        initFlow();
        com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse response = flow.newTokenRequest(code)
                .setRedirectUri("http://localhost:8080/api/gmail/callback")
                .execute();
        credential = flow.createAndStoreCredential(response, "user");
        this.gmailService = new Gmail.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance(), credential)
                .setApplicationName(APPLICATION_NAME).build();
        return credential.getAccessToken();
    }

    public Gmail getGmailService() throws Exception {
        if (gmailService == null) {
            initFlow();
            credential = flow.loadCredential("user");
            if (credential == null) throw new Exception("Not authenticated. Please authenticate first.");
            this.gmailService = new Gmail.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance(), credential)
                    .setApplicationName(APPLICATION_NAME).build();
        }
        return this.gmailService;
    }

    public void clearCredentials() throws Exception {
        initFlow();
        flow.getCredentialDataStore().delete("user");
        this.credential = null;
        this.gmailService = null;
    }
}