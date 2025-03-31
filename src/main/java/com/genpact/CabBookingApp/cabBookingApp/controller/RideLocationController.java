package com.genpact.CabBookingApp.cabBookingApp.controller;

import com.genpact.CabBookingApp.cabBookingApp.entity.RideLocation;
import com.genpact.CabBookingApp.cabBookingApp.service.RideLocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ride-locations")
public class RideLocationController {

//    @Autowired
//    private RideLocationService rideLocationService;
//
//    @GetMapping("/{rideId}")
//    public ResponseEntity<List<RideLocation>> getRideLocations(@PathVariable Long rideId) {
//        return ResponseEntity.ok(rideLocationService.getRideLocations(rideId));
//    }
//
//    @PostMapping
//    public ResponseEntity<RideLocation> saveRideLocation(@RequestBody RideLocation rideLocation) {
//        RideLocation savedRideLocation = rideLocationService.saveRideLocation(rideLocation);
//        return ResponseEntity.status(HttpStatus.CREATED).body(savedRideLocation);
//    }

    // TODO: TO BE IMPLEMENTED
}