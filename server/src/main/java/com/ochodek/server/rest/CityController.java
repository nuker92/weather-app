package com.ochodek.server.rest;

import com.ochodek.server.model.WeatherAppCity;
import com.ochodek.server.service.OpenWeatherMapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

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
    public WeatherAppCity findWeather(@PathVariable String cityName) {
        return openWeatherMapService.findByCityAndSaveToDatabase(cityName);
    }



}
