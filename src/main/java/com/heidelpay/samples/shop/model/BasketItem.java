package com.heidelpay.samples.shop.model;

import java.beans.Transient;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class BasketItem {

	

	@Id
	@GeneratedValue
	@JsonIgnore
	private Long id;

	private String title;
	private int amount;
	private BigDecimal price;
	private String cancelId;
	private long productRef;
	
	public BasketItem() {}
	
	public BasketItem(String title, BigDecimal price, long productRef, int amount) {
		this.title = title;
		this.price = price;
		this.amount = amount;
	}
	
	@ManyToOne
	@JoinColumn(name = "basket", unique = false, nullable = true)
	private Basket basket;

	
	
	public Long getId() {
		return id;
	}

	public Basket getBasket() {
		return basket;
	}

	public void setBasket(Basket basket) {
		this.basket = basket;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price.setScale(2, BigDecimal.ROUND_HALF_EVEN);
	}

	@Transient
	public BigDecimal getTotal() {

		return this.price.multiply(new BigDecimal(this.amount)).setScale(2, BigDecimal.ROUND_HALF_EVEN);
	}

	public String getCancelId() {
		return cancelId;
	}

	public void setCancelId(String cancelId) {
		this.cancelId = cancelId;
	}

	public long getProductRef() {
		return productRef;
	}

	public void setProductRef(long productRef) {
		this.productRef = productRef;
	}

	
	
}
