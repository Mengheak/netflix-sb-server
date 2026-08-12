package com.example.netflix_clone.controller;

import com.example.netflix_clone.dto.request.UserRequest;
import com.example.netflix_clone.dto.response.MessageResponse;
import com.example.netflix_clone.dto.response.PageResponse;
import com.example.netflix_clone.dto.response.UserResponse;
import com.example.netflix_clone.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<MessageResponse> createUser(@RequestBody UserRequest userRequest) {
        return ResponseEntity.ok(userService.createUser(userRequest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MessageResponse> updateUser(@PathVariable("id") Long id, @RequestBody UserRequest userRequest) {
        return ResponseEntity.ok(userService.updateUser(id, userRequest));
    }

    @GetMapping
    public ResponseEntity<PageResponse<UserResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search
    ){
        return ResponseEntity.ok(userService.getAllUsers(page, size, search));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteUser(@PathVariable("id") Long id, Authentication authentication) {
        String currentUserEmail = authentication.getName();
        return ResponseEntity.ok(userService.deleteUser(id, currentUserEmail));
    }

    @PutMapping("/{id}/toggle-status")
    public ResponseEntity<MessageResponse> toggleUserStatus(@PathVariable("id") Long id, Authentication authentication) {
        String currentUserEmail = authentication.getName();
        return ResponseEntity.ok(userService.toggleUserStatus(id, currentUserEmail));
    }

    @PutMapping("/{id}/change-role")
    public ResponseEntity<MessageResponse> changeRole(@PathVariable("id") Long id, @RequestBody UserRequest userRequest) {
        return ResponseEntity.ok(userService.changeRole(id, userRequest));
    }
}
