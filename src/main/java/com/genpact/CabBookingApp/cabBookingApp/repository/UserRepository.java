package com.genpact.CabBookingApp.cabBookingApp.repository;

import com.genpact.CabBookingApp.cabBookingApp.entity.Role;
import com.genpact.CabBookingApp.cabBookingApp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByPhoneNumber(String phoneNumber);
    List<User> findByRole(Role role);

}
