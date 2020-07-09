package com.ochodek.server.service;

import com.ochodek.server.entity.City;
import com.ochodek.server.exception.OpenWeatherMapException;
import com.ochodek.server.model.CountryCode;
import com.ochodek.server.model.WeatherAppCity;
import com.ochodek.server.repository.CityRepository;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OpenWeatherMapServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private CityRepository cityRepository;

    @InjectMocks
    @Spy
    OpenWeatherMapService openWeatherMapService;

    private static final String EXISTING_CITY = "London";
    private static final String NOT_EXISTING_CITY = "asdads";

    @Test
    @DisplayName("Everything is all right, and city doesn't exists")
    public void findByCityAndSaveToDatabase_case1() {
        WeatherAppCity createdWeather = createWeatherAppCity(EXISTING_CITY, CountryCode.GB);

        when(restTemplate.getForObject(anyString(), eq(WeatherAppCity.class)))
                .thenReturn(createdWeather);
        when(cityRepository.findByNameAndCountryCode(anyString(), any(CountryCode.class)))
                .thenReturn(Optional.empty());
        WeatherAppCity weatherReturnedByApp = openWeatherMapService.findByCityAndSaveToDatabase(EXISTING_CITY);

        assertEquals(createdWeather.getName(), weatherReturnedByApp.getName());
        verify(cityRepository).findByNameAndCountryCode(anyString(), any(CountryCode.class));
        verify(cityRepository).save(any(City.class));
    }

    @Test
    @DisplayName("Everything is all right, and city exists")
    public void findByCityAndSaveToDatabase_case2() {
        WeatherAppCity createdWeather = createWeatherAppCity(EXISTING_CITY, CountryCode.GB);

        when(restTemplate.getForObject(anyString(), eq(WeatherAppCity.class)))
                .thenReturn(createdWeather);
        when(cityRepository.findByNameAndCountryCode(anyString(), any(CountryCode.class)))
                .thenReturn(Optional.of(new City()));
        WeatherAppCity weatherReturnedByApp = openWeatherMapService.findByCityAndSaveToDatabase(EXISTING_CITY);

        assertEquals(createdWeather.getName(), weatherReturnedByApp.getName());
        verify(cityRepository).findByNameAndCountryCode(anyString(), any(CountryCode.class));
        verify(cityRepository, times(0)).save(any(City.class));
    }

    @Test
    @DisplayName("throws error 401")
    public void findByCityAndSaveToDatabase_case3() {
        when(restTemplate.getForObject(anyString(), eq(WeatherAppCity.class)))
                .thenThrow(createHttpStatusCodeException(HttpStatus.UNAUTHORIZED));

        OpenWeatherMapException openWeatherMapException =
                assertThrows(
                        OpenWeatherMapException.class,
                        () -> openWeatherMapService.findByCityAndSaveToDatabase(EXISTING_CITY));

        assertEquals(HttpStatus.UNAUTHORIZED, openWeatherMapException.getErrorCode());

        verify(cityRepository, times(0)).findByNameAndCountryCode(anyString(), any(CountryCode.class));
        verify(cityRepository, times(0)).save(any(City.class));
    }

    @Test
    @DisplayName("throws error 404")
    public void findByCityAndSaveToDatabase_case4() {
        when(restTemplate.getForObject(anyString(), eq(WeatherAppCity.class)))
                .thenThrow(createHttpStatusCodeException(HttpStatus.NOT_FOUND));

        OpenWeatherMapException openWeatherMapException =
                assertThrows(
                        OpenWeatherMapException.class,
                        () -> openWeatherMapService.findByCityAndSaveToDatabase(NOT_EXISTING_CITY));

        assertEquals(HttpStatus.NOT_FOUND, openWeatherMapException.getErrorCode());

        verify(cityRepository, times(0)).findByNameAndCountryCode(anyString(), any(CountryCode.class));
        verify(cityRepository, times(0)).save(any(City.class));
    }

    @Test
    @DisplayName("throws error 429")
    public void findByCityAndSaveToDatabase_case5() {
        when(restTemplate.getForObject(anyString(), eq(WeatherAppCity.class)))
                .thenThrow(createHttpStatusCodeException(HttpStatus.TOO_MANY_REQUESTS));

        OpenWeatherMapException openWeatherMapException =
                assertThrows(
                        OpenWeatherMapException.class,
                        () -> openWeatherMapService.findByCityAndSaveToDatabase(NOT_EXISTING_CITY));

        assertEquals(HttpStatus.TOO_MANY_REQUESTS, openWeatherMapException.getErrorCode());

        verify(cityRepository, times(0)).findByNameAndCountryCode(anyString(), any(CountryCode.class));
        verify(cityRepository, times(0)).save(any(City.class));
    }

    private WeatherAppCity createWeatherAppCity(String cityName, CountryCode countryCode) {
        WeatherAppCity city = new WeatherAppCity();
        city.setName(cityName);
        city.setSys(createSys(countryCode));
        city.setCoord(new WeatherAppCity.Coord());
        return city;
    }

    private WeatherAppCity.Sys createSys(CountryCode countryCode) {
        WeatherAppCity.Sys sys = new WeatherAppCity.Sys();
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



}