package com.example.calendar.service.google;

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

    /**
     * Retrieve Credential for the given email.
     * If no Credential exists, initiate the OAuth2 flow and save the new Credential.
     */
    public Credential getCredentials(String email) throws IOException, GeneralSecurityException {
        InputStream in = new FileInputStream(credentialsFilePath);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + credentialsFilePath);
        }

        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

        // 이메일 기반 DataStoreFactory 생성
        FileDataStoreFactory dataStoreFactory = new FileDataStoreFactory(
                new java.io.File(TOKENS_DIRECTORY_PATH + "/" + sanitizeEmail(email))
        );

        // GoogleAuthorizationCodeFlow 생성
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, JSON_FACTORY, clientSecrets, scopes)
                .setDataStoreFactory(dataStoreFactory)
                .setAccessType("offline")
                .build();

        // 기존 Credential이 존재하는지 확인
        Credential credential = getStoredCredential(dataStoreFactory, email);
        if (credential != null) {
            return credential; // 기존 Credential 반환
        }

        // Credential이 없으면 OAuth2 플로우 실행
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    /**
     * 이메일 기반으로 디렉터리 이름을 생성.
     */
    private String sanitizeEmail(String email) {
        return email.replaceAll("@", "_at_").replaceAll("\\.", "_dot_");
    }

    /**
     * 기존에 저장된 Credential이 있는지 확인.
     */
    private Credential getStoredCredential(DataStoreFactory dataStoreFactory, String email) throws IOException, GeneralSecurityException {
        DataStore<StoredCredential> dataStore = StoredCredential.getDefaultDataStore(dataStoreFactory);
        StoredCredential storedCredential = dataStore.get("user");

        if (storedCredential != null) {
            return new Credential.Builder(BearerToken.authorizationHeaderAccessMethod())
                    .setTransport(GoogleNetHttpTransport.newTrustedTransport())
                    .setJsonFactory(JSON_FACTORY)
                    .setClientAuthentication(new ClientParametersAuthentication("YOUR_CLIENT_ID", "YOUR_CLIENT_SECRET"))
                    .setTokenServerUrl(new GenericUrl("https://oauth2.googleapis.com/token"))
                    .build()
                    .setAccessToken(storedCredential.getAccessToken())
                    .setRefreshToken(storedCredential.getRefreshToken());
        }
        return null; // 저장된 Credential이 없으면 null 반환
    }

}

