package com.SWP391.KoiXpress.Entity;

import com.SWP391.KoiXpress.Entity.Enum.TransactionStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "`transaction`")
public class Transactions {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @ManyToOne
    @JoinColumn(name = "from_id")
    Users from;

    @ManyToOne
    @JoinColumn(name = "to_id")
    Users to;

    @ManyToOne
    @JoinColumn(name = "payment_id")
    Payments payments;

    @Enumerated(EnumType.STRING)
    TransactionStatus transactionStatus;

    String description;
}
