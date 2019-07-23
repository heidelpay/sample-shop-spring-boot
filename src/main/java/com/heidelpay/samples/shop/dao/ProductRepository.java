package com.heidelpay.samples.shop.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.heidelpay.samples.shop.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long>{

}
