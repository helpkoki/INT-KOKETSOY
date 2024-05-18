package com.example.WebApp.repository;

import com.example.WebApp.entity.DeliveryAddress;
import com.example.WebApp.entity.Registration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeliveryAddressRepository extends JpaRepository<DeliveryAddress,Long> {
    Optional<DeliveryAddress> findByStreetAndSuburbAndCityAndProvinceAndPostalCode(String street, String suburb, String city, String province, String postalCode);
    Optional<DeliveryAddress> findByUser(Registration user);

    Optional<DeliveryAddress> findByUserId(Long id);
}
