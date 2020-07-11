package com.ochodek.server.model.inner;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class Coordinates implements Serializable {

    @JsonProperty("lon")
    private Double longitude;
    @JsonProperty("lat")
    private Double latitude;


}
