package com.example.netflix_clone.util;

import com.example.netflix_clone.dao.UserRepository;
import com.example.netflix_clone.dao.VideoRepository;
import com.example.netflix_clone.entity.User;
import com.example.netflix_clone.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ServiceUtils {

    private final UserRepository userRepository;
    private final VideoRepository videoRepository;


    public User getUserByEmailOrThrow(String email){
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }
    public User getUserByIdOrThrow(Long id){
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }
}
