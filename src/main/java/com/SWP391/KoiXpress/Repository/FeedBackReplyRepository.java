package com.SWP391.KoiXpress.Repository;

import com.SWP391.KoiXpress.Entity.FeedBackReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedBackReplyRepository extends JpaRepository<FeedBackReply, Long> {

}
