package com.SWP391.KoiXpress.Model.request.Progress;

import com.SWP391.KoiXpress.Entity.Enum.HealthFishStatus;
import com.SWP391.KoiXpress.Entity.Enum.ProgressStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

@Data
public class UpdateProgressRequest {

    String image;

    @Enumerated(EnumType.STRING)
    HealthFishStatus healthFishStatus;

    @Enumerated(EnumType.STRING)
    ProgressStatus progressStatus;

}
