package com.ochodek.server.model.inner;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MainWeatherInfo {

    @JsonProperty("temp")
    private Double temperature;
    @JsonProperty("feels_like")
    private Double feelsLikeTemperature;
    private Double pressure;
    @JsonProperty("sea_level")
    private Double pressureSeaLevel;
    @JsonProperty("ground_level")
    private Double pressureGroundLevel;
    private Double humidity;
    @JsonProperty("temp_min")
    private Double tempMin;
    @JsonProperty("temp_max")
    private Double tempMax;
}
