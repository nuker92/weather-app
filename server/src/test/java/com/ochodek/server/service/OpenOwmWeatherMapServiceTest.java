package com.ochodek.server.service;

import com.ochodek.server.entity.City;
import com.ochodek.server.exception.OpenWeatherMapException;
import com.ochodek.server.model.CountryCode;
import com.ochodek.server.model.ActualWeatherAppCity;
import com.ochodek.server.model.inner.Coordinates;
import com.ochodek.server.repository.CityRepository;
import com.ochodek.server.repository.WeatherRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OpenOwmWeatherMapServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private CityRepository cityRepository;

    @Mock
    private WeatherRepository weatherRepository;

    @InjectMocks
    @Spy
    OpenWeatherMapService openWeatherMapService;

    private static final String EXISTING_CITY = "London";
    private static final String EXISTING_COUNTRY_CODE = "GB";
    private static final String NOT_EXISTING_CITY = "asdads";

    private static final Double COMMON_LONGITUDE = 0D;
    private static final Double COMMON_LATITUDE = 0D;

    @Test
    @DisplayName("Everything is all right, and city doesn't exists")
    public void findByCityAndSaveToDatabase_case1() {
        ActualWeatherAppCity createdWeather = createWeatherAppCity(EXISTING_CITY, CountryCode.GB);

        when(restTemplate.getForObject(anyString(), eq(ActualWeatherAppCity.class)))
                .thenReturn(createdWeather);
        when(cityRepository.findByNameAndCountryCode(anyString(), any(CountryCode.class)))
                .thenReturn(Optional.empty());
        ActualWeatherAppCity weatherReturnedByApp = openWeatherMapService.findActualByCityName(EXISTING_CITY, EXISTING_COUNTRY_CODE);

        assertEquals(createdWeather.getName(), weatherReturnedByApp.getName());
        verify(cityRepository).findByNameAndCountryCode(anyString(), any(CountryCode.class));
        verify(cityRepository).save(any(City.class));
    }

    @Test
    @DisplayName("Everything is all right, and city exists")
    public void findByCityAndSaveToDatabase_case2() {
        ActualWeatherAppCity createdWeather = createWeatherAppCity(EXISTING_CITY, CountryCode.GB);

        when(restTemplate.getForObject(anyString(), eq(ActualWeatherAppCity.class)))
                .thenReturn(createdWeather);
        when(cityRepository.findByNameAndCountryCode(anyString(), any(CountryCode.class)))
                .thenReturn(Optional.of(new City()));
        ActualWeatherAppCity weatherReturnedByApp = openWeatherMapService.findActualByCityName(EXISTING_CITY, EXISTING_COUNTRY_CODE);

        assertEquals(createdWeather.getName(), weatherReturnedByApp.getName());
        verify(cityRepository).findByNameAndCountryCode(anyString(), any(CountryCode.class));
        verify(cityRepository, times(0)).save(any(City.class));
    }

    @Test
    @DisplayName("throws error 401")
    public void findByCityAndSaveToDatabase_case3() {
        when(restTemplate.getForObject(anyString(), eq(ActualWeatherAppCity.class)))
                .thenThrow(createHttpStatusCodeException(HttpStatus.UNAUTHORIZED));

        OpenWeatherMapException openWeatherMapException =
                assertThrows(
                        OpenWeatherMapException.class,
                        () -> openWeatherMapService.findActualByCityName(EXISTING_CITY, EXISTING_COUNTRY_CODE));

        assertEquals(HttpStatus.UNAUTHORIZED, openWeatherMapException.getErrorCode());

        verify(cityRepository, times(0)).findByNameAndCountryCode(anyString(), any(CountryCode.class));
        verify(cityRepository, times(0)).save(any(City.class));
    }

    @Test
    @DisplayName("throws error 404")
    public void findByCityAndSaveToDatabase_case4() {
        when(restTemplate.getForObject(anyString(), eq(ActualWeatherAppCity.class)))
                .thenThrow(createHttpStatusCodeException(HttpStatus.NOT_FOUND));

        OpenWeatherMapException openWeatherMapException =
                assertThrows(
                        OpenWeatherMapException.class,
                        () -> openWeatherMapService.findActualByCityName(NOT_EXISTING_CITY, EXISTING_COUNTRY_CODE));

        assertEquals(HttpStatus.NOT_FOUND, openWeatherMapException.getErrorCode());

        verify(cityRepository, times(0)).findByNameAndCountryCode(anyString(), any(CountryCode.class));
        verify(cityRepository, times(0)).save(any(City.class));
    }

    @Test
    @DisplayName("throws error 429")
    public void findByCityAndSaveToDatabase_case5() {
        when(restTemplate.getForObject(anyString(), eq(ActualWeatherAppCity.class)))
                .thenThrow(createHttpStatusCodeException(HttpStatus.TOO_MANY_REQUESTS));

        OpenWeatherMapException openWeatherMapException =
                assertThrows(
                        OpenWeatherMapException.class,
                        () -> openWeatherMapService.findActualByCityName(NOT_EXISTING_CITY, EXISTING_COUNTRY_CODE));

        assertEquals(HttpStatus.TOO_MANY_REQUESTS, openWeatherMapException.getErrorCode());

        verify(cityRepository, times(0)).findByNameAndCountryCode(anyString(), any(CountryCode.class));
        verify(cityRepository, times(0)).save(any(City.class));
    }

    private ActualWeatherAppCity createWeatherAppCity(String cityName, CountryCode countryCode) {
        ActualWeatherAppCity city = new ActualWeatherAppCity();
        city.setName(cityName);
        city.setSys(createSys(countryCode));
        city.setCoordinates(createCoordinates(COMMON_LONGITUDE, COMMON_LATITUDE));
        city.setOwmWeather(Collections.emptyList());
        return city;
    }

    private ActualWeatherAppCity.Sys createSys(CountryCode countryCode) {
        ActualWeatherAppCity.Sys sys = new ActualWeatherAppCity.Sys();
        sys.setCountry(countryCode.name());
        return sys;
    }

    private HttpStatusCodeException createHttpStatusCodeException(HttpStatus status) {
        return new HttpStatusCodeException(status) {
            @Override
            public HttpStatus getStatusCode() {
                return super.getStatusCode();
            }
        };
    }

    private Coordinates createCoordinates(Double longitude, Double latitude) {
        Coordinates coordinates = new Coordinates();
        coordinates.setLongitude(longitude);
        coordinates.setLatitude(latitude);
        return coordinates;

    }



}