package com.example.netflix_clone.service;

import com.example.netflix_clone.dto.request.EmailRequest;
import com.example.netflix_clone.dto.request.UserRequest;
import com.example.netflix_clone.dto.response.EmailValidationResponse;
import com.example.netflix_clone.dto.response.LoginResponse;
import com.example.netflix_clone.dto.response.MessageResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public interface AuthService {
    MessageResponse signup(@Valid UserRequest userRequest);

    LoginResponse login(String email, @NotBlank(message = "Password is required") String password);

    EmailValidationResponse validateEmail(String email);

    MessageResponse verifyEmail(String token);

    MessageResponse sendLinkVerifyEmail(String email);

    MessageResponse forgotPassword(String email);

    MessageResponse resetPassword(String token,String newPassword);

    MessageResponse changePassword(String email,String currentPassword, @NotBlank(message = "New password is required") String newPassword);

    LoginResponse getCurrentUser(String email);
}
