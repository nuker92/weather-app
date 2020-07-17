package com.ochodek.server.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class AnotherCityName {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String anotherName;

    @JoinColumn(name = "cityId")
    @ManyToOne
    private City city;


}
