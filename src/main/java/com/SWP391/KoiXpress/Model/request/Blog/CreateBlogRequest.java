package com.SWP391.KoiXpress.Model.request.Blog;

import jakarta.persistence.Column;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateBlogRequest {

    @Column(length=50)
    String img;

    @Column(length = 2000)
    String post;
}
