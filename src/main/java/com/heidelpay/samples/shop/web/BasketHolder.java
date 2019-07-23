package com.heidelpay.samples.shop.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import com.heidelpay.samples.shop.model.Basket;
import com.heidelpay.samples.shop.model.BasketItem;
import com.heidelpay.samples.shop.model.Product;

/**
 * Utility for accessing the session-scoped basket, especially within views.
 *
 */
@Component
@SessionScope
public class BasketHolder {

	@Value("${shop.defaults.currency}")
	private String currency;
	
	private Basket basket;
	
	public void add(Product product, int amount) {
		if(basket == null) {
			initBasket();
		}
		basket.addItem(toBasketItem(product, amount));
	}
	
	private BasketItem toBasketItem(Product product, int amount) {
		return new BasketItem(product.getTitle(), product.getPrice(), product.getId(), amount);
	}
	
	public Basket get() {
		if(basket == null) {
			initBasket();
		}
		return basket;
	}
	
	public void reset() {
		initBasket();
	}
	
	private void initBasket() {
		basket = new Basket();
		basket.setCurrency(currency);
	}
}
