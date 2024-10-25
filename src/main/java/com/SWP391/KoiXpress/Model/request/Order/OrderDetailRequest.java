package com.SWP391.KoiXpress.Model.request.Order;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.format.annotation.NumberFormat;


@Data
public class OrderDetailRequest {
    @NumberFormat(pattern = "#.##")
    double priceOfFish;

    @NotBlank(message = "nameFarm can not blank")
    String nameFarm;

    @NotBlank(message = "farmAddress can not blank")
    String farmAddress;

    @NotBlank(message = "origin of fish should not null")
    String origin;

    @NotBlank(message = "fishSpecies can not blank")
    String fishSpecies;

    int numberOfFish;

    @NumberFormat(pattern = "#.##")
    double sizeOfFish;

}
