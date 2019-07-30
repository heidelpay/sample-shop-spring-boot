package com.heidelpay.samples.shop.model;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnore;


@Entity
@EntityListeners(AuditingEntityListener.class)
public class Customer {

	private String firstname, lastname;
	
	@Id
	@GeneratedValue
	@JsonIgnore
	private Long id;
	
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval=true)
	@JoinColumn(name = "customer", unique = false, nullable = true)
	private Set<PayoutReference> payouts = new HashSet<>();
	
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval=true)
	@JoinColumn(name = "customer", unique = false, nullable = true)
	private Set<PaymentReference> paymentReferences = new HashSet<>();
	
	
	
	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Set<PayoutReference> getPayouts() {
		return payouts;
	}

	public void setPayouts(Set<PayoutReference> references) {
		this.payouts.clear();
		if(references == null || references.size() == 0) {
			return;
		} else {
			for(PayoutReference reference : references) {
				reference.setCustomer(this);
				this.payouts.add(reference);
			}
		}
		
	}
	
	public void removePayouts(PayoutReference reference) {
		if(this.payouts != null) {
			this.payouts.remove(reference);
		}
	}
	
	public void addPayout(PayoutReference reference) {
		if(this.payouts == null) {
			this.payouts = new HashSet<>();
		}
		reference.setCustomer(this);
		this.payouts.add(reference);
	}

	public BigDecimal calculatePayoutSum() {
		BigDecimal sum = new BigDecimal(0);
		if(payouts != null && payouts.size() > 0) {
			for(PayoutReference payout : payouts) {
				sum = sum.add(payout.getAmount());
			}
		}
		return sum;
	}
	
	public void addPaymentReference(PaymentReference reference) {
		if(this.payouts == null) {
			this.payouts = new HashSet<>();
		}
		reference.setCustomer(this);
		this.paymentReferences.add(reference);
	}

	public Set<PaymentReference> getPaymentReferences() {
		return paymentReferences;
	}

	public void setPaymentReference(Set<PaymentReference> references) {
		this.paymentReferences.clear();
		if(references == null || references.size() == 0) {
			return;
		} else {
			for(PaymentReference reference : references) {
				reference.setCustomer(this);
				this.paymentReferences.add(reference);
			}
		}
		
	}
	public void removePaymentReference(PaymentReference reference) {
		if(this.paymentReferences != null) {
			this.paymentReferences.remove(reference);
		}
	}
}
