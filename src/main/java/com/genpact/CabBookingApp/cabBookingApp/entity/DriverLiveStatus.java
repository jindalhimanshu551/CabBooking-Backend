package com.genpact.CabBookingApp.cabBookingApp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "driver_live_status")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverLiveStatus {
    @Id
    private Long driverId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "driver_id")
    private Driver driver;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    private LocalDateTime lastUpdated = LocalDateTime.now();
}
