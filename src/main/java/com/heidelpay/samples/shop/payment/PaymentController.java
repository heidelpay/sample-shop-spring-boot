package com.heidelpay.samples.shop.payment;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.heidelpay.payment.Charge;
import com.heidelpay.payment.communication.HttpCommunicationException;
import com.heidelpay.samples.shop.model.Basket;

/**
 * Controller handling any 'native' payments, integrating the heidelpay api.
 */
@Controller
@RequestMapping("/payment")
public class PaymentController extends AbstractPaymentController{

	/**
	 * Show the credit-card form.
	 * @return view rendering the credit card form
	 */
	@RequestMapping("creditcard")
	public ModelAndView showCreditCard() {
		Map<String, Object> model = new HashMap<>();
		model.put("basket", basketHolder.get());
		model.put("publicKey", publicKey);
		return new ModelAndView("payment/creditcard", model);
	}
	
	/**
	 * 
	 * @return view rendering a SEPA direct debit form.
	 */
	@RequestMapping("sepa")
	public ModelAndView showSepa() {
		Map<String, Object> model = new HashMap<>();
		model.put("basket", basketHolder.get());
		model.put("publicKey", publicKey);
		return new ModelAndView("payment/sepa", model);
	}
	
	/**
	 * executes the payment by charging the given paymentType.
	 * @param paymentType reference to the paymentType created at heidelpay
	 * @return redirects to the success page in case of direct payments, or to the payment-method in case of redirect payments.
	 * @throws HttpCommunicationException - thrown by the payment
	 * @throws MalformedURLException
	 */
	@PostMapping("charge")
	public RedirectView charge(@RequestParam("paymentType") String paymentType)
			throws HttpCommunicationException, MalformedURLException {

		Basket basket = basketHolder.get();
		basketRepository.save(basket);
		
		Charge charge = heidelpay
				.charge(createChargeForBasket(basket, paymentType, new URL(returnUrl(basket.getId())), null));
	
		// when having 3DS or redirect based APM's the redirect Url is returned with the charge
		if(charge.getRedirectUrl() != null) {
			basket.setPaymentId(charge.getPaymentId());
			basketRepository.save(basket);
			basketHolder.reset();
			return new RedirectView(charge.getRedirectUrl().toString(), false);
		} else {
			basket.setPaymentId(charge.getPaymentId());
			basket.setState(charge.getPayment().getPaymentState().toString());
			basketRepository.save(basket);
			basketHolder.reset();
			return new RedirectView("/payment/success/" + basket.getId());
		}
		
	}

	/**
	 * Not all parameters are supported by the {@code Heidelpay} facade. However,
	 * you can use a handcrafted {@code Charge} created by this method.
	 * 
	 * @param basket
	 *            the sample basket to be associated with the
	 *            {@code Charge}/{@code Payment} respectively.
	 * @param paymentType
	 *            the payment type to be used
	 * @param returnUrl
	 *            the url to be returned after the charge call
	 * @param customerId
	 *            id of the customer created at the api (e.g by using
	 *            heidelpay.createCustomer())
	 * @return an {@code Charge} associated with the given basket
	 */
	private Charge createChargeForBasket(Basket basket, String paymentType, URL returnUrl, String customerID) {
		Charge charge = new Charge();

		// reference the basket/order
		charge.setOrderId("" + basket.getId());

		// charge the complete basket
		charge.setAmount(basket.calculateSum());

		charge.setCurrency(Currency.getInstance(basket.getCurrency()));
		
		charge.setTypeId(paymentType);
		charge.setReturnUrl(returnUrl);
		
		if(customerID != null) {
			charge.setCustomerId(customerID);
		}
		
		return charge;
	}

}
