package com.example.todoapp.api;

import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class HolidayClient {

    private static final String HOLIDAYS_URL = "https://holidays-jp.github.io/api/v1/date.json";

    public Map<String, String> fetchHolidays() {
        return RestClient.create()
                .get()
                .uri(HOLIDAYS_URL)
                .retrieve()
                .body(Map.class);
    }
}
