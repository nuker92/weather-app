package com.ochodek.server.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WeatherDao {

    private String cityName;
    private LocalDateTime weatherTime;
    private String mainInfo;
    private String description;
    private Long visibility;
    private Double windSpeed;
    private Double windDirection;
    private Double clouds;
    private Double rainOneHour;
    private Double snowOneHour;
    private Double pressure;
    private Double humidity;
    private Double temperature;
    private Double feelsLikeTemperature;

}
