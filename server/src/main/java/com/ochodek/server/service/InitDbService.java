package com.ochodek.server.service;

import com.ochodek.server.entity.City;
import com.ochodek.server.model.CountryCode;
import com.ochodek.server.repository.CityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class InitDbService {

    private CityRepository cityRepository;

    @Autowired
    public InitDbService(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    @PostConstruct
    public void createDbInfo() {
        cityRepository.save(createCity("Gliwice", CountryCode.PL, 50.3, 18.68));
        cityRepository.save(createCity("Warsaw", CountryCode.PL, 52.23, 21.01));
        cityRepository.save(createCity("Wrocław", CountryCode.PL, 51.1, 17.03));
        cityRepository.save(createCity("Szczecin", CountryCode.PL, 53.43, 14.55));
        cityRepository.save(createCity("Krakow", CountryCode.PL, 50.08, 19.92));
        cityRepository.save(createCity("Rzeszów", CountryCode.PL, 50.04, 22.0));
        cityRepository.save(createCity("Gdańsk", CountryCode.PL, 54.35, 18.65));
        cityRepository.save(createCity("Zawiercie", CountryCode.PL, 50.49, 19.42));
        cityRepository.save(createCity("Olsztyn", CountryCode.PL, 54.25, 18.7));
        cityRepository.save(createCity("Poznań", CountryCode.PL, 52.4, 16.9));
    }

    private City createCity(String name, CountryCode countryCode, Double latitude, Double longitude) {
        City city = new City();
        city.setName(name);
        city.setCountryCode(countryCode);
        city.setLatitude(latitude);
        city.setLongitude(longitude);
        return city;
    }


}
