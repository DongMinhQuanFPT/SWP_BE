package com.SWP391.KoiXpress.Model.response.Blog;

import com.SWP391.KoiXpress.Model.response.User.EachUserResponse;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateBlogResponse {

    long blogId;

    String img;

    String post;

    EachUserResponse eachUserResponse;
}
