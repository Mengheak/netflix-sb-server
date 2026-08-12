package com.example.netflix_clone.dao;

import com.example.netflix_clone.entity.User;
import com.example.netflix_clone.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Boolean existsByEmail(String email);
    Optional<User> findByVerificationToken(String verificationToken);
    Optional<User> findByPasswordResetToken(String passwordResetToken);

    long countByRoleAndActive(Role role, boolean active);

    @Query("SELECT u FROM User u WHERE "
            +"LOWER(u.fullName) LIKE LOWER(CONCAT('%', :search, '%')) OR "
            +"LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))"
    )
    Page<User> searchUsers(@Param("search") String search, Pageable pageable);

    long countByRole(Role role);
}
