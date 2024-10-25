package com.SWP391.KoiXpress.Model.response.Box;

import com.SWP391.KoiXpress.Entity.BoxDetails;
import lombok.Data;

import java.util.List;

@Data
public class CreateBoxResponse {
    long id;
    String type;
    double volume;
    double price;
    List<BoxDetails> boxDetails;
}
