
package com.Savarona.service;

import com.Savarona.entity.User;
import com.Savarona.repository.UserRepository;
import com.Savarona.security.model.SecurityUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUserFromSecurity(SecurityUser securityUser) {
        User user = new User();
        user.setEmail(securityUser.getEmail());
        user.setName(securityUser.getName());
        user.setFirebaseUid(securityUser.getUid());
        user.setCredits(0);
        return userRepository.save(user);
    }

    public Optional<User> findByFirebaseUid(String firebaseUid) {
        return userRepository.findByFirebaseUid(firebaseUid);
    }

    public User getOrCreateUser(SecurityUser securityUser) {
        return findByFirebaseUid(securityUser.getUid())
                .orElseGet(() -> createUserFromSecurity(securityUser));
    }
}