package com.SWP391.KoiXpress.Model.response.Blog;

import com.SWP391.KoiXpress.Model.response.User.EachUserResponse;
import lombok.Data;

@Data
public class AllBlogResponse {
    long blogId;
    String img;
    String post;
    EachUserResponse eachUserResponse;
}
