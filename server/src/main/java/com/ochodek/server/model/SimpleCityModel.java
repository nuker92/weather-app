package com.ochodek.server.model;

import com.ochodek.server.StringUtils;
import com.ochodek.server.valueObjects.CityName;

import java.io.Serializable;

public class SimpleCityModel implements Serializable {

    private final CityName cityName;
    private final CountryCode countryCode;

    private SimpleCityModel(CityName cityName, CountryCode countryCode) {
        this.cityName = cityName;
        this.countryCode = countryCode;
    }

    public static SimpleCityModel create(String cityName) {
        return new SimpleCityModel(CityName.of(cityName), null);
    }

    public static SimpleCityModel create(String cityName, CountryCode countryCode) {
        return new SimpleCityModel(CityName.of(cityName), countryCode);
    }

    public String getFilterCityName() {
        return StringUtils.filterDiacriticChars(cityName.getNameAsString());
    }

    public String formatValuesToOpenWeatherMapUrl() {
        return countryCode != null ?
                getFilterCityName() + "," + countryCode :
                getFilterCityName();
    }

    public String formatValuesToMessage() {
        return countryCode != null ?
                getFilterCityName() + ", " + countryCode :
                getFilterCityName();

    }

    public String getCityNameAsString() {
        return cityName.getNameAsString();
    }

    public String getCountryCodeAsString() {
        return countryCode != null ?
                countryCode.name() :
                null;
    }

    public String getCityNameFormattedForCityTable() {
        return cityName.getNameFormattedForCityTable();
    }

}
