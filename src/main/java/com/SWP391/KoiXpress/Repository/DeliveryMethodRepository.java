package com.SWP391.KoiXpress.Repository;

import com.SWP391.KoiXpress.Entity.DeliveryMethods;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryMethodRepository extends JpaRepository<DeliveryMethods, Long> {
    DeliveryMethods findDeliveryMethodsById(long id);
}
