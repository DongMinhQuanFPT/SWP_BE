package com.SWP391.KoiXpress.Model.response.WareHouse;

import com.SWP391.KoiXpress.Model.response.Order.AllOrderResponse;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WareHouseContainOrderResponse {

    String locationWareHouse;

    int totalBox;

    String description;

    Set<AllOrderResponse> orders;

}
