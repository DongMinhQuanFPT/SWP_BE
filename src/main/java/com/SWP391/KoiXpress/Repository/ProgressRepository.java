package com.SWP391.KoiXpress.Repository;

import com.SWP391.KoiXpress.Entity.Orders;
import com.SWP391.KoiXpress.Entity.Progresses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProgressRepository extends JpaRepository<Progresses, Long> {
    Optional<List<Progresses>> findProgressesByOrdersId(long orderId);

    @Query("SELECT p FROM Progresses p WHERE p.orders.trackingOrder = :trackingOrder AND p.progressStatus IS NOT NULL")
    List<Progresses> findProgressesByTrackingOrderAndStatusNotNull(UUID trackingOrder);

    Progresses findProgressesById(long id);
}
