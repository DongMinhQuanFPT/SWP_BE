package com.SWP391.KoiXpress.Repository;

import com.SWP391.KoiXpress.Entity.OrderDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetails, Long> {
}
