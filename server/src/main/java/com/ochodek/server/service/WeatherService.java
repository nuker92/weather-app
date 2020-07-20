package com.ochodek.server.service;

import com.ochodek.server.entity.City;
import com.ochodek.server.entity.Weather;
import com.ochodek.server.model.ActualWeatherAppCity;
import com.ochodek.server.model.ForecastAppCity;
import com.ochodek.server.model.SimpleCityModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface WeatherService {

    ActualWeatherAppCity findActualByCityName(SimpleCityModel simpleCityModel);
    ForecastAppCity findForecastByCityName(SimpleCityModel simpleCityModel);
    List<Weather> findHistoryForCity(City city);

    void saveWeatherToDatabase(ActualWeatherAppCity actualWeatherAppCity, City city);
}
