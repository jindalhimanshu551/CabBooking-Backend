package com.genpact.CabBookingApp.cabBookingApp.service;

import com.genpact.CabBookingApp.cabBookingApp.entity.Driver;
import com.genpact.CabBookingApp.cabBookingApp.exception.*;
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
        List<Driver> drivers = driverRepository.findAll();
        if(drivers.isEmpty()) {
            throw new NotFoundException("No driver found in the system");
        }
        return drivers;
    }

    public Optional<Driver> getDriverById(Long id) {
        if(id==0 || id <= 0) {
            throw new InvalidIdException("Driver ID is invalid" + id);
        }
        return driverRepository.findById(id);
    }

    public Optional<Driver> getDriverByEmail(String email) {
        if(email == null || email.isBlank()) {
            throw new InvalidIdException("Email cannot be null or empty");
        }
        return driverRepository.findByUserEmail(email);
    }

    public List<Driver> getAvailableDrivers() {
        List<Driver> availableDrivers = driverRepository.findByAvailabilityTrue();
        if(availableDrivers.isEmpty()) {
            throw new NotFoundException("No available drivers at the moment");
        }
        return availableDrivers;
    }

    public Driver saveDriver(Driver driver) {
        if(driver == null) {
            throw new NullException("Driver object cannot be null");
        }

        if(driver.getUser() != null && driverRepository.findByUserEmail(driver.getUser().getEmail()).isPresent()) {
            throw new UserAlreadyExistWithuserIdException("Driver with email already exists: "+ driver.getUser().getEmail());
        }
        return driverRepository.save(driver);
    }

    public boolean deleteDriver(Long id) {
        if(id==0 || id <= 0) {
            throw new InvalidIdException("Driver ID is invalid" + id);
        }
        if (!driverRepository.existsById(id)){
            throw new DriverNotFoundException("Cannot delete. Driver not found with ID "+id);
        }

        driverRepository.deleteById(id);
        return true;
    }

}

