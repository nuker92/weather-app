package com.ochodek.server.entity;

import com.ochodek.server.model.CountryCode;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Column
    private String name;
    @Column
    @Enumerated(EnumType.STRING)
    private CountryCode countryCode;
    @Column
    private Double longitude;
    @Column
    private Double latitude;

}
