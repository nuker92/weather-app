package com.ochodek.server.rest;

import com.ochodek.server.model.WeatherAppCity;
import com.ochodek.server.service.OpenWeatherMapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/city")
public class CityController {

    private OpenWeatherMapService openWeatherMapService;

    public CityController() {
        // empty
    }

    @Autowired
    public CityController(OpenWeatherMapService openWeatherMapService) {
        this.openWeatherMapService = openWeatherMapService;
    }

    @GetMapping("/{cityName}")
    public WeatherAppCity findWeather(@PathVariable String cityName) {
        return openWeatherMapService.findByCityAndSaveToDatabase(cityName);
    }



}
