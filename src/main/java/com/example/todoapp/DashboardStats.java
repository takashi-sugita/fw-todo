package com.example.todoapp;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class DashboardStats {

    private long totalTasks;
    private long completedTasks;
    private long pendingTasks;
    private long overdueTasks;
    private List<String> dailyLabels = new ArrayList<>();
    private List<Long> dailyCompletions = new ArrayList<>();
    private List<String> categoryLabels = new ArrayList<>();
    private List<Long> categoryCounts = new ArrayList<>();
    private List<Todo> recentTasks = new ArrayList<>();

    public List<Long> getStatusCounts() {
        return List.of(pendingTasks - overdueTasks, completedTasks, overdueTasks);
    }
}
