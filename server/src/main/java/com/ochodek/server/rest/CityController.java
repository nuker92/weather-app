package com.ochodek.server.rest;

import com.ochodek.server.model.ActualWeatherAppCity;
import com.ochodek.server.model.ForecastAppCity;
import com.ochodek.server.model.WeatherDao;
import com.ochodek.server.service.OpenWeatherMapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CityController {

    private final OpenWeatherMapService openWeatherMapService;

    @Autowired
    public CityController(OpenWeatherMapService openWeatherMapService) {
        this.openWeatherMapService = openWeatherMapService;
    }

    @GetMapping
    public String homePage() {
        return "There will be main page";
    }

    @GetMapping("/{cityName}")
    public ActualWeatherAppCity findWeatherByCity(@PathVariable String cityName) {
        return openWeatherMapService.findActualByCityName(cityName, null);
    }

    @GetMapping("/{cityName},{countryCode}")
    public ActualWeatherAppCity findWeatherByCityAndCountryCode(@PathVariable String cityName, @PathVariable String countryCode) {
        return openWeatherMapService.findActualByCityName(cityName, countryCode);
    }

    @GetMapping("/forecast/{cityName}")
    public ForecastAppCity findForecastByCity(@PathVariable String cityName) {
        return openWeatherMapService.findForecastByCityName(cityName, null);
    }

    @GetMapping("/forecast/{cityName},{countryCode}")
    public ForecastAppCity findForecastByCityAndCountryCode(@PathVariable String cityName, @PathVariable String countryCode) {
        return openWeatherMapService.findForecastByCityName(cityName, countryCode);
    }

    @GetMapping("/history/{cityName}")
    public List<WeatherDao> findWeatherHistoryByCity(@PathVariable String cityName) {
        return openWeatherMapService.findHistoryByCityName(cityName);
    }

    @GetMapping("/history/{cityName},{countryCode}")
    public List<WeatherDao> findWeatherHistoryByCityAndCountryCode(@PathVariable String cityName, @PathVariable String countryCode) {
        return openWeatherMapService.findHistoryByCityNameAndCountryCode(cityName, countryCode);
    }



}
