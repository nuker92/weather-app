package com.ochodek.server.model.inner;

import lombok.Data;

import java.io.Serializable;

@Data
public class OwmWeather implements Serializable {

    private Long id;
    private String main;
    private String description;
    private String icon;
}
