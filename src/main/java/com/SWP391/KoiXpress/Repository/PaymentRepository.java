package com.SWP391.KoiXpress.Repository;

import com.SWP391.KoiXpress.Entity.Payments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payments, UUID> {
}
