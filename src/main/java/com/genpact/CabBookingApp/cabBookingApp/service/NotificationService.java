package com.genpact.CabBookingApp.cabBookingApp.service;

import com.genpact.CabBookingApp.cabBookingApp.entity.Driver;
import com.genpact.CabBookingApp.cabBookingApp.entity.Ride;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


@Service
public class NotificationService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void notifyDriver(Driver driver, Ride ride) {
        // Use a consistent, unique identifier for the driver
        String username = driver.getUser().getEmail(); // Using email as a unique identifier

        System.out.println("Sending ride request to driver: " + username);

        // The destination "/queue/ride-notifications" is prefixed with "/user/" by Spring automatically
        messagingTemplate.convertAndSendToUser(
                username,
                "/queue/ride-notifications",
                ride
        );
    }

    public void notifyDriverTimeout(Driver driver, Ride ride) {
        String username = driver.getUser().getEmail();

        System.out.println("Sending timeout notification to driver: " + username);

        // Create a timeout notification object
        Map<String, Object> timeoutNotification = new HashMap<>();
        timeoutNotification.put("type", "TIMEOUT");
        timeoutNotification.put("rideId", ride.getRideId());
        timeoutNotification.put("message", "Time to respond has expired");

        // Send to the driver's personal queue
        messagingTemplate.convertAndSendToUser(
                username,
                "/queue/ride-timeouts",
                timeoutNotification
        );
    }

    public void notifyRideCancellation(Ride ride) {
        messagingTemplate.convertAndSend("/topic/ride-cancellations", ride);
    }
}