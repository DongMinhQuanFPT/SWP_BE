package com.SWP391.KoiXpress.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Set;


@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "`ware_house`")
public class WareHouses {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    String location;

    int maxCapacity;

    int currentCapacity = 0;

    boolean isAvailable=true;

    @OneToMany(mappedBy = "wareHouses")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<Progresses> progresses;

//    @OneToMany(mappedBy = "wareHouses")
//    @ToString.Exclude
//    @EqualsAndHashCode.Exclude
//    Set<Vehicles> vehiclesSet;

    @OneToMany(mappedBy = "wareHouses", cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    Set<Orders> orders;
}
