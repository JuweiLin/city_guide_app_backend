package com.yourcompany.app;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Configuration
public class FirebaseAdminInit {

    @PostConstruct
    public void initialize() {
        try {
            GoogleCredentials credentials = GoogleCredentials.fromStream(
                new ClassPathResource("city-guide-app-d48a9-firebase-adminsdk-6yu7j-b007994a73.json").getInputStream()
            );
            FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(credentials)
                .build();

            if (FirebaseApp.getApps().isEmpty()) { // <--- check with this line
                FirebaseApp.initializeApp(options);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to initialize Firebase", e);
        }
    }
}
