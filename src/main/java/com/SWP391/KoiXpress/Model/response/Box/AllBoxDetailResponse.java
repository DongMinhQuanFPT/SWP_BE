package com.SWP391.KoiXpress.Model.response.Box;

import com.SWP391.KoiXpress.Entity.Boxes;
import com.SWP391.KoiXpress.Entity.OrderDetails;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AllBoxDetailResponse {

    long id;

    int quantity;

    OrderDetails orderDetails;

    Boxes boxes;
}
