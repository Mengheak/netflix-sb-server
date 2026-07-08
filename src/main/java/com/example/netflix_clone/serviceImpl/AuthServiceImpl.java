package com.example.netflix_clone.serviceImpl;

import com.example.netflix_clone.dao.UserRepository;
import com.example.netflix_clone.dto.request.EmailRequest;
import com.example.netflix_clone.dto.request.UserRequest;
import com.example.netflix_clone.dto.response.EmailValidationResponse;
import com.example.netflix_clone.dto.response.LoginResponse;
import com.example.netflix_clone.dto.response.MessageResponse;
import com.example.netflix_clone.entity.User;
import com.example.netflix_clone.enums.Role;
import com.example.netflix_clone.exception.*;
import com.example.netflix_clone.security.JwtUtil;
import com.example.netflix_clone.service.AuthService;
import com.example.netflix_clone.service.EmailService;
import com.example.netflix_clone.util.ServiceUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;
    private final ServiceUtils serviceUtils;

    @Override
    public MessageResponse signup(UserRequest userRequest) {
        if(userRepository.existsByEmail(userRequest.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }
        User user = new User();
        user.setEmail(userRequest.getEmail());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user.setFullName(userRequest.getFullName());
        user.setRole(Role.USER);
        user.setActive(true);
        user.setEmailVerified(false);
        String verificationToken = UUID.randomUUID().toString();
        user.setVerificationToken(verificationToken);
        user.setVerificationTokenExpiry(Instant.now().plusSeconds(86400));
        userRepository.save(user);
        emailService.sendVerificationEmail(user.getEmail(), verificationToken);
        return new MessageResponse("Registration Successful");
    }

    @Override
    public LoginResponse login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .filter((u) -> passwordEncoder.matches(password, u.getPassword()))
                .orElseThrow(() -> new BadCredentialException("Invalid email or password"));

        if(!user.isActive()) {
            throw new AccountDeactivatedException("Your account has been deactivated. Please contact support for assistance.");

        }
        if(!user.isEmailVerified()) {
            throw new EmailNotVerifiedException("Please verify your email address before logging in. Check your inbox for the verification link.");
        }
        final String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        return new LoginResponse(token, user.getEmail(), user.getFullName(), user.getRole().name());
    }

    @Override
    public EmailValidationResponse validateEmail(String email) {
        Boolean exists = userRepository.existsByEmail(email);
        return new EmailValidationResponse(exists, !exists);
    }

    @Override
    public MessageResponse verifyEmail(String token) {
        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new InvalidTokenException("Invalid or expired verification token"));
        if(user.getVerificationTokenExpiry().isBefore(Instant.now()) || user.getVerificationTokenExpiry() == null) {
            throw new InvalidTokenException("Verification link has expired. Please request a new one");
        }
        user.setEmailVerified(true);
        user.setVerificationToken(null);
        user.setVerificationTokenExpiry(null);
        userRepository.save(user);
        return new MessageResponse("Email verified successfully");
    }

    @Override
    public MessageResponse sendLinkVerifyEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User with email " + email + " not found"));
        user.setEmailVerified(false);
        String token = UUID.randomUUID().toString();
        user.setVerificationToken(token);
        user.setVerificationTokenExpiry(Instant.now().plusSeconds(86400));
        userRepository.save(user);
        emailService.sendVerificationEmail(user.getEmail(), token);
        return new MessageResponse("Link verify email resent successfully");
    }

    @Override
    public MessageResponse forgotPassword(String email) {
        User user = serviceUtils.getUserByEmailOrThrow(email);
        String resetToken = UUID.randomUUID().toString();
        user.setPasswordResetToken(resetToken);
        user.setPasswordResetTokenExpiry(Instant.now().plusSeconds(3600));
        userRepository.save(user);
        emailService.sendPasswordResetEmail(email, resetToken);
        return new MessageResponse("Forgot Password Reset email resent successfully. Please check your inbox.");
    }

    @Override
    public MessageResponse resetPassword(String token, String newPassword) {
        User user = userRepository.findByPasswordResetToken(token)
                .orElseThrow(() -> new InvalidTokenException("Invalid or expired reset token"));
        if(user.getPasswordResetTokenExpiry().isBefore(Instant.now()) || user.getPasswordResetTokenExpiry() == null) {
            throw new InvalidTokenException("Password reset token has expired. Please request a new one");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordResetToken(null);
        user.setPasswordResetTokenExpiry(null);
        userRepository.save(user);
        return new MessageResponse("Password reset successfully");
    }

    @Override
    public MessageResponse changePassword(String email, String currentPassword, String newPassword) {
        User user = serviceUtils.getUserByEmailOrThrow(email);
        if(!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new InvalidCredentialException("Current password does not match the old password");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return new MessageResponse("Password changed successfully");
    }

    @Override
    public LoginResponse getCurrentUser(String email) {
        User user = serviceUtils.getUserByEmailOrThrow(email);
        return new LoginResponse(null, user.getEmail(), user.getFullName(), user.getRole().name());
    }

}
