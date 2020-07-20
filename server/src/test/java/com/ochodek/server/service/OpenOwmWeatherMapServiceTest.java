package com.ochodek.server.service;

import com.ochodek.server.exception.OpenWeatherMapException;
import com.ochodek.server.model.ActualWeatherAppCity;
import com.ochodek.server.model.CountryCode;
import com.ochodek.server.model.SimpleCityModel;
import com.ochodek.server.model.inner.Coordinates;
import com.ochodek.server.repository.WeatherRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OpenOwmWeatherMapServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private WeatherRepository weatherRepository;

    @InjectMocks
    OpenWeatherMapService openWeatherMapService;

    private static final String EXISTING_CITY = "london";
    private static final String EXISTING_CITY_ANOTHER_NAME = "londyn";
    private static final CountryCode EXISTING_COUNTRY_CODE = CountryCode.valueOf("GB");
    private static final String NOT_EXISTING_CITY = "asdads";

    private static final Double COMMON_LONGITUDE = 0D;
    private static final Double COMMON_LATITUDE = 0D;

    @Test
    @DisplayName("Weather downloading ok")
    public void findActualByCityName_case1() {
        // Given
        ActualWeatherAppCity createdWeather = createWeatherAppCity(EXISTING_CITY, CountryCode.GB);

        when(restTemplate.getForObject(anyString(), eq(ActualWeatherAppCity.class)))
                .thenReturn(createdWeather);
        // When
        ActualWeatherAppCity weatherReturnedByApp = openWeatherMapService.findActualByCityName(SimpleCityModel.create(EXISTING_CITY, EXISTING_COUNTRY_CODE));

        // Then
        assertEquals(createdWeather.getName(), weatherReturnedByApp.getName());
        verify(restTemplate).getForObject(anyString(), eq(ActualWeatherAppCity.class));
    }

    @Test
    @DisplayName("throws error 401")
    public void findActualByCityName_case2() {
        // Given
        when(restTemplate.getForObject(anyString(), eq(ActualWeatherAppCity.class)))
                .thenThrow(createHttpStatusCodeException(HttpStatus.UNAUTHORIZED));
        // When
        OpenWeatherMapException openWeatherMapException =
                assertThrows(
                        OpenWeatherMapException.class,
                        () -> openWeatherMapService.findActualByCityName(SimpleCityModel.create(EXISTING_CITY, EXISTING_COUNTRY_CODE)));

        // Then
        assertEquals(HttpStatus.UNAUTHORIZED, openWeatherMapException.getErrorCode());
        verify(restTemplate).getForObject(anyString(), eq(ActualWeatherAppCity.class));
    }

    @Test
    @DisplayName("throws error 404")
    public void findActualByCityName_case3() {
        // Given
        when(restTemplate.getForObject(anyString(), eq(ActualWeatherAppCity.class)))
                .thenThrow(createHttpStatusCodeException(HttpStatus.NOT_FOUND));
        // When
        OpenWeatherMapException openWeatherMapException =
                assertThrows(
                        OpenWeatherMapException.class,
                        () -> openWeatherMapService.findActualByCityName(SimpleCityModel.create(NOT_EXISTING_CITY, EXISTING_COUNTRY_CODE)));

        // Then
        assertEquals(HttpStatus.NOT_FOUND, openWeatherMapException.getErrorCode());
        verify(restTemplate).getForObject(anyString(), eq(ActualWeatherAppCity.class));
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
                        () -> openWeatherMapService.findActualByCityName(SimpleCityModel.create(NOT_EXISTING_CITY, EXISTING_COUNTRY_CODE)));

        // Then
        assertEquals(HttpStatus.TOO_MANY_REQUESTS, openWeatherMapException.getErrorCode());
        verify(restTemplate).getForObject(anyString(), eq(ActualWeatherAppCity.class));
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