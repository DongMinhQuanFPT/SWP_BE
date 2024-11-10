package com.SWP391.KoiXpress.Repository;

import com.SWP391.KoiXpress.Entity.Vehicles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicles, UUID> {
    Optional<Vehicles> findByIdAndAvailableTrue(UUID id);
}
