package com.SWP391.KoiXpress.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "`box_detail`")
public class BoxDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    int quantity;

    @ManyToOne
    @JoinColumn(name = "order_detail_id")
    @JsonIgnore
    OrderDetails orderDetails;

    @ManyToOne
    @JoinColumn(name = "box_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    Boxes boxes;

}
