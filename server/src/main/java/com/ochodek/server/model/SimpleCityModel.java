package com.ochodek.server.model;

import com.ochodek.server.StringUtils;
import lombok.Getter;

import java.io.Serializable;

public class SimpleCityModel implements Serializable {

    @Getter
    private final String cityName;
    @Getter
    private final CountryCode countryCode;

    private SimpleCityModel(String cityName, CountryCode countryCode) {
        this.cityName = cityName;
        this.countryCode = countryCode;
    }

    public static SimpleCityModel create(String cityName) {
        return new SimpleCityModel(cityName, null);
    }

    public static SimpleCityModel create(String cityName, CountryCode countryCode) {
        return new SimpleCityModel(cityName, countryCode);
    }

    public String getFilterCityName() {
        return StringUtils.filterDiacriticChars(cityName);
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

}
