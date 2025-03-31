package com.genpact.CabBookingApp.cabBookingApp.repository;

import com.genpact.CabBookingApp.cabBookingApp.entity.DriverLiveStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DriverLiveStatusRepository extends JpaRepository<DriverLiveStatus, Long> {
    Optional<DriverLiveStatus> findByDriver_DriverId(Long driverId);
}
