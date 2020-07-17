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
            "left join c.anotherCityNames a on (c.id = a.city.id) " +
            "and (c.name = ?1 or a.anotherName = ?1)")
    List<City> findByNameOrAnotherCityName(String name);

    @Query( "SELECT c " +
            "FROM City c " +
            "left join c.anotherCityNames a on (c.id = a.city.id) " +
            "where c.countryCode = ?1 " +
            "and (c.name = ?2 or a.anotherName = ?2)")
    Optional<City> findByCountryCodeAndNameOrAnotherCityName(CountryCode countryCode, String name);

    Optional<City> findByLongitudeAndLatitude(Double longitude, Double latitude);

}
