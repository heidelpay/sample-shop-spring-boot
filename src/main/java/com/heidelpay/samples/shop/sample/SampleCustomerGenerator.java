package com.heidelpay.samples.shop.sample;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.heidelpay.samples.shop.model.Customer;


public class SampleCustomerGenerator {

	private static List<String> firstnames = Arrays.asList(new String[] {"Mary", "Peter", "Bob"}); 
	
	private static List<String> lastnames = Arrays.asList(new String[] {"Foo", "Bar", "Smith"});

	public static  Customer create() {
		Customer customer = new Customer();
		Collections.shuffle(firstnames);
		Collections.shuffle(lastnames);
		customer.setFirstname( firstnames.get(0) );
		customer.setLastname(lastnames.get(0));
		return customer;
	}
}
