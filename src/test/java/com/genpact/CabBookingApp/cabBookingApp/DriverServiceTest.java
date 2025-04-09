package com.genpact.CabBookingApp.cabBookingApp;

import com.genpact.CabBookingApp.cabBookingApp.entity.Driver;
import com.genpact.CabBookingApp.cabBookingApp.entity.User;
import com.genpact.CabBookingApp.cabBookingApp.entity.VehicleType;
import com.genpact.CabBookingApp.cabBookingApp.repository.DriverRepository;
import com.genpact.CabBookingApp.cabBookingApp.service.DriverService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DriverServiceTest {
 
    @Mock
    private DriverRepository driverRepository;
 
    @InjectMocks
    private DriverService driverService;
 
    private Driver driver;
    private User user;
 
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
 
        user = User.builder()
                .userId(1L)
                .email("driver@example.com")
                .fullName("John Driver")
                .build();
 
        driver = new Driver();
        driver.setDriverId(1L);
        driver.setUser(user);
        driver.setLicenseNumber("LIC123456");
        driver.setVehicleNumber("KA01AB1234");
        driver.setVehicleModel("Swift");
        driver.setVehicleType(VehicleType.SEDAN);
        driver.setAvailability(true);
    }
 
    @Test
    void testGetAllDrivers() {
        when(driverRepository.findAll()).thenReturn(List.of(driver));
        List<Driver> drivers = driverService.getAllDrivers();
        assertEquals(1, drivers.size());
        verify(driverRepository, times(1)).findAll();
    }
 
    @Test
    void testGetDriverById() {
        when(driverRepository.findById(1L)).thenReturn(Optional.of(driver));
        Optional<Driver> found = driverService.getDriverById(1L);
        assertTrue(found.isPresent());
        assertEquals("LIC123456", found.get().getLicenseNumber());
    }
 
    @Test
    void testGetDriverByEmail() {
        when(driverRepository.findByUserEmail("driver@example.com")).thenReturn(Optional.of(driver));
        Optional<Driver> found = driverService.getDriverByEmail("driver@example.com");
        assertTrue(found.isPresent());
        assertEquals("Swift", found.get().getVehicleModel());
    }
 
    @Test
    void testGetAvailableDrivers() {
        when(driverRepository.findByAvailabilityTrue()).thenReturn(List.of(driver));
        List<Driver> availableDrivers = driverService.getAvailableDrivers();
        assertEquals(1, availableDrivers.size());
        assertTrue(availableDrivers.get(0).getAvailability());
    }
 
    @Test
    void testSaveDriver() {
        when(driverRepository.save(driver)).thenReturn(driver);
        Driver saved = driverService.saveDriver(driver);
        assertNotNull(saved);
        assertEquals("KA01AB1234", saved.getVehicleNumber());
    }
 
    @Test
    void testDeleteDriver_Success() {
        when(driverRepository.existsById(1L)).thenReturn(true);
        boolean deleted = driverService.deleteDriver(1L);
        assertTrue(deleted);
        verify(driverRepository, times(1)).deleteById(1L);
    }
 
    @Test
    void testDeleteDriver_NotFound() {
        when(driverRepository.existsById(2L)).thenReturn(false);
        boolean deleted = driverService.deleteDriver(2L);
        assertFalse(deleted);
        verify(driverRepository, never()).deleteById(2L);
    }
}
 
