package com.ochodek.server.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ochodek.server.model.inner.*;
import lombok.Data;

import java.util.List;

@Data
public class ForecastAppCity {

    @JsonProperty("city")
    private OpenWeatherMapCity openWeatherMapCity;
    @JsonProperty("list")
    private List<Object> forecastList;

    @Data
    public static class OpenWeatherMapCity {
        private Long id;
        private String name;
        @JsonProperty("coord")
        private Coordinates coordinates;
        private String country;
        private Long timezone;
    }

    @Data
    public static class Forecast {

        @JsonProperty("dt")
        private Long unixTimestamp;
        @JsonProperty("main")
        private MainWeatherInfo mainWeatherInfo;
        private Weather weather;
        private Clouds clouds;
        private Wind wind;
        private Rain rain;
        private Snow snow;

    }


}
