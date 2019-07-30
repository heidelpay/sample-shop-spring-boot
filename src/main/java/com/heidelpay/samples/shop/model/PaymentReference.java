package com.heidelpay.samples.shop.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.data.annotation.CreatedDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Reference/Registration of a payment-instrument/type.
 */
@Entity
public class PaymentReference {

	
	@CreatedDate
	@Temporal(TemporalType.TIMESTAMP)
	protected Date creationDate;
	
	@Id
	@GeneratedValue
	@JsonIgnore
	private Long id;
	
	private String reference;
	private String type;
	private String details;

	private boolean registered = false;
	
	@ManyToOne
	@JoinColumn(name = "customer", unique = false, nullable = true)
	private Customer customer;
	
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
	public String getDetails() {
		return details;
	}
	public void setDetails(String details) {
		this.details = details;
	}
	public Date getCreationDate() {
		return creationDate;
	}
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	public boolean isRegistered() {
		return registered;
	}
	public void setRegistered(boolean registered) {
		this.registered = registered;
	}
	public Long getId() {
		return id;
	}
	
	
	
}
