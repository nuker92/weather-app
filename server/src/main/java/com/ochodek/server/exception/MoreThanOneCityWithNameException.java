package com.ochodek.server.exception;

import lombok.Getter;

import java.util.List;

public class MoreThanOneCityWithNameException extends RuntimeException {

    @Getter
    private String cityName;
    @Getter
    private List<String> countryCodes;

    public MoreThanOneCityWithNameException(String cityName, List<String> countryCodes) {
        this.cityName = cityName;
        this.countryCodes = countryCodes;
    }
}
