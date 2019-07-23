package com.heidelpay.samples.shop.model;

import java.math.BigDecimal;

/**
 * Exchange Object for Payment-Request-Api integration.
 *
 */
public class PaymentDetails {

	private String customer, paymentMethod;
	private BigDecimal total;
	private String currency;
	public String getCustomer() {
		return customer;
	}
	public void setCustomer(String customer) {
		this.customer = customer;
	}
	public String getPaymentMethod() {
		return paymentMethod;
	}
	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}
	public BigDecimal getTotal() {
		return total;
	}
	public void setTotal(BigDecimal total) {
		this.total = total;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	
	
	
}
