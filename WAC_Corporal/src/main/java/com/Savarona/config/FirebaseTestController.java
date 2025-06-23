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
                return "❌ Firebase BAĞLANAMADI - Uygulama bulunamadı";
            }

            FirebaseApp defaultApp = FirebaseApp.getInstance();
            String projectId = defaultApp.getOptions().getProjectId();

            return "✅ Firebase BAŞARIYLA BAĞLANDI!" +
                    "<br>Project ID: " + projectId +
                    "<br>App Name: " + defaultApp.getName() +
                    "<br>Durum: Aktif";

        } catch (Exception e) {
            return "❌ Firebase HATASI: " + e.getMessage();
        }
    }

    @GetMapping("/simple-test")
    public String simpleTest() {
        return "🚀 Spring Boot çalışıyor! Firebase test için /api/test/firebase-status adresini ziyaret edin.";
    }
}