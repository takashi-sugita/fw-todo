package com.example.todoapp;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.DayOfWeek;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CalendarController {

    @GetMapping("/calendar")
    public String showCalendar(@RequestParam(name = "year", required = false) Integer year,
            @RequestParam(name = "month", required = false) Integer month,
            @RequestParam(name = "view", required = false, defaultValue = "month") String view,
            @RequestParam(name = "date", required = false) LocalDate date, Model model) {
        boolean weekView = "week".equals(view);
        YearMonth selectedMonth = (year == null || month == null)
                ? YearMonth.now()
                : YearMonth.of(year, month);
        LocalDate firstDay;
        LocalDate lastDay;
        List<LocalDate> dates = new ArrayList<>();
        if (weekView) {
            LocalDate anchor = date == null ? selectedMonth.atDay(1) : date;
            firstDay = anchor.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
            lastDay = firstDay.plusDays(6);
            selectedMonth = YearMonth.from(anchor);
            for (int offset = 0; offset < 7; offset++) {
                dates.add(firstDay.plusDays(offset));
            }
        } else {
            firstDay = selectedMonth.atDay(1);
            lastDay = selectedMonth.atEndOfMonth();
            int leadingDays = firstDay.getDayOfWeek().getValue() % 7;
            int cellCount = ((leadingDays + selectedMonth.lengthOfMonth() + 6) / 7) * 7;
            for (int cell = 0; cell < cellCount; cell++) {
                int day = cell - leadingDays + 1;
                dates.add(day < 1 || day > selectedMonth.lengthOfMonth() ? null : selectedMonth.atDay(day));
            }
        }

        YearMonth previousMonth = selectedMonth.minusMonths(1);
        YearMonth nextMonth = selectedMonth.plusMonths(1);
        model.addAttribute("selectedMonth", selectedMonth);
        model.addAttribute("weekView", weekView);
        model.addAttribute("dates", dates);
        model.addAttribute("firstDay", firstDay);
        model.addAttribute("lastDay", lastDay);
        model.addAttribute("previousMonth", previousMonth);
        model.addAttribute("nextMonth", nextMonth);
        return "calendar";
    }
}
