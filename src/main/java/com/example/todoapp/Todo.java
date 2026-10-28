package com.example.todoapp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.Data;

@Data
public class Todo {

    private Long id;

    @NotBlank(message = "やることを入力してください")
    @Size(max = 255, message = "やることは255文字以内で入力してください")
    private String title;

    @Size(max = 255, message = "メモは255文字以内で入力してください")
    private String detail;

    @NotBlank(message = "ジャンルを選んでください")
    private String category;

    @NotNull(message = "優先度を選んでください")
    private Integer priority;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dueDate;

    public boolean isExpired() {
        return dueDate != null && dueDate.isBefore(LocalDate.now());
    }

    public boolean isDueDateSoon() {
        long daysUntilDueDate = getDaysUntilDueDate();
        return daysUntilDueDate >= 0 && daysUntilDueDate <= 3;
    }

    public long getDaysUntilDueDate() {
        if (dueDate == null) {
            return Long.MAX_VALUE;
        }
        return ChronoUnit.DAYS.between(LocalDate.now(), dueDate);
    }

    private Boolean completed;
    private Boolean pinned;
    private LocalDateTime completedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
}
