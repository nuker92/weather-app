package com.ochodek.server.repository;

import com.ochodek.server.entity.City;
import com.ochodek.server.model.CountryCode;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CityRepository extends CrudRepository<City, Long> {

    Optional<City> findByNameAndCountryCode(String name, CountryCode countryCode);


}
