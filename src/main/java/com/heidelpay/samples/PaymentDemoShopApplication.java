package com.heidelpay.samples;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import com.heidelpay.samples.shop.dao.CustomerRepository;
import com.heidelpay.samples.shop.dao.ProductRepository;
import com.heidelpay.samples.shop.sample.SampleCustomerGenerator;
import com.heidelpay.samples.shop.sample.SampleProductGenerator;
import com.heidelpay.samples.shop.web.ShopInfo;



@SpringBootApplication
public class PaymentDemoShopApplication extends SpringBootServletInitializer implements CommandLineRunner {
	
	@Autowired ProductRepository productsDao;
	@Autowired CustomerRepository customerDao;
	@Autowired ShopInfo shopInfo;
	
	public static void main(String[] args) {
		SpringApplication.run(PaymentDemoShopApplication.class, args);
	}
	
	private void generateProducts() {
		if( productsDao.count() <= 0) {
			for(int i = 0; i < 6; i++) {
				productsDao.save(SampleProductGenerator.create(i, shopInfo.getId()));
			}
		}
	}

	private void generateCustomers() {
		if( customerDao.count() <= 0) {
			for(int i = 0; i < 3; i++) {
				customerDao.save(SampleCustomerGenerator.create());
			}
		}
	}
	
	@Override
	public void run(String... args) throws Exception {
		generateProducts();
		generateCustomers();
	}
}
