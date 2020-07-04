package com.ochodek.server.service;

import com.ochodek.server.entity.City;
import com.ochodek.server.exception.OpenWeatherMapException;
import com.ochodek.server.model.CountryCode;
import com.ochodek.server.repository.CityRepository;
import com.ochodek.server.model.WeatherAppCity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@Service
public class OpenWeatherMapService {

    Logger log = LoggerFactory.getLogger(OpenWeatherMapService.class);

    @Value("${openweathermap.api-key}")
    private String apiKey;

    private CityRepository cityRepository;

    public OpenWeatherMapService() {
        // empty
    }

    @Autowired
    public OpenWeatherMapService(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    public WeatherAppCity findByCityAndSaveToDatabase(String cityName) {
        String url = String.format("https://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s", cityName, apiKey);

        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<WeatherAppCity> weatherAppCityResponseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    HttpEntity.EMPTY,
                    WeatherAppCity.class);
            WeatherAppCity weatherAppCity = weatherAppCityResponseEntity.getBody();
            saveCityIfNotExists(weatherAppCity);
            return weatherAppCity;
        } catch (HttpStatusCodeException e) {
            throw new OpenWeatherMapException(cityName, e.getStatusCode());
        }
    }

    private void saveCityIfNotExists(WeatherAppCity weatherAppCity) {
        if (weatherAppCity != null &&
                cityRepository.findByNameAndCountryCode(weatherAppCity.getName(), CountryCode.valueOf(weatherAppCity.getSys().getCountry()))
                        .isEmpty()) {
            City city = new City();
            city.setName(weatherAppCity.getName());
            city.setCountryCode(CountryCode.valueOf(weatherAppCity.getSys().getCountry()));
            city.setLongitude(weatherAppCity.getCoord().getLon());
            city.setLatitude(weatherAppCity.getCoord().getLat());
            cityRepository.save(city);
            log.info(String.format("Successfully added City %s,%s", city.getName(), city.getCountryCode()));
        }
    }



}
