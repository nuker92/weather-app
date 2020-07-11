package com.ochodek.server.model.inner;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class Wind implements Serializable {

    private Double speed;
    @JsonProperty("deg")
    private Double degree;
    private Double gust;
}
