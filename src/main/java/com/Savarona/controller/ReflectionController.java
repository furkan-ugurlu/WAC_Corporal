package com.Savarona.controller;

import com.Savarona.entity.Reflection;
import com.Savarona.entity.User;
import com.Savarona.repository.ReflectionRepository;
import com.Savarona.repository.UserRepository;
import com.Savarona.security.model.SecurityUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ReflectionController {

    @Autowired
    private ReflectionRepository reflectionRepository;

    @Autowired
    private UserRepository userRepository;

    // POST /api/reflections - save a reflection (authenticated)
    @PostMapping("/reflections")
    public ResponseEntity<?> createReflection(@RequestBody ReflectionRequest request,
                                              @AuthenticationPrincipal SecurityUser securityUser) {
        try {
            Optional<User> userOpt = userRepository.findByFirebaseUid(securityUser.getUid());
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("User not found");
            }

            User user = userOpt.get();

            Reflection reflection = new Reflection();
            reflection.setContent(request.getContent());
            reflection.setCreatedAt(LocalDateTime.now());
            reflection.setUser(user);// Set default city or modify as needed

            Reflection savedReflection = reflectionRepository.save(reflection);
            return ResponseEntity.ok(savedReflection);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error saving reflection: " + e.getMessage());
        }
    }

    // GET /api/reflections - list user's reflections
    @GetMapping("/reflections")
    public ResponseEntity<?> getUserReflections(@AuthenticationPrincipal SecurityUser securityUser) {
        try {
            Optional<User> userOpt = userRepository.findByFirebaseUid(securityUser.getUid());
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("User not found");
            }

            List<Reflection> reflections = reflectionRepository.findByUserId(userOpt.get().getId());
            return ResponseEntity.ok(reflections);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error fetching reflections: " + e.getMessage());
        }
    }

    // POST /api/credits/use - decrement user's credits (only if credits > 0)
    @PostMapping("/credits/use")
    public ResponseEntity<?> useCredit(@AuthenticationPrincipal SecurityUser securityUser) {
        try {
            Optional<User> userOpt = userRepository.findByFirebaseUid(securityUser.getUid());
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("User not found");
            }

            User user = userOpt.get();

            // Check if user has credits
            if (user.getCredits() <= 0) {
                return ResponseEntity.badRequest().body("Insufficient credits");
            }

            // Decrement credits
            user.setCredits(user.getCredits() - 1);
            userRepository.save(user);

            return ResponseEntity.ok(new CreditResponse(user.getCredits(), "Credit used successfully"));

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error using credit: " + e.getMessage());
        }
    }

    // Helper classes for request/response
    public static class ReflectionRequest {
        private String content;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

    public static class CreditResponse {
        private int remainingCredits;
        private String message;

        public CreditResponse(int remainingCredits, String message) {
            this.remainingCredits = remainingCredits;
            this.message = message;
        }

        public int getRemainingCredits() {
            return remainingCredits;
        }

        public void setRemainingCredits(int remainingCredits) {
            this.remainingCredits = remainingCredits;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}