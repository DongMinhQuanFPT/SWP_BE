package com.SWP391.KoiXpress.Model.request.WareHouse;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

@Data
public class CreateWareHouseRequest {
    String location;
}
