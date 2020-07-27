package com.ochodek.server.service;

import com.ochodek.server.StringUtils;
import com.ochodek.server.entity.AnotherCityName;
import com.ochodek.server.entity.City;
import com.ochodek.server.exception.MoreThanOneCityWithNameException;
import com.ochodek.server.exception.NoCityHistoryInDatabaseException;
import com.ochodek.server.model.ActualWeatherAppCity;
import com.ochodek.server.model.CountryCode;
import com.ochodek.server.model.SimpleCityModel;
import com.ochodek.server.repository.AnotherCityRepository;
import com.ochodek.server.repository.CityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CityService {

    private final Logger log = LoggerFactory.getLogger(CityService.class);

    private final CityRepository cityRepository;
    private final AnotherCityRepository anotherCityRepository;

    @Autowired
    public CityService(CityRepository cityRepository, AnotherCityRepository anotherCityRepository) {
        this.cityRepository = cityRepository;
        this.anotherCityRepository = anotherCityRepository;
    }

    public City saveCityIfNotExists(SimpleCityModel simpleCityModel, ActualWeatherAppCity actualWeatherAppCity) {
        Optional<City> cityInDatabase =
                findCityInDatabase(
                        simpleCityModel,
                        actualWeatherAppCity.getCoordinates().getLongitude(),
                        actualWeatherAppCity.getCoordinates().getLatitude()
                );

        if (cityInDatabase.isEmpty()) {
            City city = new City();
            city.setName(StringUtils.filterDiacriticChars(actualWeatherAppCity.getName().toLowerCase()));
            city.setCountryCode(CountryCode.valueOf(actualWeatherAppCity.getSys().getCountry()));
            city.setLongitude(actualWeatherAppCity.getCoordinates().getLongitude());
            city.setLatitude(actualWeatherAppCity.getCoordinates().getLatitude());
            cityRepository.save(city);
            log.info(String.format("Successfully added City %s,%s", city.getName(), city.getCountryCode()));
            return city;
        }
        return cityInDatabase.get();
    }

    public Optional<City> findCityInDatabase(SimpleCityModel simpleCityModel, Double longitude, Double latitude) {
        return cityRepository.findByCountryCodeAndNameOrAnotherCityName(simpleCityModel.getCountryCode(), simpleCityModel.getCityName())
                .or(() -> findIfCityExistsByCoordinates(SimpleCityModel.create(simpleCityModel.getCityName()), longitude, latitude));
    }

    private Optional<City> findIfCityExistsByCoordinates(SimpleCityModel simpleCityModel, Double longitude, Double latitude) {
        Optional<City> city = cityRepository.findByLongitudeAndLatitude(longitude, latitude);
        if (city.isPresent()) {
            AnotherCityName anotherCityName = new AnotherCityName();
            anotherCityName.setAnotherName(simpleCityModel.getCityName());
            anotherCityName.setCity(city.get());
            anotherCityRepository.save(anotherCityName);
        }
        return city;
    }

    public List<City> findAll() {
        return cityRepository.findAll();
    }

    public City findBySimpleCityModel(SimpleCityModel simpleCityModel) {
        return simpleCityModel.getCountryCode() == null ?
                findOnlyByCityName(simpleCityModel) :
                findByCityNameAndCountryCode(simpleCityModel);

    }

    private City findOnlyByCityName(SimpleCityModel simpleCityModel) {
        List<City> cities = cityRepository.findByNameOrAnotherCityName(simpleCityModel.getFilterCityName());
        if (cities.size() == 0) {
            throw new NoCityHistoryInDatabaseException(simpleCityModel.getCityName());
        } else if (cities.size() != 1) {
            throw new MoreThanOneCityWithNameException(simpleCityModel.getCityName(), cities.stream()
                    .map(c -> c.getCountryCode().name())
                    .collect(Collectors.toList()));
        }
        return cities.get(0);
    }

    private City findByCityNameAndCountryCode(SimpleCityModel simpleCityModel) {
        Optional<City> city = cityRepository.findByCountryCodeAndNameOrAnotherCityName(simpleCityModel.getCountryCode(), simpleCityModel.getCityName());
        if (city.isPresent()) {
            return city.get();
        }
        throw new NoCityHistoryInDatabaseException(simpleCityModel.getCityName());
    }
}
