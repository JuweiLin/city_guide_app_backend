package com.yourcompany.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.FirebaseAuthException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

@RestController
public class Controller {

    private static final Logger logger = LoggerFactory.getLogger(Controller.class);
    private final RequestHandler requestHandler;
    private final UserService userService;

    @Autowired  // Constructor injection for better practice
    public Controller(RequestHandler requestHandler, UserService userService) {
        this.requestHandler = requestHandler;
        this.userService = userService;
    }

    @GetMapping("/getNearbyPlaces")
    public ResponseEntity<?> getNearbyPlaces(
            @RequestParam double latitude, 
            @RequestParam double longitude, 
            @RequestParam int radius) {
        try {
            return ResponseEntity.ok(requestHandler.getNearbyPlaces(latitude, longitude, radius));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to retrieve places: " + e.getMessage());
        }
    }

    @PostMapping("/login")
        public ResponseEntity<?> loginUser(@RequestBody Map<String, String> loginData) {
            String idToken = loginData.get("idToken");

            try {
                FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
                String uid = decodedToken.getUid();

                boolean isActive = userService.checkUserIsActive(uid);
                if (!isActive) {
                    userService.checkOrCreateUser(uid);
                }

                Map<String, Object> response = new HashMap<>();
                response.put("uid", uid);
                response.put("isActive", isActive);
                response.put("message", "User status checked successfully.");

                return ResponseEntity.ok(response);
            } catch (FirebaseAuthException e) {
                logger.error("Authentication error", e);
                return ResponseEntity.status(500).body("Authentication failed: " + e.getMessage());
            } catch (Exception e) {
                logger.error("Failed to process login", e);
                return ResponseEntity.status(500).body("Server error: " + e.getMessage());
            }
        }
        @PostMapping("/updateProfile")
        public ResponseEntity<?> updateProfile(@RequestBody Map<String, Object> profileData) {
            String uid = (String) profileData.get("uid");
            if (uid == null || uid.isEmpty()) {
                return ResponseEntity.badRequest().body("{\"error\":\"UID is required.\"}");
            }
            profileData.remove("uid"); // Now remove uid after checking it's not null or empty
            try {
                userService.updateUserProfile(uid, profileData);
                return ResponseEntity.ok("{\"message\":\"Profile updated successfully.\"}");
            } catch (Exception e) {
                logger.error("Failed to update user profile", e);
                return ResponseEntity.status(500).body("{\"error\":\"Failed to update profile: " + e.getMessage() + "\"}");
            }
        }

        @PostMapping("/submitInterests")
        public ResponseEntity<?> submitInterests(@RequestBody Map<String, Object> interestsData) {
            String uid = (String) interestsData.get("uid");
            if (uid == null || uid.isEmpty()) {
                return ResponseEntity.badRequest().body("{\"error\":\"UID is required.\"}");
            }

            try {
                Map<String, Double> interests = new HashMap<>();
                interests.put("restaurant", (Double) interestsData.get("restaurant"));
                interests.put("museum", (Double) interestsData.get("museum"));
                interests.put("park", (Double) interestsData.get("park"));

                userService.saveUserInterests(uid, interests);

                return ResponseEntity.ok("{\"message\":\"Interests submitted successfully.\"}");
            } catch (Exception e) {
                logger.error("Failed to submit user interests", e);
                return ResponseEntity.status(500).body("{\"error\":\"Failed to submit interests: " + e.getMessage() + "\"}");

            }
        }
}
