package com.ochodek.server.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ochodek.server.model.inner.*;
import lombok.Data;

import java.util.List;

@Data
public class ActualWeatherAppCity {

    @JsonProperty("coord")
    private Coordinates coordinates;
    private List<Weather> weather;
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
    public static class Sys {
        private String country;
        private Long sunrise;
        private Long sunset;
    }

}
