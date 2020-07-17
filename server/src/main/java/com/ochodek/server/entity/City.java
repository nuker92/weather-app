package com.ochodek.server.entity;

import com.ochodek.server.model.CountryCode;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Column
    @OneToMany(mappedBy = "city")
    private List<Weather> weathers;

    @Column
    @OneToMany(mappedBy = "city")
    private List<AnotherCityName> anotherCityNames;

}
