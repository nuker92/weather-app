package com.ochodek.server.service;

import com.ochodek.server.entity.City;
import com.ochodek.server.entity.Weather;
import com.ochodek.server.exception.OpenWeatherMapException;
import com.ochodek.server.model.ActualWeatherAppCity;
import com.ochodek.server.model.ForecastAppCity;
import com.ochodek.server.model.SimpleCityModel;
import com.ochodek.server.repository.WeatherRepository;
import com.ochodek.server.util.WeatherObjectsParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class OpenWeatherMapService implements WeatherService {

    private final Logger log = LoggerFactory.getLogger(OpenWeatherMapService.class);

    @Value("${openweathermap.api-key}")
    private String apiKey;

    private RestTemplate restTemplate;
    private WeatherRepository weatherRepository;

    public OpenWeatherMapService() {
        // empty
    }

    @Autowired
    public OpenWeatherMapService(RestTemplate restTemplate, WeatherRepository weatherRepository) {
        this.restTemplate = restTemplate;
        this.weatherRepository = weatherRepository;
    }

    @Cacheable(cacheNames = "actualWeather")
    public ActualWeatherAppCity findActualByCityName(SimpleCityModel simpleCityModel) {
        String url = String.format("https://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s",
                simpleCityModel.formatValuesToOpenWeatherMapUrl(),
                apiKey
        );
        try {
            log.info(String.format("Requesting openweathermap to get weather in %s", simpleCityModel.formatValuesToMessage()));
            return restTemplate.getForObject(
                    url,
                    ActualWeatherAppCity.class);
        } catch (HttpStatusCodeException e) {
            log.error("Error while getting data for city " + simpleCityModel.formatValuesToMessage(), e);
            throw new OpenWeatherMapException(simpleCityModel.formatValuesToMessage(), e.getStatusCode());
        }
    }

    @Cacheable(cacheNames = "forecast")
    public ForecastAppCity findForecastByCityName(SimpleCityModel simpleCityModel) {
        String url = String.format("https://api.openweathermap.org/data/2.5/forecast?q=%s&appid=%s",
                simpleCityModel.formatValuesToOpenWeatherMapUrl(),
                apiKey
                );
        try {
            log.info(String.format("Requesting openweathermap to get forecast for %s", simpleCityModel.formatValuesToMessage()));
            return restTemplate.getForObject(
                    url,
                    ForecastAppCity.class);
        } catch (HttpStatusCodeException e) {
            throw new OpenWeatherMapException(simpleCityModel.formatValuesToMessage(), e.getStatusCode());
        }

    }

    public List<Weather> findHistoryForCity(City city) {
        log.info(String.format("Requesting database to get weatherHistory for %s, %s", city.getName(), city.getCountryCode().name()));
        return weatherRepository.findAllByCityOrderByWeatherTimeDesc(city);
    }

    @Override
    public void saveWeatherToDatabase(ActualWeatherAppCity actualWeatherAppCity, City city) {
        List<Weather> weathers = WeatherObjectsParser.parseActualWeatherCityObjectToWeatherList(actualWeatherAppCity, city);
        weatherRepository.saveAll(weathers);

    }
}
