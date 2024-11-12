package com.SWP391.KoiXpress.Model.response.WareHouse;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateWareHouseRequest {

    long id;

    int maxCapacity;

    boolean isAvailable;
}
