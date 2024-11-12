package com.SWP391.KoiXpress.Model.request.Vehicle;

import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.NumberFormat;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateVehicleRequest {

    @Min(value = 1, message = "quantity vehicle at least 1")
    int quantityVehicle;

    @Min(value = 500, message = "Volume not least than 500")
    @NumberFormat(pattern = "#.##")
    double volume;

    long wareHouseId;
}
