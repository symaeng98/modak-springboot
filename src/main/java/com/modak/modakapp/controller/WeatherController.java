package com.modak.modakapp.controller;

import com.modak.modakapp.utils.weather.WeatherUtil;
import io.sentry.Sentry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/weather")
@Slf4j
public class WeatherController {
    private final WeatherUtil weatherUtil;

    @GetMapping()
    public void getWeather() throws Exception {
        try {
            throw new Exception("this is a test.");
        } catch (Exception e) {
            Sentry.captureException(e);
        }
        weatherUtil.lookUpWeather();
    }
}
