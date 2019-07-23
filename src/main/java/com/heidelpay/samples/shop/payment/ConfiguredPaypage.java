package com.heidelpay.samples.shop.payment;

import java.net.MalformedURLException;
import java.net.URL;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.heidelpay.payment.Paypage;

/**
 * 
 * {@code Paypage} configured out of the settings provided in the application.properties.
 *
 */
@Configuration
@ConfigurationProperties(prefix = "shop.heidelpay.paypage")
@Scope("prototype")
public class ConfiguredPaypage extends Paypage {

	private String returnUrlPath;
	
	@Override
	public String getLogoImage() {
		if(super.getLogoImage() != null) {
			if(super.getLogoImage().startsWith("http")) {
				return super.getLogoImage();
			}
			String imageUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
					.path(super.getLogoImage()).build().toString();
			if(imageUrl.contains("localhost")) {
				return null;
			} else {
				return imageUrl;
			}
			
		} else {
			return null;
		}
	}

	@Override
	public URL getReturnUrl() {
	
		try {
			return ServletUriComponentsBuilder.fromCurrentContextPath()
					.path(returnUrlPath).build().toUri().toURL();
		} catch (MalformedURLException e) {
			return super.getReturnUrl();
		}
	
	}

	public void setReturnUrlPath(String returnUrlPath) {
		this.returnUrlPath = returnUrlPath;
	}

	
}
