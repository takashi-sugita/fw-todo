package com.example.todoapp;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    UserAccount findByUsername(@Param("username") String username);
    int insert(UserAccount user);
}
