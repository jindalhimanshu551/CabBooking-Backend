package com.genpact.CabBookingApp.cabBookingApp.service;

import com.genpact.CabBookingApp.cabBookingApp.entity.RideLocation;
import com.genpact.CabBookingApp.cabBookingApp.repository.RideLocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RideLocationService {
    @Autowired
    private RideLocationRepository rideLocationRepository;

    public List<RideLocation> getRideLocations(Long rideId) {
        return rideLocationRepository.findByRide_RideIdOrderByUpdatedAtAsc(rideId);
    }

    public RideLocation saveRideLocation(RideLocation rideLocation) {
        return rideLocationRepository.save(rideLocation);
    }
}
