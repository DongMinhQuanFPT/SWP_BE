package com.SWP391.KoiXpress.Model.response.Progress;

import com.SWP391.KoiXpress.Entity.Enum.HealthFishStatus;
import com.SWP391.KoiXpress.Entity.Enum.ProgressStatus;
import lombok.Data;

import java.util.Date;

@Data
public class ProgressResponse {
    long id;
    String image;
    Date dateProgress;
    double totalVolume;
    int totalBox;
    boolean isInProgress = false;
    HealthFishStatus healthFishStatus;
    ProgressStatus progressStatus;

}
