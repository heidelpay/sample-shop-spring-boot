package com.heidelpay.samples.shop.sample;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.heidelpay.samples.shop.model.Product;


public class SampleProductGenerator {

	private static final List<String> titles = Arrays.asList(new String[] {"heidelpay Bluetooth Lautsprecher", "heidelpay Powerbank", "heidelpay Kopfhörer", 
			"heidelpay USB-Stick", "heidelpay T-Shirt", "heidelpay Kaffeetasse"});
	private static final List<BigDecimal> prices = Arrays.asList( new BigDecimal[] {
			new BigDecimal(19.99).setScale(2,  BigDecimal.ROUND_HALF_EVEN), 
			new BigDecimal(49.99).setScale(2,  BigDecimal.ROUND_HALF_EVEN), 
			new BigDecimal(9.99).setScale(2,  BigDecimal.ROUND_HALF_EVEN), 
			new BigDecimal(4.99).setScale(2,  BigDecimal.ROUND_HALF_EVEN),
			new BigDecimal(9.99).setScale(2,  BigDecimal.ROUND_HALF_EVEN),
			new BigDecimal(7.99).setScale(2,  BigDecimal.ROUND_HALF_EVEN)});
	
	private static final List<String> descriptions = Arrays.asList(new String[] {"Lorem ipsum dolor sit amet, consectetur adipisicing elit. Amet numquam aspernatur!", "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam", "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam"});
	
	
	public static Product create(int index, String shop) {
		
		Collections.shuffle(descriptions);
		
		Product product = new Product();
		product.setDescription(descriptions.get(0));
		product.setTitle(titles.get(index));
		product.setPrice(prices.get(index));
		product.setImageUrl(createImageUrl("product" + (index+1) + ".jpg", shop));
		product.setTeaserUrl(createImageUrl("teaser" + (index+1) + ".jpg", shop));
		return product;
	}
	
	private static String createImageUrl(String filename, String shop) {
		return "/images/shops/" + shop + "/products/" + filename;
	}
	
}
