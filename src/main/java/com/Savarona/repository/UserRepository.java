package com.Savarona.repository;

import com.Savarona.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Finding UserFirebase UID
    Optional<User> findByFirebaseUid(String firebaseUid);

    // Finding Email
    Optional<User> findByEmail(String email);

    // Checking existence Firebase UID
    boolean existsByFirebaseUid(String firebaseUid);

    // Checking existence Email
    boolean existsByEmail(String email);
}