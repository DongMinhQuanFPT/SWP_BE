package com.SWP391.KoiXpress.Entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;


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

    boolean isAvailable=true;

    @OneToMany(mappedBy = "wareHouses")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<Progresses> progresses;
}
