package com.example.todoapp.api;

import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HolidayApiController {

    private final HolidayClient holidayClient;

    public HolidayApiController(HolidayClient holidayClient) {
        this.holidayClient = holidayClient;
    }

    @GetMapping("/api/holidays")
    public ResponseEntity<Map<String, String>> getHolidays(
            @RequestParam(name = "from", required = false) String from,
            @RequestParam(name = "to", required = false) String to) {
        Map<String, String> holidays = holidayClient.fetchHolidays();

        if (from == null && to == null) {
            return ResponseEntity.ok(holidays);
        }

        LocalDate fromDate = from == null ? null : LocalDate.parse(from);
        LocalDate toDate = to == null ? null : LocalDate.parse(to);
        Map<String, String> filtered = holidays.entrySet().stream()
                .filter(entry -> {
                    LocalDate date = LocalDate.parse(entry.getKey());
                    return (fromDate == null || !date.isBefore(fromDate))
                            && (toDate == null || !date.isAfter(toDate));
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return ResponseEntity.ok(filtered);
    }
}
