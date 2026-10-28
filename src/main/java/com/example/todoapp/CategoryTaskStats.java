package com.example.todoapp;

import lombok.Data;

@Data
public class CategoryTaskStats {
    private String category;
    private long taskCount;
}
