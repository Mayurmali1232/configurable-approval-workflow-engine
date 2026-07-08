package com.workflow.engine.service.impl;
import com.workflow.engine.constants.Role;
import com.workflow.engine.dto.AuthResponse;
import com.workflow.engine.dto.LoginRequest;
import com.workflow.engine.dto.RegisterRequest;
import com.workflow.engine.entity.User;
import com.workflow.engine.exception.WorkflowException;
import com.workflow.engine.repository.UserRepository;
import com.workflow.engine.security.JwtTokenProvider;
import com.workflow.engine.service.AuthService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JwtTokenProvider tokenProvider;

    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtTokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public void register(RegisterRequest request) {

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new WorkflowException("Username already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.valueOf(request.getRole().toUpperCase()));

        userRepository.save(user);
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new WorkflowException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new WorkflowException("Invalid username or password");
        }

        String token = tokenProvider.generateToken(
                user.getUsername(),
                user.getRole().name());

        return new AuthResponse(token, user.getUsername());
    }
}
