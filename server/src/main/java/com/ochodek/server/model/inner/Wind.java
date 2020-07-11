package com.ochodek.server.model.inner;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Wind {

    private Double speed;
    @JsonProperty("deg")
    private Double degree;
    private Double gust;
}
