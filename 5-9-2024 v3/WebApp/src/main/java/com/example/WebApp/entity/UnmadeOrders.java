package com.example.WebApp.entity;

import jakarta.persistence.*;

import java.util.Date;
import java.util.List;

public class UnmadeOrders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Registration customer;

    @OneToMany(cascade = CascadeType.ALL)
    private List<CartItem> items;

    private Integer quantity;

    @Column(name = "total_price")
    private double totalPrice;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "order_date")
    private Date orderDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "delivery_date")
    private Date deliveryDate;


    @OneToOne(cascade = CascadeType.ALL)
    private DeliveryAddress deliveryAddress;

}
