package com.genpact.CabBookingApp.cabBookingApp.controller;

import com.genpact.CabBookingApp.cabBookingApp.dto.DriverRegistrationRequest;
import com.genpact.CabBookingApp.cabBookingApp.entity.Driver;
import com.genpact.CabBookingApp.cabBookingApp.entity.User;
import com.genpact.CabBookingApp.cabBookingApp.exception.NullException;
import com.genpact.CabBookingApp.cabBookingApp.exception.UserAlreadyExistWithuserIdException;
import com.genpact.CabBookingApp.cabBookingApp.service.DriverRegisterationService;
import com.genpact.CabBookingApp.cabBookingApp.service.DriverService;
import com.genpact.CabBookingApp.cabBookingApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/")
public class PublicController {

    @Autowired
    private UserService userService;

    @Autowired
    private DriverService driverService;

    @Autowired
    private DriverRegisterationService driverRegisterationService;

    @PostMapping("user/")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        if(user == null || user.getEmail() == null || user.getPasswordHash() == null) {
            throw new NullException("User details are missing or incomplete");
        }

        userService.getUserByEmail(user.getEmail()).ifPresent(existingUser -> {
            throw new UserAlreadyExistWithuserIdException("User already exists with email: "+ user.getEmail());
        });
        User savedUser = userService.newSaveUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    @PostMapping("driver/")
    public ResponseEntity<Driver> createDriver(@RequestBody DriverRegistrationRequest request) {
        Driver savedDriver = driverRegisterationService.registerDriver(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedDriver);
    }

}
