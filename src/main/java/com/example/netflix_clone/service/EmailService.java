package com.example.netflix_clone.service;

public interface EmailService {

    void sendVerificationEmail(String toEmail, String token);
    void sendPasswordResetEmail(String toEmail, String token);


}
