package com.ochodek.server.repository;

import com.ochodek.server.entity.City;
import com.ochodek.server.entity.Weather;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WeatherRepository extends JpaRepository<Weather, Long> {

    List<Weather> findAllByCityOrderByWeatherTimeDesc(City city);


}
