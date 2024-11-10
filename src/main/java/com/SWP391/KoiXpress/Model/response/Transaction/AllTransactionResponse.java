package com.SWP391.KoiXpress.Model.response.Transaction;

import com.SWP391.KoiXpress.Entity.Enum.TransactionStatus;
import com.SWP391.KoiXpress.Entity.Payments;
import com.SWP391.KoiXpress.Entity.Users;
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

    Users from;

    Users to;

    Payments payments;

    TransactionStatus transactionStatus;

    String description;

}
