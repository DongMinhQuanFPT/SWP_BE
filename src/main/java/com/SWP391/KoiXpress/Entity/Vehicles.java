//package com.SWP391.KoiXpress.Entity;
//
//import com.fasterxml.jackson.annotation.JsonIgnore;
//import jakarta.persistence.*;
//import jakarta.validation.constraints.Min;
//import lombok.*;
//import lombok.experimental.FieldDefaults;
//import org.springframework.format.annotation.NumberFormat;
//
//import java.util.Set;
//import java.util.UUID;
//
//@Getter
//@Setter
//@Entity
//@NoArgsConstructor
//@AllArgsConstructor
//@FieldDefaults(level = AccessLevel.PRIVATE)
//@Table(name = "`vehicle`")
//public class Vehicles {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.UUID)
//    UUID id;
//
//    @Min(value = 500, message = "Volume not least than 500")
//    @NumberFormat(pattern = "#.##")
//    double volume;
//
//    boolean available = true;
//
//    @ManyToOne
//    @JoinColumn(name = "wareHouse_id")
//    @JsonIgnore
//    WareHouses wareHouses;
//
//    @OneToMany(mappedBy = "vehicles",cascade = CascadeType.ALL)
//    @ToString.Exclude
//    @EqualsAndHashCode.Exclude
//    @JsonIgnore
//    Set<Orders> orders;
//
//}
