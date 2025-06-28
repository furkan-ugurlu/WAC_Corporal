package com.Savarona.controller;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/auth")
public class FirebaseAuthController {

    @PostMapping("/verify-token")
    public ResponseEntity<?> verifyToken(@RequestHeader("Authorization") String authHeader) {
        try {
            // Authorization header control
            if (authHeader == null || authHeader.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(createErrorResponse("Authorization header required"));
            }

            // Bearer prefix control
            if (!authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(createErrorResponse("Invalid authorization format"));
            }

            // Mine The Token
            String idToken = authHeader.substring(7); // "Bearer " = 7 characters

            // Token boş kontrolü
            if (idToken.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(createErrorResponse("Token gerekli"));
            }

            // Token Verification
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);

            // Token bilgilerini döndür
            Map<String, Object> response = new HashMap<>();
            response.put("uid", decodedToken.getUid());
            response.put("email", decodedToken.getEmail());
            response.put("name", decodedToken.getName());
            response.put("issuer", decodedToken.getIssuer());
            response.put("isEmailVerified", decodedToken.isEmailVerified());

            return ResponseEntity.ok(response);

        } catch (FirebaseAuthException e) {
            // Error handling for FirebaseAuthException
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse("Token cannot be verified: " ));
        } catch (Exception e) {
            // General error handling
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Unexpected error occurred: " ));
        }
    }

    @GetMapping("/user/{uid}")
    public ResponseEntity<?> getUserInfo(@PathVariable String uid) {
        try {
            // UID control
            if (uid == null || uid.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(createErrorResponse("UID required"));
            }

            UserRecord userRecord = FirebaseAuth.getInstance().getUser(uid);

            Map<String, Object> response = new HashMap<>();
            response.put("uid", userRecord.getUid());
            response.put("email", userRecord.getEmail());
            response.put("displayName", userRecord.getDisplayName());
            response.put("photoUrl", userRecord.getPhotoUrl());
            response.put("emailVerified", userRecord.isEmailVerified());

            return ResponseEntity.ok(response);

        } catch (FirebaseAuthException e) {
            // Specific error handling for FirebaseAuthException
            if (e.getErrorCode().equals("USER_NOT_FOUND")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(createErrorResponse("User not found"));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("User retrieval failed: "));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Unexpected error occurred: "));
        }
    }

    // Error response helper method
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", true);
        error.put("message", message);
        error.put("timestamp", System.currentTimeMillis());
        return error;
    }
}