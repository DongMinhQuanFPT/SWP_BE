package com.SWP391.KoiXpress.Repository;

import com.SWP391.KoiXpress.Entity.Transactions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transactions, UUID> {
}
