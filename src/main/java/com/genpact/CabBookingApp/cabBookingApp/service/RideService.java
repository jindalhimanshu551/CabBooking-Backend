package com.genpact.CabBookingApp.cabBookingApp.service;

import com.genpact.CabBookingApp.cabBookingApp.entity.Ride;
import com.genpact.CabBookingApp.cabBookingApp.entity.User;
import com.genpact.CabBookingApp.cabBookingApp.repository.RideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RideService {
    @Autowired
    private RideRepository rideRepository;

    public List<Ride> getAllRides() {
        return rideRepository.findAll();
    }

    public List<Ride> getRidesByPassenger(User passenger) {
        return rideRepository.findByPassenger(passenger);
    }

    public Ride getRideByIdAndUser(Long rideId, User user) {
        Optional<Ride> rideOpt = rideRepository.findByRideIdAndPassenger(rideId, user);
        return rideOpt.orElse(null); // or throw an exception if ride not found
    }

    public Ride saveRide(Ride ride) {
        return rideRepository.save(ride);
    }
}
