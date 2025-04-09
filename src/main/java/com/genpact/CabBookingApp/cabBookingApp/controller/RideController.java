package com.genpact.CabBookingApp.cabBookingApp.controller;

import com.genpact.CabBookingApp.cabBookingApp.entity.Ride;
import com.genpact.CabBookingApp.cabBookingApp.entity.User;
import com.genpact.CabBookingApp.cabBookingApp.service.RideService;
import com.genpact.CabBookingApp.cabBookingApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/ride")
public class RideController {

    @Autowired
    private RideService rideService;

    @Autowired
    private UserService userService;


    @PostMapping
    public ResponseEntity<Ride> createRide(@RequestBody Ride ride) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails currentUser = (UserDetails) authentication.getPrincipal();
        User userObj = userService.getUserByEmail(currentUser.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        ride.setPassenger(userObj);
        Ride savedRide = rideService.saveRide(ride);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedRide);
    }

    @GetMapping("/myRides")
    public ResponseEntity<List<Ride>> getMyRides() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails currentUser = (UserDetails) authentication.getPrincipal();
        User user = userService.getUserByEmail(currentUser.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Ride> rides = rideService.getRidesByPassenger(user);
        if (rides == null || rides.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(rides);
    }

    @GetMapping("/{rideId}")
    public ResponseEntity<Ride> getRideById(@PathVariable Long rideId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails currentUser = (UserDetails) authentication. getPrincipal();
        User user = userService.getUserByEmail(currentUser.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Ride ride = rideService.getRideByIdAndUser(rideId, user);
        if (ride == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(ride);
    }

    @GetMapping("/completed-rides/{driverId}")
    public ResponseEntity<?> getCompletedRides(@PathVariable Long driverId,
                                 @RequestParam int year,
                                 @RequestParam int month) {
        Integer completedRide = rideService.getRidesCompletedByDriverInMonth(driverId, year, month);
        return ResponseEntity.ok(completedRide);
    }

    @GetMapping("/monthly-earnings/{driverId}")
    public ResponseEntity<?> getMonthlyEarnings(@PathVariable Long driverId,
                                         @RequestParam int year,
                                         @RequestParam int month) {
        BigDecimal monthlyEarning = rideService.getMonthlyEarnings(driverId, year, month);
        return ResponseEntity.ok(monthlyEarning);
    }

    @GetMapping("/total-earnings/{driverId}")
    public ResponseEntity<?> getTotalEarnings(@PathVariable Long driverId) {
        BigDecimal totalEarning = rideService.getTotalEarnings(driverId);
        return ResponseEntity.ok(totalEarning);
    }

    @GetMapping("/completed-rides/all/{driverId}")
    public List<Ride> getAllCompletedRides(@PathVariable Long driverId) {
        return rideService.getAllCompletedRides(driverId);
    }

}
