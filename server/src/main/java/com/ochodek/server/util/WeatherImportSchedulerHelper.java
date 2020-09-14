package com.ochodek.server.util;

import com.ochodek.server.entity.City;
import com.ochodek.server.model.SimpleCityModel;
import com.ochodek.server.service.CityService;
import com.ochodek.server.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WeatherImportSchedulerHelper {

    private final CityService cityService;
    private final List<WeatherService> weatherServices;

    @Autowired
    public WeatherImportSchedulerHelper(CityService cityService, List<WeatherService> weatherServices) {
        this.cityService = cityService;
        this.weatherServices = weatherServices;
    }

    public void updateWeatherForAllCitiesInDatabaseViaAllWeatherProviders() {
        List<City> cities = cityService.findAll();

        weatherServices.parallelStream()
                .forEach(weatherService -> cities.parallelStream()
                        .forEach(city -> weatherService.findActualByCityName(SimpleCityModel.create(city.getName(), city.getCountryCode()))));
    }


}
