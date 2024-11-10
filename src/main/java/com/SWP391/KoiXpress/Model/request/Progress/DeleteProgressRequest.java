package com.SWP391.KoiXpress.Model.request.Progress;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class DeleteProgressRequest {

    @NotBlank(message = "Reason can not null")
    private String reason;

}
