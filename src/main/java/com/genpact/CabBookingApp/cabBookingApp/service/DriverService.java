package com.genpact.CabBookingApp.cabBookingApp.service;

import com.genpact.CabBookingApp.cabBookingApp.entity.Driver;
import com.genpact.CabBookingApp.cabBookingApp.entity.User;
import com.genpact.CabBookingApp.cabBookingApp.repository.DriverRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

@Service
public class DriverService {
    @Autowired
    private DriverRepository driverRepository;

    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }

    public Optional<Driver> getDriverById(Long id) {
        return driverRepository.findById(id);
    }

    public Optional<Driver> getDriverByEmail(String email) {
        return driverRepository.findByUserEmail(email);
    }

    public List<Driver> getAvailableDrivers() {
        return driverRepository.findByAvailabilityTrue();
    }

    public Driver saveDriver(Driver driver) {
        return driverRepository.save(driver);
    }

    public boolean deleteDriver(Long id) {
        if (driverRepository.existsById(id)) {
            driverRepository.deleteById(id);
            return true;  // Deletion successful
        }
        return false;  // User not found
    }

}

