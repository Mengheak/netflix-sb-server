package com.example.netflix_clone.serviceImpl;

import com.example.netflix_clone.dao.UserRepository;
import com.example.netflix_clone.dto.request.UserRequest;
import com.example.netflix_clone.dto.response.MessageResponse;
import com.example.netflix_clone.dto.response.PageResponse;
import com.example.netflix_clone.dto.response.UserResponse;
import com.example.netflix_clone.entity.User;
import com.example.netflix_clone.enums.Role;
import com.example.netflix_clone.exception.EmailAlreadyExistsException;
import com.example.netflix_clone.exception.InvalidRoleException;
import com.example.netflix_clone.service.EmailService;
import com.example.netflix_clone.service.UserService;
import com.example.netflix_clone.util.PaginationUtils;
import com.example.netflix_clone.util.ServiceUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Override
    public MessageResponse updateUser(Long id, UserRequest userRequest) {
        User user = serviceUtils.getUserByIdOrThrow(id);

        ensureNotLastActiveAdmin(user);
        validateRole(userRequest.getRole());
        user.setFullName(userRequest.getFullName());
        user.setRole(Role.valueOf(userRequest.getRole().toUpperCase()));
        userRepository.save(user);
        return new MessageResponse("User updated successfully");
    }

    @Override
    public PageResponse<UserResponse> getAllUsers(int page, int size, String search) {
        Pageable pageable = PaginationUtils.createPageRequest(page, size, "id");
        Page<User> userPage;
        if(search != null && !search.trim().isEmpty()) {
            userPage = userRepository.searchUsers(search, pageable);
        }else{
            userPage = userRepository.findAll(pageable);
        }
        return PaginationUtils.toPageResponse(userPage, UserResponse::fromEntity);
  }

    @Override
    public MessageResponse deleteUser(Long id, String currentUserEmail) {
        User user =  serviceUtils.getUserByIdOrThrow(id);
        if(user.getEmail().equals(currentUserEmail)) {
            throw new RuntimeException("You can not delete your own account");
        }
        ensureNotLastAdmin(user, "delete");
        userRepository.deleteById(id);
        return new MessageResponse("User deleted successfully");
    }

    @Override
    public MessageResponse toggleUserStatus(Long id, String currentUserEmail) {
        User user = serviceUtils.getUserByIdOrThrow(id);

        if(user.getEmail().equals(currentUserEmail)) {
            throw new RuntimeException("You can not deactivate your account");
        }
        ensureNotLastActiveAdmin(user);
        user.setActive(!user.isActive());
        userRepository.save(user);
        return new MessageResponse("User status updated successfully");
    }



    private void ensureNotLastAdmin(User user, String operation) {
        if(user.getRole() == Role.ADMIN) {
            long adminCount = userRepository.countByRole(Role.ADMIN);
            if(adminCount <= 1) {
                throw new RuntimeException("Cannot "+operation+"the last admin user");
            }
        }
    }

    private void ensureNotLastActiveAdmin(User user) {
        if(user.isActive() && user.getRole() == Role.ADMIN) {
            long activeAdminCount = userRepository.countByRoleAndActive(Role.ADMIN, true);
            if(activeAdminCount <= 1) {
                throw new RuntimeException("Cannot deactivate the last active admin user");
            }
        }
    }

    private void validateRole(String role) {
        if(Arrays.stream(Role.values()).noneMatch(r -> r.name().equalsIgnoreCase(role))) {
            throw new InvalidRoleException("Invalid role: "+role);
        }
    }

    @Override
    public MessageResponse changeRole(Long id, UserRequest userRequest) {
        User user = serviceUtils.getUserByIdOrThrow(id);
        validateRole(userRequest.getRole());
        Role newRole = Role.valueOf(userRequest.getRole().toUpperCase());
        if(user.getRole() == Role.ADMIN && newRole == Role.USER) {
            ensureNotLastAdmin(user, "change the role of");
        }
        user.setRole(newRole);
        userRepository.save(user);
        return new MessageResponse("Role changed successfully");
    }
}
