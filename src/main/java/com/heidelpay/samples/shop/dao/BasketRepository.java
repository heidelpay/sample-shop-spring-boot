package com.heidelpay.samples.shop.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.heidelpay.samples.shop.model.Basket;

public interface BasketRepository extends JpaRepository<Basket, Long>{

	List<Basket> findAllByOrderByCreationDateAsc();
	
}
