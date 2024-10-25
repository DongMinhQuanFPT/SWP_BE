package com.SWP391.KoiXpress.Model.response.WareHouse;

import lombok.Data;

@Data
public class CreateWarehouseResponse {
    long id;
    String location;
    boolean isAvailable;
}
