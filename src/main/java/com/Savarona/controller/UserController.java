package com.Savarona.controller;

import com.Savarona.entity.User;
import com.Savarona.repository.UserRepository;
import com.Savarona.service.FirebaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*") // Accept requests from frontend
public class UserController {

    // Define logger
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private FirebaseService firebaseService;

    @Autowired
    private UserRepository userRepository;

    // Test endpoint - Check if backend is running
    @GetMapping("/test")
    public String test() {
        logger.info("📍 GET /api/users/test endpoint called");
        return "Backend API is running!";
    }

    // NEW TEST AUTH ENDPOINT - ADDED HERE
    @PostMapping("/test-auth")
    public ResponseEntity<?> testAuth(@RequestHeader("Authorization") String authHeader) {
        logger.info("📍 POST /api/users/test-auth endpoint called");
        logger.info("🔍 Test auth endpoint - Header: {}", authHeader);

        try {
            String token = authHeader.replace("Bearer ", "");
            logger.info("🔐 Token length: {} characters", token.length());

            String uid = firebaseService.getUidFromToken(token); // This method confirm token
            logger.info("✅ Token valid - UID: {}", uid);

            return ResponseEntity.ok("Token valid - UID: " + uid);
        } catch (Exception e) {
            logger.error("❌ Token invalid: {}", e.getMessage());
            return ResponseEntity.status(401).body("Token invalid: " + e.getMessage());
        }
    }

    // Simple hello endpoint (keeping old functionality)
    @GetMapping("/hello")
    public String hello() {
        logger.info("📍 GET /api/users/hello endpoint called");
        return "Hello World from Savarona User API";
    }

    // Dynamic hello endpoint
    @GetMapping("/hello/{name}")
    public String helloName(@PathVariable String name) {
        logger.info("📍 GET /api/users/hello/{} endpoint called - Incoming name: {}", name, name);
        return "Hello " + name + "! Welcome to Savarona User API!";
    }

    // User registration/login endpoint
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestHeader("Authorization") String authHeader) {
        logger.info("📍 POST /api/users/register endpoint called");
        logger.info("🔍 Incoming Authorization Header: '{}'", authHeader);

        try {
            // Clean token (remove "Bearer " part)
            String token = authHeader.replace("Bearer ", "");
            logger.info("🔐 Cleaned token length: {} characters", token.length());
            logger.info("🔐 Token beginning: {}", token.substring(0, Math.min(50, token.length())));

            // Firebase token validation (error will occur here)
            logger.info("🔐 Firebase token is being validated...");

            // Get information from Firebase token
            String firebaseUid = firebaseService.getUidFromToken(token);
            String email = firebaseService.getEmailFromToken(token);
            String name = firebaseService.getNameFromToken(token);

            logger.info("👤 Information from Firebase - UID: {}, Email: {}, Name: {}",
                    firebaseUid, email, name);

            // Check if user already exists
            Optional<User> existingUser = userRepository.findByFirebaseUid(firebaseUid);
            if (existingUser.isPresent()) {
                logger.info("✅ Existing user found - ID: {}", existingUser.get().getId());
                return ResponseEntity.ok(existingUser.get());
            }

            // Create new user
            User newUser = new User();
            newUser.setFirebaseUid(firebaseUid);
            newUser.setEmail(email);
            newUser.setName(name != null ? name : "User");
            newUser.setCredits(10); // Default credit

            User savedUser = userRepository.save(newUser);
            logger.info("🎉 New user created - ID: {}, Email: {}, Credits: {}",
                    savedUser.getId(), savedUser.getEmail(), savedUser.getCredits());

            return ResponseEntity.ok(savedUser);

        } catch (Exception e) {
            logger.error("❌ User registration failed: {}", e.getMessage());
            return ResponseEntity.status(401).body("Authentication failed: " + e.getMessage());
        }
    }

    // Get user profile endpoint
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        logger.info("📍 GET /api/users/me endpoint called");

        try {
            String token = authHeader.replace("Bearer ", "");
            String firebaseUid = firebaseService.getUidFromToken(token);
            logger.info("🔍 Searching for user - Firebase UID: {}", firebaseUid);

            Optional<User> user = userRepository.findByFirebaseUid(firebaseUid);
            if (user.isPresent()) {
                logger.info("✅ User found - ID: {}, Email: {}",
                        user.get().getId(), user.get().getEmail());
                return ResponseEntity.ok(user.get());
            } else {
                logger.warn("⚠️ User not found - Firebase UID: {}", firebaseUid);
                return ResponseEntity.status(404).body("User not found");
            }

        } catch (Exception e) {
            logger.error("❌ Getting user profile failed: {}", e.getMessage());
            return ResponseEntity.status(401).body("Authentication failed: " + e.getMessage());
        }
    }

    // Update user credits endpoint (bonus)
    @PutMapping("/credits")
    public ResponseEntity<?> updateCredits(@RequestHeader("Authorization") String authHeader,
                                           @RequestParam Integer credits) {
        logger.info("📍 PUT /api/users/credits endpoint called - New credit: {}", credits);

        try {
            String token = authHeader.replace("Bearer ", "");
            String firebaseUid = firebaseService.getUidFromToken(token);

            Optional<User> userOpt = userRepository.findByFirebaseUid(firebaseUid);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                Integer oldCredits = user.getCredits();
                user.setCredits(credits);
                User updatedUser = userRepository.save(user);

                logger.info("💰 Credits updated - User ID: {}, Old: {}, New: {}",
                        user.getId(), oldCredits, credits);
                return ResponseEntity.ok(updatedUser);
            } else {
                return ResponseEntity.status(404).body("User not found");
            }

        } catch (Exception e) {
            logger.error("❌ Credit update failed: {}", e.getMessage());
            return ResponseEntity.status(401).body("Authentication failed: " + e.getMessage());
        }
    }
}