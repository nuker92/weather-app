package com.ochodek.server.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ochodek.server.model.inner.*;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ActualWeatherAppCity implements Serializable {

    @JsonProperty("coord")
    private Coordinates coordinates;
    @JsonProperty("weather")
    private List<OwmWeather> owmWeather;
    @JsonProperty("main")
    private MainWeatherInfo mainWeatherInfo;
    private Long visibility;
    private Wind wind;
    private Clouds clouds;
    private Rain rain;
    private Snow snow;
    @JsonProperty("dt")
    private Long unixTimestamp;
    private Sys sys;
    private Long timezone;
    private Long id;
    private String name;

    @Data
    public static class Sys implements Serializable{
        private String country;
        private Long sunrise;
        private Long sunset;
    }

}
