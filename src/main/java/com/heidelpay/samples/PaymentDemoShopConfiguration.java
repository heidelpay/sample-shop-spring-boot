package com.heidelpay.samples;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.heidelpay.payment.Heidelpay;

import nz.net.ultraq.thymeleaf.LayoutDialect;

@Configuration
@EnableAutoConfiguration
@EnableJpaAuditing
public class PaymentDemoShopConfiguration implements WebMvcConfigurer {

	@Value("${shop.heidelpay.key.private}")
	private String privateKey;
	
	@Bean
	public LayoutDialect layoutDialect() {
	    return new LayoutDialect();
	}
	
	@Bean
	public Heidelpay heidelpay() {
		return new Heidelpay(privateKey);
	}
	
}
