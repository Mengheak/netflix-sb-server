package com.example.netflix_clone.serviceImpl;

import com.example.netflix_clone.dao.UserRepository;
import com.example.netflix_clone.dto.request.UserRequest;
import com.example.netflix_clone.dto.response.MessageResponse;
import com.example.netflix_clone.entity.User;
import com.example.netflix_clone.enums.Role;
import com.example.netflix_clone.exception.EmailAlreadyExistsException;
import com.example.netflix_clone.exception.InvalidRoleException;
import com.example.netflix_clone.service.EmailService;
import com.example.netflix_clone.service.UserService;
import com.example.netflix_clone.util.ServiceUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ServiceUtils serviceUtils;
    private final EmailService emailService;

    @Override
    public MessageResponse createUser(UserRequest userRequest) {
        if(userRepository.existsByEmail(userRequest.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }
        validateRole(userRequest.getRole());
        User user = new User();
        user.setEmail(userRequest.getEmail());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user.setFullName(userRequest.getFullName());
        user.setRole(Role.valueOf(userRequest.getRole().toUpperCase()));
        String verificationToken = UUID.randomUUID().toString();
        user.setVerificationToken(verificationToken);
        user.setVerificationTokenExpiry(Instant.now().plusMillis(86400));
        userRepository.save(user);
        emailService.sendVerificationEmail(userRequest.getEmail(), verificationToken);
        return new MessageResponse("User created successfully. Please check your email and verify your email");
    }

    private void validateRole(String role) {
        if(Arrays.stream(Role.values()).noneMatch(r -> r.name().equalsIgnoreCase(role))) {
            throw new InvalidRoleException("Invalid role: "+role);
        }
    }
}
