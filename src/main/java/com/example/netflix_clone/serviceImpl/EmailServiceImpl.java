package com.example.netflix_clone.serviceImpl;

import com.example.netflix_clone.exception.EmailSendingException;
import com.example.netflix_clone.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public void sendVerificationEmail(String toEmail, String token) {
        try{
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Netflix Clone - Verification Email");

            String verificationLink = frontendUrl + "/verify-email?token=" + token;
            String emailBody = "Welcome to Netflix Clone - Verification Email\n\n"
                    + verificationLink
                    +"\n\n"
                    +"This link will expire in 24 hours. \n\n"
                    +"If you didn't create this account, please ignore this email. \n\n"
                    +"Best regards. \n"
                    +"Netflix Clone Team";

            message.setText(emailBody);
            mailSender.send(message);
            log.info("Email sent to {} successfully", toEmail);
        }catch (Exception e){
            log.error("Failed to send verification email to {}: {}", toEmail, e.getMessage());
            throw new EmailSendingException("Failed to send verification email", e);
        }
    }


    @Override
    public void sendPasswordResetEmail(String toEmail, String token) {
        try{
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Netflix Clone - Password Reset Email");
            String resetLink = frontendUrl + "/reset-password?token=" + token;
            String emailBody = "Welcome to Netflix Clone - Password Reset\n\n"
                    + resetLink
                    +"\n\n"
                    +"This link will expire in 1 hours. \n\n"
                    +"If you didn't request a password reset, please ignore this email. \n\n"
                    +"Best regards. \n"
                    +"Netflix Clone Team";
            message.setText(emailBody);
            mailSender.send(message);
            log.info("Password reset email sent to {} successfully", toEmail);
        }catch (Exception e){
            log.error("Failed to send password reset email to {}: {}",toEmail, e.getMessage());
            throw new RuntimeException("Failed to send password reset email to " + toEmail);
        }
    }
}
