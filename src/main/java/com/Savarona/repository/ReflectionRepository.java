package com.Savarona.repository;

import com.Savarona.entity.Reflection;
import com.Savarona.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReflectionRepository extends JpaRepository<Reflection, Long> {

    // Basic user queries
    List<Reflection> findByUserId(Long userId);
    List<Reflection> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Reflection> findTop10ByUserIdOrderByCreatedAtDesc(Long userId);

    // Date range queries
    List<Reflection> findByUserIdAndCreatedAtBetween(Long userId, LocalDateTime start, LocalDateTime end);

    // Search functionality
    List<Reflection> findByUserIdAndContentContainingIgnoreCase(Long userId, String keyword);

    // Analytics
    long countByUserId(Long userId);

    // Security - ensure user owns the reflection
    Optional<Reflection> findByIdAndUserId(Long reflectionId, Long userId);

    // Cleanup
    void deleteAllByUserId(Long userId);

    // Custom queries for specific features
    @Query("SELECT r FROM Reflection r WHERE r.user.id = :userId AND r.createdAt >= :since ORDER BY r.createdAt DESC")
    List<Reflection> findRecentReflections(@Param("userId") Long userId, @Param("since") LocalDateTime since);
}