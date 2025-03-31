package com.genpact.CabBookingApp.cabBookingApp.service;

import com.genpact.CabBookingApp.cabBookingApp.entity.DriverLiveStatus;
import com.genpact.CabBookingApp.cabBookingApp.repository.DriverLiveStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DriverLiveStatusService {
    @Autowired
    private DriverLiveStatusRepository driverLiveStatusRepository;

    public Optional<DriverLiveStatus> getDriverLiveStatus(Long driverId) {
        return driverLiveStatusRepository.findByDriver_DriverId(driverId);
    }

    public DriverLiveStatus saveDriverLiveStatus(DriverLiveStatus status) {
        return driverLiveStatusRepository.save(status);
    }
}
