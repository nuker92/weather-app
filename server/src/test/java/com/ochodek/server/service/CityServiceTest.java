package com.ochodek.server.service;

import com.ochodek.server.entity.City;
import com.ochodek.server.exception.MoreThanOneCityWithNameException;
import com.ochodek.server.exception.NoCityHistoryInDatabaseException;
import com.ochodek.server.model.CountryCode;
import com.ochodek.server.model.SimpleCityModel;
import com.ochodek.server.repository.AnotherCityRepository;
import com.ochodek.server.repository.CityRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CityServiceTest {

    @Mock
    private CityRepository cityRepository;

    @Mock
    private AnotherCityRepository anotherCityRepository;

    @InjectMocks
    private CityService cityService;

    private static final String EXISTING_CITY = "london";
    private static final String EXISTING_CITY_ANOTHER_NAME = "londyn";
    private static final String NOT_EXISTING_CITY = "asdads";
    private static final CountryCode EXISTING_COUNTRY_CODE_1 = CountryCode.valueOf("GB");
    private static final CountryCode EXISTING_COUNTRY_CODE_2 = CountryCode.valueOf("PL");

    private static final Double COMMON_LONGITUDE = 0D;
    private static final Double COMMON_LATITUDE = 0D;

    @Test
    @DisplayName("find city when it exists in Database")
    public void findCityInDatabaseTest_case01() {
        // Given
        when(cityRepository.findByCountryCodeAndNameOrAnotherCityName(any(), eq(EXISTING_CITY)))
                .thenReturn(Optional.of(createCity(EXISTING_CITY)));
        // When
        Optional<City> cityInDatabase = cityService.findCityInDatabase(SimpleCityModel.create(EXISTING_CITY, EXISTING_COUNTRY_CODE_1), COMMON_LONGITUDE, COMMON_LATITUDE);
        // Then
        assertEquals(EXISTING_CITY, cityInDatabase.get().getName());
        verify(cityRepository).findByCountryCodeAndNameOrAnotherCityName(any(), eq(EXISTING_CITY));
        verify(cityRepository, times(0)).findByLongitudeAndLatitude(eq(COMMON_LONGITUDE), eq(COMMON_LATITUDE));

    }

    @Test
    @DisplayName("find city when it exists in Database but in another name which don't exists in Database")
    public void findCityInDatabaseTest_case02() {
        // Given
        when(cityRepository.findByCountryCodeAndNameOrAnotherCityName(any(), eq(EXISTING_CITY_ANOTHER_NAME)))
                .thenReturn(Optional.empty());
        when(cityRepository.findByLongitudeAndLatitude(eq(COMMON_LONGITUDE), eq(COMMON_LATITUDE)))
                .thenReturn(Optional.of(createCity(EXISTING_CITY)));
        // When
        Optional<City> cityInDatabase = cityService.findCityInDatabase(SimpleCityModel.create(EXISTING_CITY_ANOTHER_NAME, EXISTING_COUNTRY_CODE_1), COMMON_LONGITUDE, COMMON_LATITUDE);
        // Then
        assertEquals(EXISTING_CITY, cityInDatabase.get().getName());
        verify(cityRepository).findByCountryCodeAndNameOrAnotherCityName(eq(EXISTING_COUNTRY_CODE_1), eq(EXISTING_CITY_ANOTHER_NAME));
        verify(cityRepository).findByLongitudeAndLatitude(eq(COMMON_LONGITUDE), eq(COMMON_LATITUDE));
        verify(anotherCityRepository).save(any());

    }

    public City createCity(String cityName) {
        City c = new City();
        c.setName(cityName);
        return c;
    }

    @Test
    @DisplayName("find when only cityName is provided, no cities found")
    public void findByCityName_case02() {
        // Given
        when(cityRepository.findByNameOrAnotherCityName(any()))
                .thenReturn(Collections.emptyList());
        // When
        NoCityHistoryInDatabaseException noCityHistoryInDatabaseException =
                assertThrows(NoCityHistoryInDatabaseException.class,
                        () -> cityService.findBySimpleCityModel(SimpleCityModel.create(NOT_EXISTING_CITY)));
        // Then
        assertEquals(NOT_EXISTING_CITY, noCityHistoryInDatabaseException.getMessage());
    }

    @Test
    @DisplayName("find when only cityName is provided, more than one citiy found")
    public void findByCityName_case03() {
        // Given
        when(cityRepository.findByNameOrAnotherCityName(any()))
                .thenReturn(createCities());
        // When
        MoreThanOneCityWithNameException moreThanOneCityWithNameException =
                assertThrows(MoreThanOneCityWithNameException.class,
                        () -> cityService.findBySimpleCityModel(SimpleCityModel.create(NOT_EXISTING_CITY)));
        // Then
        assertEquals(NOT_EXISTING_CITY, moreThanOneCityWithNameException.getCityName());
        assertEquals(List.of(EXISTING_COUNTRY_CODE_1.name(), EXISTING_COUNTRY_CODE_2.name()), moreThanOneCityWithNameException.getCountryCodes());
    }
    private List<City> createCities() {
        List<City> cities = new ArrayList<>();
        cities.add(createCity(EXISTING_CITY, EXISTING_COUNTRY_CODE_1));
        cities.add(createCity(EXISTING_CITY, EXISTING_COUNTRY_CODE_2));
        return cities;
    }

    private City createCity(String name, CountryCode countryCode) {
        City c = new City();
        c.setName(name);
        c.setCountryCode(countryCode);
        return c;
    }



    @Test
    @DisplayName("find when only cityName is provided, one city found")
    public void findByCityName_case04() {
        // Given
        when(cityRepository.findByNameOrAnotherCityName(any()))
                .thenReturn(Collections.singletonList(createCity(EXISTING_CITY)));
        // When
        City city = cityService.findBySimpleCityModel(SimpleCityModel.create(EXISTING_CITY));
        // Then
        assertEquals(EXISTING_CITY, city.getName());
    }

    @Test
    @DisplayName("find when cityName and country code is provided, no city found")
    public void findByCityName_case05() {
        // Given
        when(cityRepository.findByCountryCodeAndNameOrAnotherCityName(any(), any()))
                .thenReturn(Optional.empty());
        // When
        NoCityHistoryInDatabaseException noCityHistoryInDatabaseException =
                assertThrows(NoCityHistoryInDatabaseException.class,
                        () -> cityService.findBySimpleCityModel(SimpleCityModel.create(NOT_EXISTING_CITY, EXISTING_COUNTRY_CODE_1)));
        // Then
        assertEquals(NOT_EXISTING_CITY, noCityHistoryInDatabaseException.getMessage());
    }

    @Test
    @DisplayName("find when cityName and country code is provided, city found")
    public void findByCityName_case06() {
        // Given
        when(cityRepository.findByCountryCodeAndNameOrAnotherCityName(any(), any()))
                .thenReturn(Optional.of(createCity(EXISTING_CITY, EXISTING_COUNTRY_CODE_1)));
        // When
        City city = cityService.findBySimpleCityModel(SimpleCityModel.create(EXISTING_CITY, EXISTING_COUNTRY_CODE_1));
        // Then
        assertEquals(EXISTING_CITY, city.getName());
    }

}