package com.example.WebApp.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
public class DeliveryAddress implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String street;
    private String suburb;
    private String city;
    private String province;
    private String postalCode;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Registration user;
    // Constructors
    public DeliveryAddress() {
    }

    public DeliveryAddress(String street, String suburb, String city, String province, String postalCode, Registration user) {
        this.street = street;
        this.suburb = suburb;
        this.city = city;
        this.province = province;
        this.postalCode = postalCode;
        this.user = user;
    }

    public DeliveryAddress(String street, String suburb, String city, String province, String postalCode) {
        this.street = street;
        this.suburb = suburb;
        this.city = city;
        this.province = province;
        this.postalCode = postalCode;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getSuburb() {
        return suburb;
    }

    public void setSuburb(String suburb) {
        this.suburb = suburb;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public Registration getUser() {
        return user;
    }

    public void setUser(Registration user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DeliveryAddress)) return false;
        DeliveryAddress that = (DeliveryAddress) o;
        return Objects.equals(getStreet(), that.getStreet()) &&
                Objects.equals(getSuburb(), that.getSuburb()) &&
                Objects.equals(getCity(), that.getCity()) &&
                Objects.equals(getProvince(), that.getProvince()) &&
                Objects.equals(getPostalCode(), that.getPostalCode()) &&
                Objects.equals(getUser(), that.getUser());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getStreet(), getSuburb(), getCity(), getProvince(), getPostalCode(), getUser());
    }
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
    public boolean hasUser() {
        return user != null;
    }
}