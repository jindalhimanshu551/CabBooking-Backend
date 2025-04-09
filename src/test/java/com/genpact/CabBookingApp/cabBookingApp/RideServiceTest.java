package com.genpact.CabBookingApp.cabBookingApp;

import com.genpact.CabBookingApp.cabBookingApp.entity.Ride;
import com.genpact.CabBookingApp.cabBookingApp.entity.RideStatus;
import com.genpact.CabBookingApp.cabBookingApp.entity.User;
import com.genpact.CabBookingApp.cabBookingApp.repository.DriverRepository;
import com.genpact.CabBookingApp.cabBookingApp.repository.RideRepository;
import com.genpact.CabBookingApp.cabBookingApp.service.RideService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RideServiceTest {
	
	@Mock
	private RideRepository rideRepository;
	
	@InjectMocks
	private RideService rideService;

	@Mock
	private DriverRepository driverRepository;
	
	private User testPassenger;
	private Ride ride1, ride2;
	
	@BeforeEach
	void setup() {
		testPassenger = User.builder()
				.userId(1L)
				.fullName("Passenger One")
				.email("passenger@example.com")
				.build();
		ride1 = Ride.builder()
				.rideId(101L)
				.passenger(testPassenger)
				.pickupLocation("Location A")
				.dropoffLocation("Location B")
				.build();
		
		ride2 = Ride.builder()
				.rideId(102L)
				.passenger(testPassenger)
				.pickupLocation("Location C")
				.dropoffLocation("Location D")
				.build();

	}
	
	@Test
	void testGetAllRides() {
		List<Ride> mockRides = Arrays.asList(ride1, ride2);
		when(rideRepository.findAll()).thenReturn(mockRides);
		
		List<Ride> result = rideService.getAllRides();
		
		assertEquals(2, result.size());
		verify(rideRepository, times(1)).findAll();
	}
	
	@Test
	void testGetAllRidesWhenNoneExist() {
	    when(rideRepository.findAll()).thenReturn(Collections.emptyList());
	    List<Ride> rides = rideService.getAllRides();
	    assertTrue(rides.isEmpty());
	}
	 
	
	@Test
	void testGetRidesByPassenger() {
		when(rideRepository.findByPassenger(testPassenger)).thenReturn(Arrays.asList(ride1, ride2));
		
		List<Ride> result = rideService.getRidesByPassenger(testPassenger);
		
		assertEquals(2, result.size());
		assertEquals(testPassenger, result.get(0).getPassenger());
	}
	
	@Test
	void testGetRideByIdAndUser_Found() {
		when(rideRepository.findByRideIdAndPassenger(101L, testPassenger)).thenReturn(Optional.of(ride1));
		
		Ride result = rideService.getRideByIdAndUser(101L, testPassenger);
		
		assertNotNull(result);
		assertEquals(101L, result.getRideId());
		assertEquals(testPassenger, result.getPassenger());
	}
	
	@Test
	void testGetRideByIdAndUser_NotFound() {
		when(rideRepository.findByRideIdAndPassenger(999L, testPassenger)).thenReturn(Optional.empty());
		
		Ride result = rideService.getRideByIdAndUser(999L, testPassenger);
		
		assertNull(result);
	}
	
	@Test
	void testSaveRide() {
		when(rideRepository.save(ride1)).thenReturn(ride1);
		
		Ride result = rideService.saveRide(ride1);
		
		assertNotNull(result);
		assertEquals("Location A", result.getPickupLocation());
		verify(rideRepository, times(1)).save(ride1);
		
	}
	
	@Test
	void testGetRidesByPassengerWithNoRides() {
	    when(rideRepository.findByPassenger(testPassenger)).thenReturn(Collections.emptyList());
	    List<Ride> rides = rideService.getRidesByPassenger(testPassenger);
	    assertTrue(rides.isEmpty());
	}
	
//	@Test
//	void testGetRideByIdAndUserWithInvalidId() {
//	    when(rideRepository.findByRideIdAndPassenger(999L, testPassenger)).thenReturn(Optional.empty());
//	    assertThrows(RideNotFoundException.class, () -> {
//	        rideService.getRideByIdAndUser(999L, testPassenger);
//	    });
//	}
	
	@Test
	void testCalculateTotalEarnings() {
	    when(rideRepository.getTotalEarnings(RideStatus.COMPLETED))
	            .thenReturn(new BigDecimal("550.75"));
	 
	    BigDecimal result = rideService.calculateTotalEarnings();
	 
	    assertEquals(new BigDecimal("550.75"), result);
	    verify(rideRepository, times(1)).getTotalEarnings(RideStatus.COMPLETED);
	}
	
	@Test
	void testGetTotalEarningsByDriverEmail() {
	    when(rideRepository.findTotalEarningsByDriverEmail("driver1@example.com"))
	            .thenReturn(Optional.of(new BigDecimal("320.00")));
	 
	    BigDecimal result = rideService.getTotalEarningsByDriverEmail("driver1@example.com");
	 
	    assertEquals(new BigDecimal("320.00"), result);
	    verify(rideRepository, times(1)).findTotalEarningsByDriverEmail("driver1@example.com");
	}
	 
	@Test
	void testGetTotalEarningsByDriverEmail_NoData() {
	    when(rideRepository.findTotalEarningsByDriverEmail("driver2@example.com"))
	            .thenReturn(Optional.empty());
	 
	    BigDecimal result = rideService.getTotalEarningsByDriverEmail("driver2@example.com");
	 
	    assertEquals(BigDecimal.ZERO, result);
	}
	
	@Test
	void testGetTotalSpendingByPassengerEmail() {
	    when(rideRepository.findTotalSpendingByPassengerEmail("passenger1@example.com"))
	            .thenReturn(Optional.of(new BigDecimal("145.25")));
	 
	    BigDecimal result = rideService.getTotalSpendingByPassengerEmail("passenger1@example.com");
	 
	    assertEquals(new BigDecimal("145.25"), result);
	    verify(rideRepository, times(1)).findTotalSpendingByPassengerEmail("passenger1@example.com");
	}
	 
	@Test
	void testGetTotalSpendingByPassengerEmail_NoData() {
	    when(rideRepository.findTotalSpendingByPassengerEmail("passenger2@example.com"))
	            .thenReturn(Optional.empty());
	 
	    BigDecimal result = rideService.getTotalSpendingByPassengerEmail("passenger2@example.com");
	 
	    assertEquals(BigDecimal.ZERO, result);
	}
}
