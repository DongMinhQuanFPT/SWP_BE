package com.SWP391.KoiXpress.Entity;

import com.SWP391.KoiXpress.Entity.Enum.HealthFishStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.NumberFormat;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "`order_detail`")
public class OrderDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    UUID healthCertificate = UUID.randomUUID();

    @NumberFormat(pattern = "#.##")
    double priceOfFish;

    @NotBlank(message = "nameFarm can not blank")
    String nameFarm;

    @NotBlank(message = "farmAddress can not blank")
    String farmAddress;

    @NotBlank(message = "origin of fish should not null")
    String origin;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    Date inspectionDate;

    @NotBlank(message = "fishSpecies can not blank")
    String fishSpecies;

    int numberOfFish;

    @NumberFormat(pattern = "#.##")
    double sizeOfFish;


    int totalBox;
    @NumberFormat(pattern = "#.##")
    double totalVolume;
    @NumberFormat(pattern = "#.##")
    double price;
    //

    @Enumerated(EnumType.STRING)
    HealthFishStatus healthFishStatus;

    @ManyToOne
    @JoinColumn(name = "order_id")
    @JsonIgnore
    Orders orders;

    @OneToMany(mappedBy = "orderDetails", cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<BoxDetails> boxDetails;
}
