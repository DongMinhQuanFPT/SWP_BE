package com.SWP391.KoiXpress.Entity;

import com.SWP391.KoiXpress.Entity.Enum.HealthFishStatus;
import com.SWP391.KoiXpress.Entity.Enum.ProgressStatus;
import com.SWP391.KoiXpress.Entity.Enum.VehicleType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;


@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "`progress`")
public class Progresses {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    String image;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    Date dateProgress;

    boolean isInProgress = false;

    @Enumerated(EnumType.STRING)
    HealthFishStatus healthFishStatus;

    @Enumerated(EnumType.STRING)
    ProgressStatus progressStatus;

    @Enumerated(EnumType.STRING)
    VehicleType vehicleType;

    String from_Location;

    String to_Location;

    String delivery_name;

    String delivery_phone;

    String failure_reason;

    @ManyToOne
    @JoinColumn(name = "order_id")
    Orders orders;

    @ManyToOne
    @JoinColumn(name = "warehouse_id")
    WareHouses wareHouses;

}
