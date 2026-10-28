package com.example.todoapp.api;

import java.time.Duration;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class WeatherClient {

    private static final Logger logger = LoggerFactory.getLogger(WeatherClient.class);
    private static final String WEATHER_URL = "https://api.open-meteo.com/v1/forecast?latitude=35.7138&longitude=139.7772&daily=weather_code,temperature_2m_max,temperature_2m_min&past_days=92&forecast_days=16&timezone=Asia%2FTokyo";

    public WeatherResult fetchWeather() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(3));
        requestFactory.setReadTimeout(Duration.ofSeconds(5));

        try {
            Map<String, Object> response = RestClient.builder()
                    .requestFactory(requestFactory)
                    .build()
                    .get()
                    .uri(URI.create(WEATHER_URL))
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, Object>>() {});
            Object dailyValue = response == null ? null : response.get("daily");
            if (!(dailyValue instanceof Map<?, ?> daily)
                    || !(daily.get("time") instanceof List<?> dates)
                    || !(daily.get("weather_code") instanceof List<?> weatherCodes)
                    || !(daily.get("temperature_2m_max") instanceof List<?> maxTemperatures)
                    || !(daily.get("temperature_2m_min") instanceof List<?> minTemperatures)) {
                logger.warn("Weather API response did not contain the expected daily weather fields");
                return new WeatherResult(Map.of(), true);
            }

            Map<String, String> weatherByDate = new HashMap<>();
            int dayCount = Math.min(Math.min(dates.size(), weatherCodes.size()),
                    Math.min(maxTemperatures.size(), minTemperatures.size()));
            for (int index = 0; index < dayCount; index++) {
                if (dates.get(index) instanceof String date
                        && weatherCodes.get(index) instanceof Number weatherCode
                        && maxTemperatures.get(index) instanceof Number maxTemperature
                        && minTemperatures.get(index) instanceof Number minTemperature) {
                    String label = weatherDescription(weatherCode.intValue()) + " "
                            + Math.round(maxTemperature.doubleValue()) + "°/"
                            + Math.round(minTemperature.doubleValue()) + "°";
                    weatherByDate.put(date, label);
                }
            }
            return new WeatherResult(weatherByDate, weatherByDate.isEmpty());
        } catch (RestClientException exception) {
            logger.warn("Could not retrieve weather from Open-Meteo", exception);
            return new WeatherResult(Map.of(), true);
        } catch (NullPointerException exception) {
            logger.warn("Open-Meteo returned an empty weather response", exception);
            return new WeatherResult(Map.of(), true);
        }
    }

    private String weatherDescription(int code) {
        return switch (code) {
            case 0 -> "快晴";
            case 1 -> "晴れ";
            case 2 -> "一部曇り";
            case 3 -> "曇り";
            case 45, 48 -> "霧";
            case 51, 53, 55, 56, 57 -> "霧雨";
            case 61, 63, 65, 66, 67 -> "雨";
            case 71, 73, 75, 77 -> "雪";
            case 80, 81, 82 -> "にわか雨";
            case 85, 86 -> "にわか雪";
            case 95, 96, 99 -> "雷雨";
            default -> "天気";
        };
    }

    public record WeatherResult(Map<String, String> weather, boolean unavailable) {
    }
}
