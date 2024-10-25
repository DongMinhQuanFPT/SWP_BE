package com.SWP391.KoiXpress.Entity;

import com.SWP391.KoiXpress.Entity.Enum.PaymentMethod;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "`payment`")
public class Payments {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    Date createPayment;

    @Enumerated(EnumType.STRING)
    PaymentMethod paymentMethod;

    @OneToOne
    @JoinColumn(name = "order_id")
    Orders orders;

    @OneToMany(mappedBy = "payments", cascade = CascadeType.ALL)
    Set<Transactions> transactions;
}
