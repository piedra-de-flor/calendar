package com.example.calendar.service.schedule.google;

import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.StoredCredential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.DataStore;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.List;

@Service
public class GoogleAuthorizationService {

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    @Value("${google.credentials.file-path}")
    private String credentialsFilePath;

    @Value("#{'${google.scopes}'.split(',')}")
    private List<String> scopes;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    public Credential getCredentials(String email) throws IOException, GeneralSecurityException {
        InputStream in = new FileInputStream(credentialsFilePath);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + credentialsFilePath);
        }

        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
        NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

        FileDataStoreFactory dataStoreFactory = new FileDataStoreFactory(
                new java.io.File(TOKENS_DIRECTORY_PATH + "/" + sanitizeEmail(email))
        );

        Credential credential = getStoredCredential(dataStoreFactory, email);
        if (credential != null) {
            return credential;
        }

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, JSON_FACTORY, clientSecrets, scopes)
                .setDataStoreFactory(dataStoreFactory)
                .setAccessType("offline")
                .build();

        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize(email);
    }

    private String sanitizeEmail(String email) {
        return email.replaceAll("@", "_at_").replaceAll("\\.", "_dot_");
    }

    private Credential getStoredCredential(DataStoreFactory dataStoreFactory, String email) throws IOException, GeneralSecurityException {
        DataStore<StoredCredential> dataStore = StoredCredential.getDefaultDataStore(dataStoreFactory);
        StoredCredential storedCredential = dataStore.get(email);

        if (storedCredential != null) {
            Credential credential = new Credential.Builder(BearerToken.authorizationHeaderAccessMethod())
                    .setTransport(GoogleNetHttpTransport.newTrustedTransport())
                    .setJsonFactory(JSON_FACTORY)
                    .setClientAuthentication(new ClientParametersAuthentication(clientId, clientSecret))
                    .setTokenServerUrl(new GenericUrl("https://oauth2.googleapis.com/token"))
                    .build()
                    .setAccessToken(storedCredential.getAccessToken())
                    .setRefreshToken(storedCredential.getRefreshToken());

            if (credential.getAccessToken() == null || credential.getExpiresInSeconds() != null && credential.getExpiresInSeconds() <= 3600) {
                credential.refreshToken();
            }

            return credential;
        }
        return null;
    }
}

