package com.genpact.CabBookingApp.cabBookingApp.repository;

import com.genpact.CabBookingApp.cabBookingApp.entity.Ride;
import com.genpact.CabBookingApp.cabBookingApp.entity.RideStatus;
import com.genpact.CabBookingApp.cabBookingApp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RideRepository extends JpaRepository<Ride, Long> {
    // Find all rides for a given passenger
    List<Ride> findByPassenger(User passenger);

    // Find a specific ride by rideId for a given passenger
    Optional<Ride> findByRideIdAndPassenger(Long rideId, User passenger);

    @Query("SELECT r FROM Ride r WHERE r.driver.driverId = :driverId AND r.status = 'COMPLETED' " +
            "AND r.completedAt BETWEEN :startOfMonth AND :endOfMonth")
    List<Ride> findCompletedRidesByDriverAndMonth(Long driverId, LocalDateTime startOfMonth, LocalDateTime endOfMonth);

    @Query("SELECT COALESCE(SUM(r.fare), 0) FROM Ride r WHERE r.driver.driverId = :driverId AND r.status = 'COMPLETED' " +
            "AND r.completedAt BETWEEN :startOfMonth AND :endOfMonth")
    BigDecimal getMonthlyEarningsByDriver(Long driverId, LocalDateTime startOfMonth, LocalDateTime endOfMonth);

    @Query("SELECT COALESCE(SUM(r.fare), 0) FROM Ride r WHERE r.driver.driverId = :driverId AND r.status = 'COMPLETED'")
    BigDecimal getTotalEarningsByDriver(Long driverId);

    @Query("SELECT r FROM Ride r WHERE r.driver.driverId = :driverId AND r.status = 'COMPLETED'")
    List<Ride> findAllCompletedRidesByDriver(Long driverId);

    @Query("SELECT SUM(r.fare) FROM Ride r WHERE r.status = 'COMPLETED'")
    BigDecimal getTotalEarnings(RideStatus completed);

    @Query("SELECT SUM(r.fare) FROM Ride r WHERE r.driver.user.email = :email AND r.status = 'COMPLETED'")
    Optional<BigDecimal> findTotalEarningsByDriverEmail(@Param("email") String email);
//
//    @Query("SELECT r.driver.user.email, SUM(r.fare) as totalEarnings" +
//    "FROM Ride r "+
//    "WHERE r.driver IS NOT NULL " +
//    "GROUP BY r.driver.user.email "+
//    "ORDER BY totalEarnings DESC")
//    List<Object[]> findTopEarningDrivers(Pageable pageable);

    @Query("SELECT SUM(r.fare) FROM Ride r WHERE r.passenger.email = :email AND r.status ='COMPLETED'")
    Optional<BigDecimal> findTotalSpendingByPassengerEmail(@Param("email") String email);

}
