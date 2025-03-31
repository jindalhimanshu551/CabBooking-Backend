package com.genpact.CabBookingApp.cabBookingApp.repository;

import com.genpact.CabBookingApp.cabBookingApp.entity.Ride;
import com.genpact.CabBookingApp.cabBookingApp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RideRepository extends JpaRepository<Ride, Long> {
    // Find all rides for a given passenger
    List<Ride> findByPassenger(User passenger);

    // Find a specific ride by rideId for a given passenger
    Optional<Ride> findByRideIdAndPassenger(Long rideId, User passenger);
}
