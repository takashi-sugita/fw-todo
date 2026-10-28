package com.example.todoapp.security;

import com.example.todoapp.UserAccount;
import com.example.todoapp.UserMapper;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class DatabaseUserDetailsService implements UserDetailsService {
    private final UserMapper userMapper;

    public DatabaseUserDetailsService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAccount account = userMapper.findByUsername(username);
        if (account == null) {
            throw new UsernameNotFoundException("ユーザーが見つかりません");
        }
        String role = account.getRole();
        if (role.startsWith("ROLE_")) {
            role = role.substring("ROLE_".length());
        }
        return User.withUsername(account.getUsername())
                .password(account.getPassword())
                .roles(role)
                .build();
    }
}
