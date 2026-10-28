package com.example.todoapp;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserAccountService {
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserAccountService(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void createUser(String username, String rawPassword, String role) {
        UserAccount account = new UserAccount();
        account.setUsername(username);
        account.setPassword(passwordEncoder.encode(rawPassword));
        account.setRole(role == null || role.isBlank() ? "USER" : role);
        userMapper.insert(account);
    }
}
