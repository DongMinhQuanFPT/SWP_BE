package com.SWP391.KoiXpress.Model.request.Blog;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class CreateBlogRequest {
    @Column(length=50)
    String img;

    @Column(length = 2000)
    String post;
}
