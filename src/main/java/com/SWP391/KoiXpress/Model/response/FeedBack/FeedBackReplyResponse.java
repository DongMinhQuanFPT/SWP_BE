package com.SWP391.KoiXpress.Model.response.FeedBack;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class FeedBackReplyResponse {
    String replyContent;

    String repliedBy;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    Date replyDate;
}
