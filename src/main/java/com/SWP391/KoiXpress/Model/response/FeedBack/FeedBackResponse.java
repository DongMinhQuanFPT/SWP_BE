package com.SWP391.KoiXpress.Model.response.FeedBack;

import com.SWP391.KoiXpress.Model.response.User.EachUserResponse;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class FeedBackResponse {
    long id;
    float ratingScore;
    String comment;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    Date createdTime;
    EachUserResponse eachUserResponse;
    List<FeedBackReplyResponse> replies;
}
