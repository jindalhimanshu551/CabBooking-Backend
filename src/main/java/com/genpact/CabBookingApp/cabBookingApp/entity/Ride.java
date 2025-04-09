package com.genpact.CabBookingApp.cabBookingApp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "rides")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ride {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rideId;

    @ManyToOne
    @JoinColumn(name = "passenger_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User passenger;

    @ManyToOne
    @JoinColumn(name = "driver_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Driver driver;

    @Column(nullable = false)
    private String pickupLocation;

    @Column(nullable = false)
    private String dropoffLocation;

    @Column(precision = 10, scale = 2)
    private BigDecimal fare;

    @Enumerated(EnumType.STRING)
    private VehicleType requestedVehicle;

    @Enumerated(EnumType.STRING)
    private RideStatus status = RideStatus.REQUESTED;

    private LocalDateTime requestedAt = LocalDateTime.now();
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
}

