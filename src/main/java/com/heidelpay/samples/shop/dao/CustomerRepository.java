package com.heidelpay.samples.shop.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.heidelpay.samples.shop.model.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long>{

}
