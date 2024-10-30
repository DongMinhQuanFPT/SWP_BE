package com.SWP391.KoiXpress.Model.response.Box;

import com.SWP391.KoiXpress.Entity.BoxDetails;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateBoxResponse {

    long id;

    String type;

    double volume;

    double price;

    List<BoxDetails> boxDetails;
}
