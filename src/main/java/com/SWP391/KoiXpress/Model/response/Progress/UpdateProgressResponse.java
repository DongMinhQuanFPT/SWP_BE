package com.SWP391.KoiXpress.Model.response.Progress;

import com.SWP391.KoiXpress.Entity.Enum.*;
import com.SWP391.KoiXpress.Entity.Orders;
import com.SWP391.KoiXpress.Entity.WareHouses;
import com.SWP391.KoiXpress.Model.response.Order.AllOrderResponse;
import com.SWP391.KoiXpress.Model.response.WareHouse.AllWareHouseResponse;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;


import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateProgressResponse {

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

    AllOrderResponse orders;

    AllWareHouseResponse wareHouses;
}
