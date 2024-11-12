package com.SWP391.KoiXpress.Model.response.Transaction;

import com.SWP391.KoiXpress.Entity.Enum.TransactionStatus;
import com.SWP391.KoiXpress.Model.response.Payment.PaymentResponse;
import com.SWP391.KoiXpress.Model.response.User.EachUserResponse;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AllTransactionResponse {
    UUID id;

    EachUserResponse from;

    EachUserResponse to;

    PaymentResponse payments;

    @Enumerated(EnumType.STRING)
    TransactionStatus transactionStatus;

    String description;

}
