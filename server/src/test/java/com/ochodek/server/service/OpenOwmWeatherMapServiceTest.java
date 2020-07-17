package com.ochodek.server.service;

import com.ochodek.server.entity.City;
import com.ochodek.server.exception.OpenWeatherMapException;
import com.ochodek.server.model.CountryCode;
import com.ochodek.server.model.ActualWeatherAppCity;
import com.ochodek.server.model.inner.Coordinates;
import com.ochodek.server.repository.AnotherCityRepository;
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

    @Mock
    private AnotherCityRepository anotherCityRepository;

    @InjectMocks
    @Spy
    OpenWeatherMapService openWeatherMapService;

    private static final String EXISTING_CITY = "london";
    private static final String EXISTING_CITY_ANOTHER_NAME = "londyn";
    private static final String EXISTING_COUNTRY_CODE = "GB";
    private static final String NOT_EXISTING_CITY = "asdads";

    private static final Double COMMON_LONGITUDE = 0D;
    private static final Double COMMON_LATITUDE = 0D;

    @Test
    @DisplayName("Everything is all right, and city doesn't exists")
    public void findByCityAndSaveToDatabase_case1() {
        // Given
        ActualWeatherAppCity createdWeather = createWeatherAppCity(EXISTING_CITY, CountryCode.GB);

        when(restTemplate.getForObject(anyString(), eq(ActualWeatherAppCity.class)))
                .thenReturn(createdWeather);
        when(cityRepository.findByCountryCodeAndNameOrAnotherCityName(any(CountryCode.class), anyString()))
                .thenReturn(Optional.empty());
        // When
        ActualWeatherAppCity weatherReturnedByApp = openWeatherMapService.findActualByCityName(EXISTING_CITY, EXISTING_COUNTRY_CODE);

        // Then
        assertEquals(createdWeather.getName(), weatherReturnedByApp.getName());
        verify(cityRepository).findByCountryCodeAndNameOrAnotherCityName(any(CountryCode.class), anyString());
        verify(cityRepository).save(any(City.class));
    }

    @Test
    @DisplayName("Everything is all right, and city exists")
    public void findByCityAndSaveToDatabase_case2() {
        // Given
        ActualWeatherAppCity createdWeather = createWeatherAppCity(EXISTING_CITY, CountryCode.GB);

        when(restTemplate.getForObject(anyString(), eq(ActualWeatherAppCity.class)))
                .thenReturn(createdWeather);
        when(cityRepository.findByCountryCodeAndNameOrAnotherCityName(any(CountryCode.class), anyString()))
                .thenReturn(Optional.of(new City()));
        // When
        ActualWeatherAppCity weatherReturnedByApp = openWeatherMapService.findActualByCityName(EXISTING_CITY, EXISTING_COUNTRY_CODE);
        // Then
        assertEquals(createdWeather.getName(), weatherReturnedByApp.getName());
        verify(cityRepository).findByCountryCodeAndNameOrAnotherCityName(any(CountryCode.class), anyString());
        verify(cityRepository, times(0)).save(any(City.class));
    }

    @Test
    @DisplayName("throws error 401")
    public void findByCityAndSaveToDatabase_case3() {
        // Given
        when(restTemplate.getForObject(anyString(), eq(ActualWeatherAppCity.class)))
                .thenThrow(createHttpStatusCodeException(HttpStatus.UNAUTHORIZED));
        // When
        OpenWeatherMapException openWeatherMapException =
                assertThrows(
                        OpenWeatherMapException.class,
                        () -> openWeatherMapService.findActualByCityName(EXISTING_CITY, EXISTING_COUNTRY_CODE));

        // Then
        assertEquals(HttpStatus.UNAUTHORIZED, openWeatherMapException.getErrorCode());
        verify(cityRepository, times(0)).findByCountryCodeAndNameOrAnotherCityName(any(CountryCode.class), anyString());
        verify(cityRepository, times(0)).save(any(City.class));
    }

    @Test
    @DisplayName("throws error 404")
    public void findByCityAndSaveToDatabase_case4() {
        // Given
        when(restTemplate.getForObject(anyString(), eq(ActualWeatherAppCity.class)))
                .thenThrow(createHttpStatusCodeException(HttpStatus.NOT_FOUND));
        // When
        OpenWeatherMapException openWeatherMapException =
                assertThrows(
                        OpenWeatherMapException.class,
                        () -> openWeatherMapService.findActualByCityName(NOT_EXISTING_CITY, EXISTING_COUNTRY_CODE));

        // Then
        assertEquals(HttpStatus.NOT_FOUND, openWeatherMapException.getErrorCode());
        verify(cityRepository, times(0)).findByCountryCodeAndNameOrAnotherCityName(any(CountryCode.class), anyString());
        verify(cityRepository, times(0)).save(any(City.class));
    }

    @Test
    @DisplayName("throws error 429")
    public void findByCityAndSaveToDatabase_case5() {
        // Given
        when(restTemplate.getForObject(anyString(), eq(ActualWeatherAppCity.class)))
                .thenThrow(createHttpStatusCodeException(HttpStatus.TOO_MANY_REQUESTS));
        // When
        OpenWeatherMapException openWeatherMapException =
                assertThrows(
                        OpenWeatherMapException.class,
                        () -> openWeatherMapService.findActualByCityName(NOT_EXISTING_CITY, EXISTING_COUNTRY_CODE));

        // Then
        assertEquals(HttpStatus.TOO_MANY_REQUESTS, openWeatherMapException.getErrorCode());
        verify(cityRepository, times(0)).findByCountryCodeAndNameOrAnotherCityName(any(CountryCode.class), anyString());
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

    @Test
    @DisplayName("find city when it exists in Database")
    public void findCityInDatabaseTest_case06() {
        // Given
        when(cityRepository.findByCountryCodeAndNameOrAnotherCityName(any(), eq(EXISTING_CITY)))
                .thenReturn(Optional.of(createCity(EXISTING_CITY)));
        // When
        Optional<City> cityInDatabase = openWeatherMapService.findCityInDatabase(EXISTING_CITY, CountryCode.valueOf(EXISTING_COUNTRY_CODE), COMMON_LONGITUDE, COMMON_LATITUDE);
        // Then
        assertEquals(EXISTING_CITY, cityInDatabase.get().getName());
        verify(cityRepository).findByCountryCodeAndNameOrAnotherCityName(any(), eq(EXISTING_CITY));
        verify(cityRepository, times(0)).findByLongitudeAndLatitude(eq(COMMON_LONGITUDE), eq(COMMON_LATITUDE));

    }

    @Test
    @DisplayName("find city when it exists in Database but in another name which don't exists in Database")
    public void findCityInDatabaseTest_case07() {
        // Given
        when(cityRepository.findByCountryCodeAndNameOrAnotherCityName(any(), eq(EXISTING_CITY_ANOTHER_NAME)))
                .thenReturn(Optional.empty());
        when(cityRepository.findByLongitudeAndLatitude(eq(COMMON_LONGITUDE), eq(COMMON_LATITUDE)))
                .thenReturn(Optional.of(createCity(EXISTING_CITY)));
        // When
        Optional<City> cityInDatabase = openWeatherMapService.findCityInDatabase(EXISTING_CITY_ANOTHER_NAME, CountryCode.valueOf(EXISTING_COUNTRY_CODE), COMMON_LONGITUDE, COMMON_LATITUDE);
        // Then
        assertEquals(EXISTING_CITY, cityInDatabase.get().getName());
        verify(cityRepository).findByCountryCodeAndNameOrAnotherCityName(eq(CountryCode.valueOf(EXISTING_COUNTRY_CODE)), eq(EXISTING_CITY_ANOTHER_NAME));
        verify(cityRepository).findByLongitudeAndLatitude(eq(COMMON_LONGITUDE), eq(COMMON_LATITUDE));
        verify(anotherCityRepository).save(any());

    }

    public City createCity(String cityName) {
        City c = new City();
        c.setName(cityName);
        return c;
    }



}