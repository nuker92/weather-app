package com.ochodek.server.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class WeatherAppCity {

    private Coord coord;
    private List<Weather> weather;
    private String base;
    private Main main;
    private Long visibility;
    private Wind wind;
    private Clouds clouds;
    private Rain rain;
    private Snow snow;
    private Long dt;
    private Sys sys;
    private Long id;
    private String name;
    private Long cod;


    @Data
    public static class Coord {
        private Double lon;
        private Double lat;
    }

    @Data
    public static class Weather {
        private Long id;
        private String main;
        private String description;
        private String icon;
    }

    @Data
    public static class Main {
        private Double temp;
        private Double pressure;
        private Double humidity;
        private Double temp_min;
        private Double temp_max;
    }

    @Data
    public static class Wind {
        private Double speed;
        private Double deg;
    }

    @Data
    public static class Clouds {
        private Double all;
    }

    @Data
    public static class Rain {
        @JsonProperty("1h")
        private Double oneHour;
        @JsonProperty("3h")
        private Double threeHour;
    }

    @Data
    public static class Snow {
        @JsonProperty("1h")
        private Double oneHour;
        @JsonProperty("3h")
        private Double threeHour;
    }

    @Data
    public static class Sys {
        private Long type;
        private Long id;
        private Double message;
        private String country;
        private Long sunrise;
        private Long sunset;
    }

}
