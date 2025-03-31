package com.genpact.CabBookingApp.cabBookingApp.controller;

import com.genpact.CabBookingApp.cabBookingApp.dto.DriverUpdateRequest;
import com.genpact.CabBookingApp.cabBookingApp.entity.Driver;
import com.genpact.CabBookingApp.cabBookingApp.service.DriverService;
import com.genpact.CabBookingApp.cabBookingApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/driver")
public class DriverController {

    @Autowired
    private DriverService driverService;

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
                .orElseThrow(() -> new RuntimeException("Driver not found with Email"));

        driverObj.setLicenseNumber(updateRequest.getLicenseNumber());
        driverObj.setVehicleNumber(updateRequest.getVehicleNumber());
        driverObj.setVehicleModel(updateRequest.getVehicleModel());
        driverObj.setVehicleType(updateRequest.getVehicleType());
        driverObj.setAvailability(updateRequest.getAvailability());
        return ResponseEntity.ok(driverObj);
    }
}
