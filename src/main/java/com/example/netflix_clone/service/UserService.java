package com.example.netflix_clone.service;

import com.example.netflix_clone.dto.request.UserRequest;
import com.example.netflix_clone.dto.response.MessageResponse;

public interface UserService {
    MessageResponse createUser(UserRequest userRequest);
}
