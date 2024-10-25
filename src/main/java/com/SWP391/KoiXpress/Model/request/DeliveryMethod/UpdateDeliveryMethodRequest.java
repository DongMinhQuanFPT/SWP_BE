package com.SWP391.KoiXpress.Model.request.DeliveryMethod;

import lombok.Data;
import org.springframework.format.annotation.NumberFormat;

@Data
public class UpdateDeliveryMethodRequest {
    String typeVehicle;

    @NumberFormat(pattern = "#.##")
    double price;
}
