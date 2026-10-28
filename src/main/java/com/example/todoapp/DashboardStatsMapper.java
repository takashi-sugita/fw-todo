package com.example.todoapp;

import java.time.LocalDate;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface DashboardStatsMapper {

    DashboardStats selectTaskSummary();

    List<DailyCompletionStats> selectDailyCompletions(@Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    List<CategoryTaskStats> selectCategoryCounts();

    List<Todo> selectRecentTasks(@Param("limit") int limit);
}
