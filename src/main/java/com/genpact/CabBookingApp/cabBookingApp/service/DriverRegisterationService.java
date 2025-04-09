package com.genpact.CabBookingApp.cabBookingApp.service;

import com.genpact.CabBookingApp.cabBookingApp.dto.DriverRegistrationRequest;
import com.genpact.CabBookingApp.cabBookingApp.entity.Driver;
import com.genpact.CabBookingApp.cabBookingApp.entity.Role;
import com.genpact.CabBookingApp.cabBookingApp.entity.User;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class DriverRegisterationService {

    @Autowired
    private UserService userService;

    @Autowired
    private DriverService driverService;

    @Transactional
    public Driver registerDriver(DriverRegistrationRequest request) {
        // Create and save user
        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .passwordHash(request.getPassword())
                .role(Role.DRIVER)
                .build();
        User savedUser = userService.newSaveUser(user);

        // Create and save driver
        Driver driver = Driver.builder()
                .user(savedUser)
                .licenseNumber(request.getLicenseNumber())
                .vehicleNumber(request.getVehicleNumber())
                .vehicleModel(request.getVehicleModel())
                .vehicleType(request.getVehicleType())
                .availability(true)
                .build();
        return driverService.saveDriver(driver);
    }
}
