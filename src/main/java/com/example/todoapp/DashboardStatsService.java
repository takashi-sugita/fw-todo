package com.example.todoapp;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class DashboardStatsService {

    private static final DateTimeFormatter CHART_DATE_FORMAT = DateTimeFormatter.ofPattern("M/d");
    private final DashboardStatsMapper dashboardStatsMapper;

    public DashboardStatsService(DashboardStatsMapper dashboardStatsMapper) {
        this.dashboardStatsMapper = dashboardStatsMapper;
    }

    public DashboardStats getDashboardStats() {
        LocalDate today = LocalDate.now();
        LocalDate firstDay = today.minusDays(6);
        DashboardStats stats = dashboardStatsMapper.selectTaskSummary();
        if (stats == null) {
            stats = new DashboardStats();
        }
        stats.setPendingTasks(stats.getTotalTasks() - stats.getCompletedTasks());

        Map<LocalDate, Long> dailyCounts = new HashMap<>();
        dashboardStatsMapper.selectDailyCompletions(firstDay, today)
                .forEach(item -> dailyCounts.put(item.getCompletionDate(), item.getTaskCount()));
        for (LocalDate date = firstDay; !date.isAfter(today); date = date.plusDays(1)) {
            stats.getDailyLabels().add(date.format(CHART_DATE_FORMAT));
            stats.getDailyCompletions().add(dailyCounts.getOrDefault(date, 0L));
        }

        List<CategoryTaskStats> categories = dashboardStatsMapper.selectCategoryCounts();
        for (CategoryTaskStats item : categories) {
            stats.getCategoryLabels().add(item.getCategory());
            stats.getCategoryCounts().add(item.getTaskCount());
        }
        stats.setRecentTasks(dashboardStatsMapper.selectRecentTasks(5));
        return stats;
    }
}
