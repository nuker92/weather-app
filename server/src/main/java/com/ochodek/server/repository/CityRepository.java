package com.ochodek.server.repository;

import com.ochodek.server.entity.City;
import com.ochodek.server.model.CountryCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {

    @Query( "SELECT c " +
            "FROM City c " +
            "LEFT JOIN c.anotherCityNames a ON (c.id = a.city.id) " +
            "WHERE (UPPER(c.name) = UPPER(?1) or UPPER(a.anotherName) = UPPER(?1))")
    List<City> findByNameOrAnotherCityName(String name);

    @Query( "SELECT c " +
            "FROM City c " +
            "LEFT JOIN c.anotherCityNames a on (c.id = a.city.id) " +
            "WHERE c.countryCode = ?1 " +
            "AND (UPPER(c.name) = UPPER(?2) or UPPER(a.anotherName) = UPPER(?2))")
    Optional<City> findByCountryCodeAndNameOrAnotherCityName(CountryCode countryCode, String name);

    Optional<City> findByLongitudeAndLatitude(Double longitude, Double latitude);

}
