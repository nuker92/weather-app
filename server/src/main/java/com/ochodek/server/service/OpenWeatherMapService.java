package com.ochodek.server.service;

import com.ochodek.server.StringUtils;
import com.ochodek.server.entity.AnotherCityName;
import com.ochodek.server.entity.City;
import com.ochodek.server.exception.MoreThanOneCityWithNameException;
import com.ochodek.server.exception.NoCityHistoryInDatabaseException;
import com.ochodek.server.exception.OpenWeatherMapException;
import com.ochodek.server.model.ActualWeatherAppCity;
import com.ochodek.server.model.CountryCode;
import com.ochodek.server.model.ForecastAppCity;
import com.ochodek.server.model.WeatherDao;
import com.ochodek.server.repository.AnotherCityRepository;
import com.ochodek.server.repository.CityRepository;
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
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OpenWeatherMapService {

    private final Logger log = LoggerFactory.getLogger(OpenWeatherMapService.class);

    @Value("${openweathermap.api-key}")
    private String apiKey;

    private CityRepository cityRepository;
    private RestTemplate restTemplate;
    private WeatherRepository weatherRepository;
    private AnotherCityRepository anotherCityRepository;

    public OpenWeatherMapService() {
        // empty
    }

    @Autowired
    public OpenWeatherMapService(CityRepository cityRepository, RestTemplate restTemplate, WeatherRepository weatherRepository, AnotherCityRepository anotherCityRepository) {
        this.cityRepository = cityRepository;
        this.restTemplate = restTemplate;
        this.weatherRepository = weatherRepository;
        this.anotherCityRepository = anotherCityRepository;
    }

    @Cacheable(cacheNames = "actualWeather")
    public ActualWeatherAppCity findActualByCityName(String cityName, String countryCode) {
        String url;
        String cityNameSimplified = StringUtils.filterDiacriticChars(cityName);
        if (countryCode == null) {
            url = String.format("https://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s", cityNameSimplified, apiKey);
        } else {
            url = String.format("https://api.openweathermap.org/data/2.5/weather?q=%s,%s&appid=%s", cityNameSimplified, countryCode, apiKey);
        }

        try {
            log.info(String.format("Requesting openweathermap to get weather in %s", cityName));
            ActualWeatherAppCity actualWeatherAppCity = restTemplate.getForObject(
                    url,
                    ActualWeatherAppCity.class);
            if (actualWeatherAppCity != null) {
                City city = saveCityIfNotExists(cityName, actualWeatherAppCity);
                weatherRepository.saveAll(WeatherObjectsParser.parseActualWeatherCityObjectToWeatherList(actualWeatherAppCity, city));
            }
            return actualWeatherAppCity;
        } catch (HttpStatusCodeException e) {
            log.error("Error while getting data for city " + cityName, e);
            throw new OpenWeatherMapException(cityName, e.getStatusCode());
        }
    }

    @Cacheable(cacheNames = "forecast")
    public ForecastAppCity findForecastByCityName(String cityName, String countryCode) {
        String url;
        String cityNameSimplified = StringUtils.filterDiacriticChars(cityName);
        if (countryCode == null) {
            url = String.format("https://api.openweathermap.org/data/2.5/forecast?q=%s&appid=%s", cityNameSimplified, apiKey);
        } else {
            url = String.format("https://api.openweathermap.org/data/2.5/forecast?q=%s,%s&appid=%s", cityNameSimplified, countryCode, apiKey);
        }

        try {
            log.info(String.format("Requesting openweathermap to get forecast for %s", cityName));
            return restTemplate.getForObject(
                    url,
                    ForecastAppCity.class);
        } catch (HttpStatusCodeException e) {
            throw new OpenWeatherMapException(cityName, e.getStatusCode());
        }

    }

    private City saveCityIfNotExists(String cityName, ActualWeatherAppCity actualWeatherAppCity) {
        Optional<City> cityInDatabase =
                findCityInDatabase(
                        cityName.toLowerCase(),
                        CountryCode.valueOf(actualWeatherAppCity.getSys().getCountry()),
                        actualWeatherAppCity.getCoordinates().getLongitude(),
                        actualWeatherAppCity.getCoordinates().getLatitude()
                );

        if (cityInDatabase.isEmpty()) {
            City city = new City();
            city.setName(actualWeatherAppCity.getName().toLowerCase());
            city.setCountryCode(CountryCode.valueOf(actualWeatherAppCity.getSys().getCountry()));
            city.setLongitude(actualWeatherAppCity.getCoordinates().getLongitude());
            city.setLatitude(actualWeatherAppCity.getCoordinates().getLatitude());
            cityRepository.save(city);
            log.info(String.format("Successfully added City %s,%s", city.getName(), city.getCountryCode()));
            return city;
        }
        return cityInDatabase.get();
    }

    public Optional<City> findCityInDatabase(String cityName, CountryCode countryCode, Double longitude, Double latitude) {
        return cityRepository.findByCountryCodeAndNameOrAnotherCityName(countryCode, cityName)
                .or(() -> findIfCityExistsByCoordinates(cityName, longitude, latitude));
    }

    private Optional<City> findIfCityExistsByCoordinates(String cityName, Double longitude, Double latitude) {
        Optional<City> city = cityRepository.findByLongitudeAndLatitude(longitude, latitude);
        if (city.isPresent()) {
            AnotherCityName anotherCityName = new AnotherCityName();
            anotherCityName.setAnotherName(cityName);
            anotherCityName.setCity(city.get());
            anotherCityRepository.save(anotherCityName);
        }
        return city;
    }


    public void updateWeatherForAllCitiesInDatabase() {
        List<City> cities = cityRepository.findAll();
        cities.parallelStream()
                .forEach(city -> findActualByCityName(city.getName(), city.getCountryCode().name()));

    }
    
    public List<WeatherDao> findHistoryByCityName(String cityName) {
        List<City> cities = cityRepository.findByNameOrAnotherCityName(cityName.toLowerCase());
        if (cities.size() == 0) {
            throw new NoCityHistoryInDatabaseException(cityName);
        } else if (cities.size() != 1) {
            throw new MoreThanOneCityWithNameException(cityName, cities.stream()
                    .map(c -> c.getCountryCode().name())
                    .collect(Collectors.toList()));
        }
        return findHistoryByCity(cityName, cities.get(0));
    }

    public List<WeatherDao> findHistoryByCityNameAndCountryCode(String cityName, String countryCode) {
        Optional<City> city = cityRepository.findByCountryCodeAndNameOrAnotherCityName(CountryCode.valueOf(countryCode), cityName.toLowerCase());
        if (city.isPresent()) {
            return findHistoryByCity(cityName, city.get());
        }
        throw new NoCityHistoryInDatabaseException(cityName);
    }

    public List<WeatherDao> findHistoryByCity(String cityName, City city) {
        log.info(String.format("Requesting database to get weatherHistory for %s, %s", city.getName(), city.getCountryCode().name()));
        return weatherRepository.findAllByCityOrderByWeatherTimeDesc(city).stream()
                .map(weather -> WeatherObjectsParser.parseWeatherToWeatherDao(cityName, weather))
                .collect(Collectors.toList());
    }


}
