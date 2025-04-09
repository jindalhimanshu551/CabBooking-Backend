package com.genpact.CabBookingApp.cabBookingApp.controller;

import com.genpact.CabBookingApp.cabBookingApp.entity.Ride;
import com.genpact.CabBookingApp.cabBookingApp.entity.Role;
import com.genpact.CabBookingApp.cabBookingApp.entity.User;
import com.genpact.CabBookingApp.cabBookingApp.service.RideService;
import com.genpact.CabBookingApp.cabBookingApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/admin/")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private RideService rideService;

    @GetMapping("allUsers/")
    public ResponseEntity<?> getAllUsers() {
        List<User> all = userService.getAllUsers();
        if (all != null && !all.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(all);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping("allDrivers/")
    public ResponseEntity<?> getAllDrivers() {
        List<User> drivers = userService.getUsersByRole(Role.DRIVER);
        if (drivers != null && !drivers.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(drivers);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping("allRides/")
    public ResponseEntity<?> getAllRides() {
        List<Ride> rides = rideService.getAllRides();
        if (rides != null && !rides.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(rides);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping("allPassengers/")
    public ResponseEntity<?> getAllPassengers() {
        List<User> passengers = userService.getAllPassengers();
        if(passengers.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No passengers found.");
        }
        return ResponseEntity.ok(passengers);
    }

    @GetMapping("totalEarnings/")
    public ResponseEntity<?> getTotalEarnings() {
        BigDecimal totalEarnings = rideService.calculateTotalEarnings();
        return ResponseEntity.ok(totalEarnings);
    }

    @GetMapping("/driver/earnings")
    public ResponseEntity<?> getEarningByDriverEmail(@RequestParam String email){
        BigDecimal earnings = rideService.getTotalEarningsByDriverEmail(email);
        return ResponseEntity.ok(earnings);
    }

    @GetMapping("/passenger/spending")
    public ResponseEntity<?> getSpendingByPassengerEmail(@RequestParam String email){
        BigDecimal spending = rideService.getTotalSpendingByPassengerEmail(email);
        return ResponseEntity.ok(spending);
    }

    @DeleteMapping("deleteUser/")
    public ResponseEntity<String> deleteUser(@RequestParam Long id) {
        boolean isDeleted = userService.deleteUser(id);
        if (isDeleted) {
            return ResponseEntity.ok("User with ID " + id + " has been deleted successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with ID " + id + " not found.");
        }
    }
}
