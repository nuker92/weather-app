package com.ochodek.server.model.inner;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Rain {

    @JsonProperty("1h")
    private Double oneHour;
    @JsonProperty("3h")
    private Double threeHour;

}
