package com.example.todoapp.api;

import java.util.Map;
import java.time.Duration;

import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClient;

@Component
public class HolidayClient {

    private static final String HOLIDAYS_URL = "https://holidays-jp.github.io/api/v1/date.json";

    public HolidayResult fetchHolidays() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(3));
        requestFactory.setReadTimeout(Duration.ofSeconds(5));

        try {
            Map<String, String> holidays = RestClient.builder()
                    .requestFactory(requestFactory)
                    .build()
                    .get()
                    .uri(HOLIDAYS_URL)
                    .retrieve()
                    .body(Map.class);
            if (holidays == null) {
                return new HolidayResult(Map.of(), true);
            }
            return new HolidayResult(holidays, false);
        } catch (RestClientException exception) {
            return new HolidayResult(Map.of(), true);
        }
    }

    public record HolidayResult(Map<String, String> holidays, boolean unavailable) {
    }
}
