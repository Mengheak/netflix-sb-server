package com.example.netflix_clone.controller;


import com.example.netflix_clone.dto.request.*;
import com.example.netflix_clone.dto.response.EmailValidationResponse;
import com.example.netflix_clone.dto.response.LoginResponse;
import com.example.netflix_clone.dto.response.MessageResponse;
import com.example.netflix_clone.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<MessageResponse> signup(@Valid @RequestBody UserRequest userRequest) {
        return ResponseEntity.ok(authService.signup(userRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = authService.login(loginRequest.getEmail(), loginRequest.getPassword());
        return ResponseEntity.ok(loginResponse);

    }

    @GetMapping("/send-link-verify-email")
    public ResponseEntity<MessageResponse> requestForVerifyEmailLink(@RequestParam String email) {
        return ResponseEntity.ok(authService.sendLinkVerifyEmail(email));
    }

    @GetMapping("/validate-email")
    public ResponseEntity<EmailValidationResponse> validateEmail(@RequestParam String email) {
        return ResponseEntity.ok(authService.validateEmail(email));
    }

    @GetMapping("/verify-email")
    public ResponseEntity<MessageResponse> verifyEmail(@RequestParam String token) {
        return ResponseEntity.ok(authService.verifyEmail(token));
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<MessageResponse> forgotPassword(
            @Valid @RequestBody EmailRequest emailRequest
    ){
        return ResponseEntity.ok(authService.forgotPassword(emailRequest.getEmail()));
    }
    @PostMapping("/reset-password")
    public ResponseEntity<MessageResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
        return ResponseEntity.ok(authService.resetPassword(resetPasswordRequest.getToken(), resetPasswordRequest.getNewPassword()));
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(Authentication authentication, @Valid @RequestBody ChangePasswordRequest changePasswordRequest) {

        String email = authentication.getName();


        return ResponseEntity.ok(authService.changePassword(email, changePasswordRequest.getCurrentPassword(), changePasswordRequest.getNewPassword()));
    }
    @GetMapping("/current-user")
    public ResponseEntity<LoginResponse> currentUser(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(authService.getCurrentUser(email));
    }
}
