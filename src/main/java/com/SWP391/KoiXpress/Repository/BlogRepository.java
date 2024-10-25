package com.SWP391.KoiXpress.Repository;

import com.SWP391.KoiXpress.Entity.Blogs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlogRepository extends JpaRepository<Blogs, Long> {
    Blogs findBlogsById(long Id);
}
