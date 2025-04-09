package com.genpact.CabBookingApp.cabBookingApp.controller;

import com.genpact.CabBookingApp.cabBookingApp.dto.DriverUpdateRequest;
import com.genpact.CabBookingApp.cabBookingApp.entity.Driver;
import com.genpact.CabBookingApp.cabBookingApp.entity.Ride;
import com.genpact.CabBookingApp.cabBookingApp.entity.RideStatus;
import com.genpact.CabBookingApp.cabBookingApp.entity.User;
import com.genpact.CabBookingApp.cabBookingApp.exception.DriverNotFoundException;
import com.genpact.CabBookingApp.cabBookingApp.exception.InvalidPasswordException;
import com.genpact.CabBookingApp.cabBookingApp.exception.NotFoundException;
import com.genpact.CabBookingApp.cabBookingApp.repository.DriverRepository;
import com.genpact.CabBookingApp.cabBookingApp.repository.RideRepository;
import com.genpact.CabBookingApp.cabBookingApp.repository.UserRepository;
import com.genpact.CabBookingApp.cabBookingApp.service.DriverService;
import com.genpact.CabBookingApp.cabBookingApp.service.RideService;
import com.genpact.CabBookingApp.cabBookingApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/driver")
public class DriverController {

    @Autowired
    private DriverService driverService;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private RideService rideService;

    @Autowired
    private UserService userService;


    @PostMapping
    public ResponseEntity<Driver> createDriver(@RequestBody Driver driver) {
        Driver savedDriver = driverService.saveDriver(driver);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedDriver);
    }

    @PutMapping()
    public ResponseEntity<Driver> updateDriver(@RequestBody DriverUpdateRequest updateRequest) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) auth.getPrincipal();

        Driver driverObj = driverService.getDriverByEmail(userDetails.getUsername())
                .orElseThrow(() -> new DriverNotFoundException("Driver not found with Email"));

        driverObj.setLicenseNumber(updateRequest.getLicenseNumber());
        driverObj.setVehicleNumber(updateRequest.getVehicleNumber());
        driverObj.setVehicleModel(updateRequest.getVehicleModel());
        driverObj.setVehicleType(updateRequest.getVehicleType());
        driverObj.setAvailability(updateRequest.getAvailability());
        return ResponseEntity.ok(driverObj);
    }

    @GetMapping("/current")
    public ResponseEntity<?> getCurrentDriver() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"message\": \"Not Authorized\"}");
        }
        String email = authentication.getName();
        User userObj = userService.getUserByEmail(email)
                .orElse(null);
        if (userObj != null) {
            Optional<Driver> driverObj = driverRepository.findByUser(userObj);
            return ResponseEntity.status(HttpStatus.OK).body(driverObj);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PostMapping("/ride/{rideId}/response")
    public ResponseEntity<?> respondToRide(
            @PathVariable Long rideId,
            @RequestBody Map<String, Object> requestBody) {

        Long driverId = Long.valueOf(requestBody.get("driverId").toString());
        boolean accepted = Boolean.parseBoolean(requestBody.get("accepted").toString());

        // Optional: Verify the driver matches the authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User userObj = userService.getUserByEmail(email)
                .orElse(null);

        if (userObj == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Optional<Driver> driverObj = driverRepository.findByUser(userObj);
        if (driverId != driverObj.get().getDriverId()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        // Call RideService to handle the response
        boolean success = rideService.handleDriverResponse(rideId, driverId, accepted);

        if (success) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().body("Failed to process response");
        }
    }

    @PostMapping("/ride/{rideId}/start")
    public ResponseEntity<?> startRide(
            @PathVariable Long rideId,
            @RequestBody Map<String, Object> requestBody) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Optional<Driver> driverObj = driverRepository.findByUserEmail(email);
        Optional<Ride> rideObj = rideRepository.findById(rideId);
        if (driverObj.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Driver driver = driverObj.get();

        if (rideObj.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Ride ride = rideObj.get();
        Driver assignedDriver = ride.getDriver();

        if (!driver.equals(assignedDriver)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        String requestOtp = (String) requestBody.get("otp");
        String passengerOtp = ride.getPassenger().getOtp();

        if (passengerOtp.equals(requestOtp)) {
            ride.setStartedAt(LocalDateTime.now());
            ride.setStatus(RideStatus.ONGOING);
            rideRepository.save(ride);
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("/ride/{rideId}/complete")
    public ResponseEntity<?> completeRide(
            @PathVariable Long rideId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Optional<Driver> driverObj = driverRepository.findByUserEmail(email);
        Optional<Ride> rideObj = rideRepository.findById(rideId);
        if (driverObj.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Driver driver = driverObj.get();

        if (rideObj.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Ride ride = rideObj.get();
        Driver assignedDriver = ride.getDriver();

        if (!driver.equals(assignedDriver)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        ride.setCompletedAt(LocalDateTime.now());
        ride.setStatus(RideStatus.COMPLETED);
        rideRepository.save(ride);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
