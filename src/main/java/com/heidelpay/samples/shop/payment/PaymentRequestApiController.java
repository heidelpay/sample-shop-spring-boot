package com.heidelpay.samples.shop.payment;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Currency;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.heidelpay.payment.Charge;
import com.heidelpay.payment.PaymentError;
import com.heidelpay.payment.PaymentException;
import com.heidelpay.payment.communication.HttpCommunicationException;
import com.heidelpay.samples.shop.model.Basket;
import com.heidelpay.samples.shop.model.PaymentDetails;

/**
 * Controller connecting the browsers Payment-Request-Api support to heidelpay as processor.
 * @author Dirk
 *
 */
@Controller
@RequestMapping("/payment/native")
public class PaymentRequestApiController extends AbstractPaymentController {

	@Value("${shop.heidelpay.callback}")
	protected String callbackUrl;
	
	/**
	 * Processes the Payment given from the browsers payment-request-api support as {@code PaymentDetails}.
	 * @param details the {@code PAymentDetail} as defined by the payment-request-api
	 * @param response the {@code HttpServletResponse}
	 * @return a message for the payment-request-api
	 * @throws HttpCommunicationException
	 * @throws MalformedURLException
	 */
	@PostMapping(path = "pay", consumes = "application/json")
	public @ResponseBody String pay(@RequestBody PaymentDetails details, HttpServletResponse response)
			throws HttpCommunicationException, MalformedURLException {

		try {
			Charge charge = heidelpay.charge(details.getTotal(), Currency.getInstance(details.getCurrency()),
					details.getPaymentMethod(), createCallbackUrl(), details.getCustomer());

			Basket basket = basketHolder.get();
			basket.setPaymentId(charge.getPaymentId());
			basket.setState(charge.getPayment().getPaymentState().toString());
			basketRepository.save(basket);
			basketHolder.reset();
			return "{\"success\":true, \"basketId\":\"" + basket.getId() + "\"}";

		} catch (PaymentException ex) {
			String message = "";
			if (ex.getPaymentErrorList() != null) {
				for (PaymentError err : ex.getPaymentErrorList()) {
					message = message + err.getMerchantMessage() + " ";
				}
			}
			response.setStatus(400);
			return "{\"success\":false, \"message\":\"" + message + "\"}";
		}
	}

	private URL createCallbackUrl() throws MalformedURLException {
		return new URL(callbackUrl);
	}


}
