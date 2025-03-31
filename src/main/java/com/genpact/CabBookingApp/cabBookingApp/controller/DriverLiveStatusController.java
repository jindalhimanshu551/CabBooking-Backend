package com.genpact.CabBookingApp.cabBookingApp.controller;

import com.genpact.CabBookingApp.cabBookingApp.entity.DriverLiveStatus;
import com.genpact.CabBookingApp.cabBookingApp.service.DriverLiveStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/driver-status")
public class DriverLiveStatusController {

    @Autowired
    private DriverLiveStatusService driverLiveStatusService;

//    @GetMapping("/{driverId}")
//    public ResponseEntity<DriverLiveStatus> getDriverLiveStatus(@PathVariable Long driverId) {
//        return driverLiveStatusService.getDriverLiveStatus(driverId)
//                .map(status -> ResponseEntity.ok(status))
//                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
//    }
//
//    @PostMapping
//    public ResponseEntity<DriverLiveStatus> saveDriverLiveStatus(@RequestBody DriverLiveStatus driverLiveStatus) {
//        DriverLiveStatus savedStatus = driverLiveStatusService.saveDriverLiveStatus(driverLiveStatus);
//        return ResponseEntity.status(HttpStatus.CREATED).body(savedStatus);
//    }
    // TODO: TO BE IMPLEMENTED
}
