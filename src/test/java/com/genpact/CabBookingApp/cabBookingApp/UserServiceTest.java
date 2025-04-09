package com.genpact.CabBookingApp.cabBookingApp;

import com.genpact.CabBookingApp.cabBookingApp.entity.Role;
import com.genpact.CabBookingApp.cabBookingApp.entity.User;
import com.genpact.CabBookingApp.cabBookingApp.repository.UserRepository;
import com.genpact.CabBookingApp.cabBookingApp.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

	@Mock
	private UserRepository userRepository;
	
	@InjectMocks
	private UserService userService;
	
	private User testUser;
	
	@BeforeEach
	void setUp() {
		testUser = User.builder()
				.userId(1L)
				.fullName("Testing User")
				.email("test@example.com")
				.phoneNumber("1234567789")
				.passwordHash("password")
				.role(Role.PASSENGER)
				.build();
	}
	
	@Test
	void testGetAllUsers() {
		when(userRepository.findAll()).thenReturn(Arrays.asList(new User(), new User(), new User()));
		List<User> result = userService.getAllUsers();
		assertEquals(3, result.size());
		verify(userRepository, times(1)).findAll();
	}
	
	@Test
	void testGetUserById() {
		when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
		Optional<User> result = userService.getUserById(1L);
		assertTrue(result.isPresent());
		assertEquals(1L, result.get().getUserId());
	}
	
	@Test
	void testGetUserByEmail() {
		when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
		Optional<User> result = userService.getUserByEmail("test@example.com");
		assertTrue(result.isPresent());
		assertEquals("test@example.com", result.get().getEmail());
	}
	
	@Test
	void testNewSaveUser() {
		when(userRepository.save(any(User.class))).thenReturn(testUser);
		User encryptedUser = User.builder()
				.userId(2L)
				.fullName("Encrypted User")
				.email("secure@example.com")
				.phoneNumber("9876554321")
				.passwordHash("securepassword")
				.role(Role.PASSENGER)
				.build();
		
		User result = userService.newSaveUser(encryptedUser);
		assertNotNull(result);
		verify(userRepository, times(1)).save(any(User.class));	
	}
	
	@Test
	void TestGetUserByRole() {
		when(userRepository.findByRole(Role.DRIVER)).thenReturn(Collections.singletonList(testUser));
		List<User> result = userService.getUsersByRole(Role.DRIVER);
		assertEquals(1, result.size());
	}
	
	@Test
	void testGetUsersByRoleWithMultipleDrivers() {
		User driver1 = User.builder()
				.userId(1L)
				.fullName("Driver One")
				.email("driver1@gmail.com")
				.phoneNumber("1231231231")
				.passwordHash("hash1")
				.role(Role.DRIVER)
				.build();
		
		User driver2 = User.builder()
				.userId(2L)
				.fullName("Driver Two")
				.email("driver2@gmail.com")
				.phoneNumber("1221231231")
				.passwordHash("hash2")
				.role(Role.DRIVER)
				.build();
		
		List<User> drivers = Arrays.asList(driver1, driver2);
		when(userRepository.findByRole(Role.DRIVER)).thenReturn(drivers);
		List<User> result = userService.getUsersByRole(Role.DRIVER);
		
		assertEquals(2, result.size());
		assertEquals("Driver One", result.get(0).getFullName());
		assertEquals("Driver Two", result.get(1).getFullName());
		verify(userRepository, times(1)).findByRole(Role.DRIVER);
	}
	
	@Test
	void testDeleteUserWhenExists() {
		when(userRepository.existsById(1L)).thenReturn(true);
		boolean result = userService.deleteUser(1L);
		assertTrue(result);
		verify(userRepository).deleteById(1L);
		}
	
	@Test
	void testDeleteWhenNotExists() {
		when(userRepository.existsById(2L)).thenReturn(false);
		boolean result = userService.deleteUser(2L);
		assertFalse(result);
		verify(userRepository, never()).deleteById(2L);
		}
	
	@Test
	void testGetAllPassengers() {
		when(userRepository.findByRole(Role.PASSENGER)).thenReturn(List.of(testUser));
		List<User> result = userService.getAllPassengers();
		assertEquals(1, result.size());
		assertEquals(Role.PASSENGER, result.get(0).getRole());
		}
	
	@Test
	void testGetAllPassengersWithMultipleUsers() {
		User passenger1 = User.builder()
				.userId(1L)
				.fullName("Passenger One")
				.email("passenger1@example.com")
				.phoneNumber("1233434311")
				.passwordHash("hashed1")
				.role(Role.PASSENGER)
				.build();
		
		User passenger2 = User.builder()
				.userId(2L)
				.fullName("Passenger Two")
				.email("passenger2@example.com")
				.phoneNumber("1233334311")
				.passwordHash("hashed2")
				.role(Role.PASSENGER)
				.build();
		
		List<User> passengers = List.of(passenger1, passenger2);
		
		when(userRepository.findByRole(Role.PASSENGER)).thenReturn(passengers);
		List<User> result = userService.getAllPassengers();
		
		assertEquals(2, result.size());
		assertEquals("Passenger One", result.get(0).getFullName());
		assertEquals("Passenger Two", result.get(1).getFullName());
		assertEquals(Role.PASSENGER, result.get(0).getRole());
		assertEquals(Role.PASSENGER, result.get(1).getRole());
		
		verify(userRepository, times(1)).findByRole(Role.PASSENGER);
		}
	}
