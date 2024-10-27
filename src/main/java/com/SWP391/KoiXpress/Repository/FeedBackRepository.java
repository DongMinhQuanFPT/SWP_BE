package com.SWP391.KoiXpress.Repository;

import com.SWP391.KoiXpress.Entity.FeedBacks;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedBackRepository extends JpaRepository<FeedBacks, Long> {
    Page<FeedBacks> findFeedBacksByUsersId(Long userId, Pageable pageable);
    List<FeedBacks> findFeedBacksByOrdersId(Long orderId);

    FeedBacks findById(long feedId);
    @Query("SELECT AVG(f.ratingScore) FROM FeedBacks f")
    Double getAverageRating();

}
