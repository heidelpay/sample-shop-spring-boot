package com.heidelpay.samples.shop.payment;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.heidelpay.payment.Heidelpay;
import com.heidelpay.payment.Payment;
import com.heidelpay.payment.communication.HttpCommunicationException;
import com.heidelpay.samples.shop.dao.BasketRepository;
import com.heidelpay.samples.shop.model.Basket;
import com.heidelpay.samples.shop.web.BasketHolder;

/**
 * Base implementation for Controllers integrating payment.
 *
 */
@Controller
public class AbstractPaymentController {
	
	@Value("${shop.heidelpay.key.public}")
	protected String publicKey;
	
	@Autowired 
	protected BasketHolder basketHolder;
	
	@Autowired
	protected BasketRepository basketRepository;
	

	@Autowired 
	protected Heidelpay heidelpay;
	
	
	protected void applyPaymentOnBasket(String paymentId) {
		Basket basket = basketHolder.get();
		basket.setPaymentId(paymentId);
		basketRepository.save(basket);
	}
	
	/**
	 * Sets the state of the (successful) payment and renders the success page for the user.
	 * @param id basket-id
	 * @return the success view
	 * @throws HttpCommunicationException thrown by the payment
	 */
	@GetMapping("/payment/success/{id}") ModelAndView success(@PathVariable Long id) throws HttpCommunicationException {
		
		Basket basket = basketRepository.getOne(id);
		Payment payment = heidelpay.fetchPayment(basket.getPaymentId());
		basket.setState(payment.getPaymentState().toString());
		basketRepository.save(basket);
		
		return createSuccessView(basket, payment);
	}

	protected ModelAndView createSuccessView(Basket basket, Payment payment) throws HttpCommunicationException {
		Map<String, Object> model = new HashMap<>();
		model.put("basket", basket);
		model.put("payment", payment);
		if(payment.getCustomerId() != null) {
			model.put("customer", heidelpay.fetchCustomer(payment.getCustomerId()));
		}

		return new ModelAndView("shop/success", model);
	}
	
	
	/**
	 * redirect based payments, such as paypal, sofort or credit-card with 3d-secure, needs a return url the user is redirected to.
	 * 
	 * @param basketId id of the basket
	 * @return the url the user gets redirected to when the external payment provider has finished.
	 */
	protected String returnUrl(Long basketId) {

		String url = ServletUriComponentsBuilder.fromCurrentContextPath()
				.path("/payment/success/" + basketId).build().toString();
		
		// payment only allows https....
		if(url.contains("http://localhost")) {
			url = url.replace("http://local", "https://local");
		}
		
		return url;	
	}
	
}
