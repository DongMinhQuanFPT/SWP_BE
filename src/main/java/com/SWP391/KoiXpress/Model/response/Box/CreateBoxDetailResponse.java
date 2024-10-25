package com.SWP391.KoiXpress.Model.response.Box;

import com.SWP391.KoiXpress.Entity.BoxDetails;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.NumberFormat;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateBoxDetailResponse {
    List<BoxDetails> boxDetails;
    @NumberFormat(pattern = "#.##")
    double totalPrice;
    @NumberFormat(pattern = "#.##")
    double totalVolume;
    int totalCount;

}
