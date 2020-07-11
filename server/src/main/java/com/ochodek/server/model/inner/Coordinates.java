package com.ochodek.server.model.inner;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Coordinates {

    @JsonProperty("lon")
    private Double longitude;
    @JsonProperty("lat")
    private Double latitude;


}
