package com.example.todoapp;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class AdminAccountInitializer implements ApplicationRunner {
    private final UserMapper userMapper;
    private final UserAccountService userAccountService;

    public AdminAccountInitializer(UserMapper userMapper, UserAccountService userAccountService) {
        this.userMapper = userMapper;
        this.userAccountService = userAccountService;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (userMapper.findByUsername("admin") == null) {
            userAccountService.createUser("admin", "edix", "ADMIN");
        }
    }
}
