package com.example.netflix_clone.dao;

import com.example.netflix_clone.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoRepository extends JpaRepository<User, Long> {
}
