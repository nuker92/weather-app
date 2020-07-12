package com.ochodek.server.repository;

import com.ochodek.server.entity.City;
import com.ochodek.server.model.CountryCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {

    List<City> findByName(String name);

    Optional<City> findByNameAndCountryCode(String name, CountryCode countryCode);

}
