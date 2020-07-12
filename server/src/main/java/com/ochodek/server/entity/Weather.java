package com.ochodek.server.entity;


import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
public class Weather {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private LocalDateTime weatherTime;

    @ManyToOne
    @JoinColumn(name = "cityId")
    private City city;

    @Column
    private String mainInfo;

    @Column
    private String description;

    @Column
    private Long visibility;

    @Column
    private Double windSpeed;

    @Column
    private Double windDirection;

    @Column
    private Double clouds;

    @Column
    private Double rainOneHour;

    @Column
    private Double snowOneHour;

    @Column
    private Double pressure;

    @Column
    private Double humidity;

    @Column
    private Double temperature;

    @Column
    private Double feelsLikeTemperature;

}
