package com.example.todoapp.api;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WeatherApiController {

    private final WeatherClient weatherClient;

    public WeatherApiController(WeatherClient weatherClient) {
        this.weatherClient = weatherClient;
    }

    @GetMapping("/api/weather")
    public ResponseEntity<Map<String, String>> getWeather() {
        WeatherClient.WeatherResult result = weatherClient.fetchWeather();
        if (result.unavailable()) {
            return ResponseEntity.ok()
                    .header("X-Weather-Unavailable", "true")
                    .body(result.weather());
        }
        return ResponseEntity.ok(result.weather());
    }
}
