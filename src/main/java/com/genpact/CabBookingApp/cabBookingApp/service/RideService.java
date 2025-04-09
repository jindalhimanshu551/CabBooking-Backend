package com.genpact.CabBookingApp.cabBookingApp.service;

import com.genpact.CabBookingApp.cabBookingApp.entity.Driver;
import com.genpact.CabBookingApp.cabBookingApp.entity.Ride;
import com.genpact.CabBookingApp.cabBookingApp.entity.RideStatus;
import com.genpact.CabBookingApp.cabBookingApp.entity.User;
import com.genpact.CabBookingApp.cabBookingApp.exception.*;
import com.genpact.CabBookingApp.cabBookingApp.repository.DriverRepository;
import com.genpact.CabBookingApp.cabBookingApp.repository.RideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Date;
import java.util.List;
import java.util.Optional;

//@Service
//public class RideService {
//    @Autowired
//    private RideRepository rideRepository;
//
//    public List<Ride> getAllRides() {
//        return rideRepository.findAll();
//    }
//
//    public List<Ride> getRidesByPassenger(User passenger) {
//        return rideRepository.findByPassenger(passenger);
//    }
//
//    public Ride getRideByIdAndUser(Long rideId, User user) {
//        Optional<Ride> rideOpt = rideRepository.findByRideIdAndPassenger(rideId, user);
//        return rideOpt.orElse(null); // or throw an exception if ride not found
//    }
//
//    public Ride saveRide(Ride ride) {
//        return rideRepository.save(ride);
//    }
//}

@Service
public class RideService {

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private TaskScheduler taskScheduler;

    // Existing methods
    public List<Ride> getAllRides() {
        List<Ride> rides = rideRepository.findAll();
        if(rides.isEmpty()) {
            throw new NotFoundException("Currently no ride exists");
        }
        return rides;
    }

    public List<Ride> getRidesByPassenger(User passenger) {
        if(passenger == null) {
            throw new NullException("Passenger cannot be null");
        }
        return rideRepository.findByPassenger(passenger);
    }

    public Ride getRideByIdAndUser(Long rideId, User user) {
        if(rideId == null || user == null) {
            throw new NullException("Ride Id and User must not be null");
        }
        return rideRepository.findByRideIdAndPassenger(rideId, user).orElse(null);
    }

    public int getRidesCompletedByDriverInMonth(Long driverId, int year, int month) {
        if(!driverRepository.existsById(driverId)) {
            throw new DriverNotFoundException("Driver not found with ID: "+driverId);
        }
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDateTime startOfMonth = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = yearMonth.atEndOfMonth().atTime(23, 59, 59);

        List<Ride> completedRides = rideRepository.findCompletedRidesByDriverAndMonth(driverId, startOfMonth, endOfMonth);
        return completedRides.size();
    }

    public BigDecimal getMonthlyEarnings(Long driverId, int year, int month) {
        if(!driverRepository.existsById(driverId)) {
            throw new DriverNotFoundException("Driver not found with ID: "+driverId);
        }
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDateTime startOfMonth = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = yearMonth.atEndOfMonth().atTime(23, 59, 59);

        return rideRepository.getMonthlyEarningsByDriver(driverId, startOfMonth, endOfMonth);
    }

    public BigDecimal getTotalEarnings(Long driverId) {
        if(!driverRepository.existsById(driverId)) {
            throw new DriverNotFoundException("Driver not found with ID: "+driverId);
        }
        return rideRepository.getTotalEarningsByDriver(driverId);
    }

    public List<Ride> getAllCompletedRides(Long driverId) {
        if(!driverRepository.existsById(driverId)) {
            throw new DriverNotFoundException("Driver not found with ID: "+driverId);
        }
        return rideRepository.findAllCompletedRidesByDriver(driverId);
    }


    public Ride saveRide(Ride ride) {

        if(ride == null) {
            throw new NullException("Ride cannot be null");
        }
        ride.setStatus(RideStatus.REQUESTED);
        ride.setRequestedAt(LocalDateTime.now());
        Ride savedRide = rideRepository.save(ride);

        // Fetch available drivers (ensure this returns your two drivers for testing)
        List<Driver> drivers = driverRepository.findByAvailabilityTrue();
        if(drivers == null || drivers.isEmpty()) {
            throw new AdminExceptions("No drivers available at the moment");
        }

        sendRequestToNextDriver(savedRide, drivers, 0);
        return savedRide;
    }

    private void sendRequestToNextDriver(Ride ride, List<Driver> drivers, int index) {
        // Stop if we've notified all drivers or if the ride status has changed
        if (index >= drivers.size()) {
            // All drivers have been notified and none accepted - cancel the ride
            Optional<Ride> currentRide = rideRepository.findById(ride.getRideId());
            if (currentRide.isPresent() && currentRide.get().getStatus() == RideStatus.REQUESTED) {
                Ride rideToCancel = currentRide.get();
                rideToCancel.setStatus(RideStatus.CANCELLED);
                rideRepository.save(rideToCancel);
                notificationService.notifyRideCancellation(rideToCancel);
                System.out.println("No drivers accepted ride " + ride.getRideId() + ". Ride cancelled.");
            }
            return;
        }

        if (ride.getStatus() != RideStatus.REQUESTED) {
            return;
        }

        Driver driver = drivers.get(index);
        System.out.println("Notifying driver: " + driver.getDriverId() + " for ride: " + ride.getRideId());
        notificationService.notifyDriver(driver, ride);

        // Schedule notification for the next driver after a 10-second delay
        taskScheduler.schedule(() -> {
            Optional<Ride> currentRide = rideRepository.findById(ride.getRideId());
            if (currentRide.isPresent() && currentRide.get().getStatus() == RideStatus.REQUESTED) {
                notificationService.notifyDriverTimeout(driver, ride);
                sendRequestToNextDriver(ride, drivers, index + 1);
            }
        }, new Date(System.currentTimeMillis() + 10000));
    }

    public boolean handleDriverResponse(Long rideId, Long driverId, boolean accepted) {
        Optional<Ride> rideObj = rideRepository.findById(rideId);
        if (!rideObj.isPresent()) {
            return false;
        }
        Ride ride = rideObj.get();
        if (ride.getStatus() != RideStatus.REQUESTED) {
            return false;
        }

        if (accepted) {
            ride.setStatus(RideStatus.ACCEPTED);
            Optional<Driver> driverObj = driverRepository.findById(driverId);
            driverObj.ifPresent(ride::setDriver);
            rideRepository.save(ride);
            System.out.println("Driver " + driverId + " accepted ride " + ride.getRideId());
            notificationService.notifyRideCancellation(ride);
            return true;
        } else {
            System.out.println("Driver " + driverId + " rejected ride " + ride.getRideId());
            // No need to do anything special here as the scheduler will automatically
            // notify the next driver or cancel the ride if no more drivers
            return true;
        }
    }

    public BigDecimal calculateTotalEarnings() {
        return rideRepository.getTotalEarnings(RideStatus.COMPLETED);
    }

    public BigDecimal getTotalEarningsByDriverEmail(String email) {
        if(email==null || email.isBlank()) {
            throw new InvalidIdException("Email cannot be blank");
        }
        return rideRepository.findTotalEarningsByDriverEmail(email).orElse(BigDecimal.ZERO);
    }

    public BigDecimal getTotalSpendingByPassengerEmail(String email) {
        if(email==null || email.isBlank()) {
            throw new InvalidIdException("Email cannot be blank");
        }
        return rideRepository.findTotalSpendingByPassengerEmail(email).orElse(BigDecimal.ZERO);
    }
}
