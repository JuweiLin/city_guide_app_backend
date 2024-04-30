package com.yourcompany.app;

import com.google.cloud.firestore.DocumentReference;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import org.springframework.stereotype.Service;
import org.springframework.core.io.ClassPathResource;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.io.IOException;
@Service
public class UserService {

    private final Firestore db;

    public UserService() {
        try {
            GoogleCredentials credentials = GoogleCredentials.fromStream(
                new ClassPathResource("city-guide-app-d48a9-firebase-adminsdk-6yu7j-b007994a73.json").getInputStream()
            );

            FirestoreOptions firestoreOptions = FirestoreOptions.newBuilder()
                .setCredentials(credentials)
                .setProjectId("city-guide-app-d48a9")
                .build();

            this.db = firestoreOptions.getService();
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize Firestore", e);
        }
    }

    public void checkOrCreateUser(String uid) throws ExecutionException, InterruptedException {
        DocumentReference docRef = db.collection("users").document(uid);
        if (!docRef.get().get().exists()) {
            Map<String, Object> newUser = new HashMap<>();
            newUser.put("isActive", false);
            newUser.put("createdAt", com.google.cloud.Timestamp.now());
            docRef.set(newUser).get();
        }
    }

    public boolean checkUserIsActive(String uid) throws ExecutionException, InterruptedException {
        DocumentReference docRef = db.collection("users").document(uid);
        if (docRef.get().get().exists()) {
            Boolean isActive = docRef.get().get().getBoolean("isActive");
            return isActive != null && isActive;
        } else {
            return false;
        }
    }

    public void storeUserInfo(String firstName, String lastName, int age, String gender, String occupation, String phone) throws ExecutionException, InterruptedException {
       
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("firstName", firstName);
        userInfo.put("lastName", lastName);
        userInfo.put("age", age);
        userInfo.put("gender", gender);
        userInfo.put("occupation", occupation);
        userInfo.put("phone", phone);

        
        String userDocId = firstName.toLowerCase() + "-" + lastName.toLowerCase();
        DocumentReference docRef = db.collection("users").document(userDocId);

        
        docRef.set(userInfo).get(); 
    }

    public void activateUser(String firstName, String lastName) throws ExecutionException, InterruptedException {
        String userDocId = firstName.toLowerCase() + "-" + lastName.toLowerCase();
        DocumentReference docRef = db.collection("users").document(userDocId);

        if (docRef.get().get().exists()) {
            docRef.update("isActive", true).get();
        } else {
            throw new IllegalStateException("User document does not exist");
        }
    }
}

 
