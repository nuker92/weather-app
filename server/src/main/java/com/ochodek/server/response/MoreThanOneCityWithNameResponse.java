package com.ochodek.server.response;

import lombok.Data;

import java.util.List;

@Data
public class MoreThanOneCityWithNameResponse {

    private String message;
    private String cityName;
    private List<String> countryCodes;

    public MoreThanOneCityWithNameResponse() {
        // empty
    }

    public MoreThanOneCityWithNameResponse(String message, String cityName, List<String> countryCodes) {
        this.message = message;
        this.cityName = cityName;
        this.countryCodes = countryCodes;
    }
}
