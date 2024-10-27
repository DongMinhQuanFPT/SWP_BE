package com.SWP391.KoiXpress.Model.request.FeedBack;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FeedBackRequet {
    @NotNull(message = "you need to rate")
    @Min(value = 0)
    @Max(value = 5)
    float ratingScore;

    @Column(length = 200)
    String comment;

    long orderId;
}
