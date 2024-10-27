package com.SWP391.KoiXpress.Model.request.Box;

import lombok.Data;
import org.springframework.format.annotation.NumberFormat;

@Data
public class UpdateBoxRequest {
    String type;

    @NumberFormat(pattern = "#.##")
    double volume;

    @NumberFormat(pattern = "#.##")
    double price;
}
