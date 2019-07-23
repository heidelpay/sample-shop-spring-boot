package com.heidelpay.samples.shop.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class PayoutReference {


	@CreatedDate
	@Temporal(TemporalType.TIMESTAMP)
	protected Date creationDate;
	
	@Id
	@GeneratedValue
	@JsonIgnore
	private Long id;
	
	
	private String paymentType, type, detail, reference, currency;

	private BigDecimal amount;

	@ManyToOne
	@JoinColumn(name = "customer", unique = false, nullable = true)
	private Customer customer;
	
	public Long getId() {
		return id;
	}

	
	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	
	
	public String getPaymentType() {
		return paymentType;
	}


	public void setPaymentType(String paymentTypeReference) {
		this.paymentType = paymentTypeReference;
	}


	public BigDecimal getAmount() {
		return amount;
	}


	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}


	public Date getCreationDate() {
		return creationDate;
	}


	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}


	public Customer getCustomer() {
		return customer;
	}


	public void setCustomer(Customer customer) {
		this.customer = customer;
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
	
	
}
