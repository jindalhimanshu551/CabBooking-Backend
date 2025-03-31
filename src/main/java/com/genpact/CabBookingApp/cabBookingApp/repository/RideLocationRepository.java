package com.genpact.CabBookingApp.cabBookingApp.repository;

import com.genpact.CabBookingApp.cabBookingApp.entity.RideLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RideLocationRepository extends JpaRepository<RideLocation, Long> {
    List<RideLocation> findByRide_RideIdOrderByUpdatedAtAsc(Long rideId);
}
