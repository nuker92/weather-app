package com.ochodek.server.model.inner;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class Snow implements Serializable {

    @JsonProperty("1h")
    private Double oneHour;
    @JsonProperty("3h")
    private Double threeHour;
}
