package com.workflow.engine.service;

import com.workflow.engine.dto.AuthResponse;
import com.workflow.engine.dto.LoginRequest;
import com.workflow.engine.dto.RegisterRequest;

public interface AuthService {
    void register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}