package com.genpact.CabBookingApp.cabBookingApp.dto;

import com.genpact.CabBookingApp.cabBookingApp.entity.VehicleType;
import lombok.Data;

@Data
public class DriverUpdateRequest {
    private String licenseNumber;
    private String vehicleNumber;
    private String vehicleModel;
    private VehicleType vehicleType;
    private Boolean availability;
}