package com.workflow.engine.config;

import com.workflow.engine.constants.Role;
import com.workflow.engine.entity.User;
import com.workflow.engine.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

  
    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            
            User requester = new User();
            requester.setUsername("mayur");
            requester.setPassword(passwordEncoder.encode("password"));
            requester.setRole(Role.REQUESTER);
            userRepository.save(requester);

            User approver = new User();
            approver.setUsername("ganesh");
            approver.setPassword(passwordEncoder.encode("password"));
            approver.setRole(Role.APPROVER);
            userRepository.save(approver);

            User admin = new User();
            admin.setUsername("rushi");
            admin.setPassword(passwordEncoder.encode("password"));
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);

          
        }
    }
}