package com.ochodek.server.rest;

import com.ochodek.server.entity.City;
import com.ochodek.server.model.*;
import com.ochodek.server.service.CityService;
import com.ochodek.server.service.OpenWeatherMapService;
import com.ochodek.server.util.WeatherObjectsParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class CityController {

    private final OpenWeatherMapService openWeatherMapService;
    private final CityService cityService;

    @Autowired
    public CityController(OpenWeatherMapService openWeatherMapService, CityService cityService) {
        this.openWeatherMapService = openWeatherMapService;
        this.cityService = cityService;
    }

    @GetMapping
    public String homePage() {
        return "There will be main page";
    }

    @GetMapping("/{cityName}")
    public ActualWeatherAppCity findWeatherByCity(@PathVariable String cityName) {
        ActualWeatherAppCity actualWeatherAppCity =
                openWeatherMapService.findActualByCityName(SimpleCityModel.create(cityName));
        City city = cityService.saveCityIfNotExists(SimpleCityModel.create(cityName), actualWeatherAppCity);
        openWeatherMapService.saveWeatherToDatabase(actualWeatherAppCity, city);
        return actualWeatherAppCity;
    }

    @GetMapping("/{countryCode}/{cityName}")
    public ActualWeatherAppCity findWeatherByCityAndCountryCode(@PathVariable String cityName, @PathVariable String countryCode) {
        ActualWeatherAppCity actualWeatherAppCity =
                openWeatherMapService.findActualByCityName(SimpleCityModel.create(cityName, CountryCode.valueOf(countryCode)));
        City city = cityService.saveCityIfNotExists(SimpleCityModel.create(cityName), actualWeatherAppCity);
        openWeatherMapService.saveWeatherToDatabase(actualWeatherAppCity, city);
        return actualWeatherAppCity;
    }

    @GetMapping("/forecast/{cityName}")
    public ForecastAppCity findForecastByCity(@PathVariable String cityName) {
        return openWeatherMapService.findForecastByCityName(SimpleCityModel.create(cityName));
    }

    @GetMapping("/forecast/{countryCode}/{cityName}")
    public ForecastAppCity findForecastByCityAndCountryCode(@PathVariable String cityName, @PathVariable String countryCode) {
        return openWeatherMapService.findForecastByCityName(SimpleCityModel.create(cityName, CountryCode.valueOf(countryCode)));
    }

    @GetMapping("/history/{cityName}")
    public List<WeatherDao> findWeatherHistoryByCity(@PathVariable String cityName) {
        City city = cityService.findBySimpleCityModel(SimpleCityModel.create(cityName));
        return openWeatherMapService.findHistoryForCity(city).stream()
                .map(weather -> WeatherObjectsParser.parseWeatherToWeatherDao(cityName, weather))
                .collect(Collectors.toList());
    }

    @GetMapping("/history/{countryCode}/{cityName}")
    public List<WeatherDao> findWeatherHistoryByCityAndCountryCode(@PathVariable String cityName, @PathVariable String countryCode) {
        City city = cityService.findBySimpleCityModel(SimpleCityModel.create(cityName, CountryCode.valueOf(countryCode)));
        return openWeatherMapService.findHistoryForCity(city).stream()
                .map(weather -> WeatherObjectsParser.parseWeatherToWeatherDao(cityName, weather))
                .collect(Collectors.toList());
    }

}
