package com.Savarona.config;

import com.google.firebase.FirebaseApp;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class FirebaseTestController {

    @GetMapping("/firebase-status")
    public String checkFirebaseStatus() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                return "❌ Firebase CONNECTION FAILED - Application not found";
            }

            FirebaseApp defaultApp = FirebaseApp.getInstance();
            String projectId = defaultApp.getOptions().getProjectId();

            return "✅ Firebase SUCCESSFULLY CONNECTED!" +
                    "<br>Project ID: " + projectId +
                    "<br>App Name: " + defaultApp.getName() +
                    "<br>Status: Active";

        } catch (Exception e) {
            return "❌ Firebase ERROR: " + e.getMessage();
        }
    }

    @GetMapping("/simple-test")
    public String simpleTest() {
        return "🚀 Spring Boot is running! Visit /api/test/firebase-status for Firebase testing.";
    }
}