package com.ochodek.server.model.inner;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Snow {

    @JsonProperty("1h")
    private Double oneHour;
    @JsonProperty("3h")
    private Double threeHour;
}
