package com.SWP391.KoiXpress.Model.request.Order;

import com.SWP391.KoiXpress.Entity.Enum.HealthFishStatus;
import com.SWP391.KoiXpress.Entity.Enum.OrderStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

@Data
public class UpdateOrderRequest {

    @Enumerated(EnumType.STRING)
    OrderStatus orderStatus;

}
