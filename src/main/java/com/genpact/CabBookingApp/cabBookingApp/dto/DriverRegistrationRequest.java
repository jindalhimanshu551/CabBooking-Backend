package com.genpact.CabBookingApp.cabBookingApp.dto;

import com.genpact.CabBookingApp.cabBookingApp.entity.VehicleType;
import lombok.Data;

@Data
public class DriverRegistrationRequest {
    private String fullName;
    private String email;
    private String phoneNumber;
    private String password;

    private String licenseNumber;
    private String vehicleNumber;
    private String vehicleModel;
    private VehicleType vehicleType;
}
