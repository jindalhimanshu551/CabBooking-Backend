package com.genpact.CabBookingApp.cabBookingApp.service;

import com.genpact.CabBookingApp.cabBookingApp.entity.User;
import com.genpact.CabBookingApp.cabBookingApp.entity.Role;
import com.genpact.CabBookingApp.cabBookingApp.exception.NotFoundException;
import com.genpact.CabBookingApp.cabBookingApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    private final Random random = new Random();

    public static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public List<User> getAllUsers() {
        List<User> users = userRepository.findAll();
        if(users.isEmpty()) {
            throw new NotFoundException("No users found in the system");
        }
        return users;
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public User newSaveUser(User user) {
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        String generatedOtp = generateRandomOtp();
        user.setOtp(generatedOtp);
        return userRepository.save(user);
    }

    public List<User> getUsersByRole(Role role) {
        List<User> users = userRepository.findByRole(role);
        if(users.isEmpty()) {
            throw new NotFoundException("No user found with role"+ role);
        }
        return users;
    }

    public boolean deleteUser(Long id) {
        if(id == null|| id <= 0) {
            throw new IllegalArgumentException("Invalid ID for deletion: "+id);
        }
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;  // Deletion successful
        }
        throw new NotFoundException("User not found with ID:"+ id);  // User not found
    }

    public List<User> getAllPassengers() {
        return userRepository.findByRole(Role.PASSENGER);
    }

    public String generateRandomOtp() {
        int otp = 1000 + random.nextInt(9000);  // Generates a 4-digit random number
        return String.valueOf(otp);
    }
}
