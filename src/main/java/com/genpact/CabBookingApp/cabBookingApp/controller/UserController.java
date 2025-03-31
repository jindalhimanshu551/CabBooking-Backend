package com.genpact.CabBookingApp.cabBookingApp.controller;

import com.genpact.CabBookingApp.cabBookingApp.entity.User;
import com.genpact.CabBookingApp.cabBookingApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @DeleteMapping
    public ResponseEntity<Void> deleteUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User userObj = userService.getUserByEmail(email)
                .orElse(null);
        if (userObj != null) {
            userService.deleteUser(userObj.getUserId());
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PutMapping
    public ResponseEntity<String> updateUser(@RequestBody User user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User userObj = userService.getUserByEmail(email)
                .orElse(null);

        // Check if user exists with the authenticated email
        if (userObj == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No user exists with this email.");
        }

        // Check if the email in the request body is the same as the authenticated user's email
        if (!Objects.equals(user.getEmail(), userObj.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("This email is already in use.");
        }

        userObj.setFullName(user.getFullName());
        userObj.setEmail(user.getEmail());
        userObj.setPasswordHash(user.getPasswordHash());
        userObj.setPhoneNumber(user.getPhoneNumber());
        userObj.setRole(user.getRole());
        userService.newSaveUser(userObj);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
