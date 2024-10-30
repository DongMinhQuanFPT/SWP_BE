package com.SWP391.KoiXpress.Model.response.Blog;

import com.SWP391.KoiXpress.Entity.Users;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeleteBlogResponse {

    long id;

    String img;

    String post;

    boolean isDeleted =false;

    Users users;
}
