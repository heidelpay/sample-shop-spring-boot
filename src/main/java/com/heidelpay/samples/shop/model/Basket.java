package com.heidelpay.samples.shop.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.heidelpay.samples.shop.dao.RandomIntIdGenerator;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class Basket {

	@Id
    @GeneratedValue(generator = RandomIntIdGenerator.name)
    @GenericGenerator(name = RandomIntIdGenerator.name, strategy = "com.heidelpay.samples.shop.dao.RandomIntIdGenerator")
	private Long id;
	
	public String currency;

	@CreatedDate
	@Temporal(TemporalType.TIMESTAMP)
	protected Date creationDate;
	
	private String paymentId;
	
	private String state;
	
	
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval=true)
	@JoinColumn(name = "basket", unique = false, nullable = true)
	private List<BasketItem> items = new ArrayList<>();
	
	public BigDecimal calculateSum() {
		BigDecimal sum = new BigDecimal(0.0);
		for(BasketItem item : items) {
			sum = sum.add( item.getTotal() );
		}
		return sum.setScale(2, BigDecimal.ROUND_HALF_EVEN);
	}
	
	
	
	public Long getId() {
		return id;
	}



	public void setId(Long id) {
		this.id = id;
	}



	public String getCurrency() {
		return currency;
	}



	public void setCurrency(String currency) {
		this.currency = currency;
	}



	public Date getCreationDate() {
		return creationDate;
	}



	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}



	public List<BasketItem> getItems() {
		return items;
	}


	public String getState() {
		return state;
	}



	public void setState(String state) {
		this.state = state;
	}



	public void setItems(List<BasketItem> items) {
		this.items.clear();
		if(items == null || items.size() == 0) {
			return;
		} else {
			for(BasketItem item : items) {
				item.setBasket(this);
				this.items.add(item);
			}
		}
		
	}
	
	public void removeItem(BasketItem item) {
		if(this.items != null) {
			this.items.remove(item);
		}
	}
	
	public void addItem(BasketItem item) {
		if(this.items == null) {
			this.items = new ArrayList<>();
		}
		item.setBasket(this);
		this.items.add(item);
	}

	public String getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}
	
	
	
}
