package com.SWP391.KoiXpress.Entity;

import com.SWP391.KoiXpress.Entity.Enum.HealthFishStatus;
import com.SWP391.KoiXpress.Entity.Enum.ProgressStatus;
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

    @ManyToOne
    @JoinColumn(name = "warehouse_id")
    WareHouses wareHouses;

    @ManyToOne
    @JoinColumn(name = "order_id")
    Orders orders;
}
