package com.example.demo;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void init() throws IOException {
        InputStream serviceAccount = 
                getClass().getClassLoader().getResourceAsStream("firebase-key.json");

        if (serviceAccount == null) {
            throw new IllegalStateException("Firebase key not found!");
        }

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
        }

        System.out.println("🔥 Firebase Admin Initialized");
    }
}
