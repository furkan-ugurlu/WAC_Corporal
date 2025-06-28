package com.Savarona.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class FirebaseService {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseService.class);

    public String getUidFromToken(String token) throws Exception {
        try {
            logger.info("🔥 Firebase token doğrulanıyor...");
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
            String uid = decodedToken.getUid();
            logger.info("✅ Token doğrulandı - UID: {}", uid);
            return uid;
        } catch (Exception e) {
            logger.error("❌ Firebase token doğrulama hatası: {}", e.getMessage());
            throw new Exception("Invalid Firebase token: " + e.getMessage());
        }
    }

    public String getEmailFromToken(String token) throws Exception {
        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
            String email = decodedToken.getEmail();
            logger.info("✅ Email alındı: {}", email);
            return email;
        } catch (Exception e) {
            logger.error("❌ Email alma hatası: {}", e.getMessage());
            throw new Exception("Cannot get email from token: " + e.getMessage());
        }
    }

    public String getNameFromToken(String token) throws Exception {
        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
            String name = decodedToken.getName();
            if (name == null) {
                name = (String) decodedToken.getClaims().get("name");
            }
            logger.info("✅ Name alındı: {}", name);
            return name;
        } catch (Exception e) {
            logger.error("❌ Name alma hatası: {}", e.getMessage());
            return "User"; // Fallback değer
        }
    }
}