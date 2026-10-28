package com.example.todoapp;

import java.time.LocalDate;

import lombok.Data;

@Data
public class DailyCompletionStats {
    private LocalDate completionDate;
    private long taskCount;
}
