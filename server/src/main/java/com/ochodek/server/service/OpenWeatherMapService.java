package com.ochodek.server.service;

import com.ochodek.server.entity.City;
import com.ochodek.server.exception.OpenWeatherMapException;
import com.ochodek.server.model.CountryCode;
import com.ochodek.server.model.ActualWeatherAppCity;
import com.ochodek.server.model.ForecastAppCity;
import com.ochodek.server.repository.CityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@Service
public class OpenWeatherMapService {

    private final Logger log = LoggerFactory.getLogger(OpenWeatherMapService.class);

    @Value("${openweathermap.api-key}")
    private String apiKey;

    private CityRepository cityRepository;
    private RestTemplate restTemplate;

    public OpenWeatherMapService() {
        // empty
    }

    @Autowired
    public OpenWeatherMapService(CityRepository cityRepository, RestTemplate restTemplate) {
        this.cityRepository = cityRepository;
        this.restTemplate = restTemplate;
    }

    @Cacheable(cacheNames = "actualWeather")
    public ActualWeatherAppCity findActualByCityAndSaveToDatabase(String cityName) {
        String url = String.format("https://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s", cityName, apiKey);

        try {
            log.info("Requesting openweathermap to get wheather in " + cityName);
            ActualWeatherAppCity actualWeatherAppCity = restTemplate.getForObject(
                    url,
                    ActualWeatherAppCity.class);
            saveCityIfNotExists(actualWeatherAppCity);
            return actualWeatherAppCity;
        } catch (HttpStatusCodeException e) {
            throw new OpenWeatherMapException(cityName, e.getStatusCode());
        }
    }

    @Cacheable(cacheNames = "forecast")
    public ForecastAppCity findForecastByCity(String cityName) {
        String url = String.format("https://api.openweathermap.org/data/2.5/forecast?q=%s&appid=%s", cityName, apiKey);

        try {
            log.info("Requesting openweathermap to get forecast for " + cityName);
            return restTemplate.getForObject(
                    url,
                    ForecastAppCity.class);
        } catch (HttpStatusCodeException e) {
            throw new OpenWeatherMapException(cityName, e.getStatusCode());
        }

    }

    private void saveCityIfNotExists(ActualWeatherAppCity actualWeatherAppCity) {
        if (actualWeatherAppCity != null &&
                cityRepository.findByNameAndCountryCode(actualWeatherAppCity.getName(), CountryCode.valueOf(actualWeatherAppCity.getSys().getCountry()))
                        .isEmpty()) {
            City city = new City();
            city.setName(actualWeatherAppCity.getName());
            city.setCountryCode(CountryCode.valueOf(actualWeatherAppCity.getSys().getCountry()));
            city.setLongitude(actualWeatherAppCity.getCoordinates().getLongitude());
            city.setLatitude(actualWeatherAppCity.getCoordinates().getLatitude());
            cityRepository.save(city);
            log.info(String.format("Successfully added City %s,%s", city.getName(), city.getCountryCode()));
        }
    }



}
