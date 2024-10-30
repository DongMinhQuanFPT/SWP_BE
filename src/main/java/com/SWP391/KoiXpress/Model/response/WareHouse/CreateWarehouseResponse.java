package com.SWP391.KoiXpress.Model.response.WareHouse;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateWarehouseResponse {

    long id;

    String location;

    boolean isAvailable;
}
