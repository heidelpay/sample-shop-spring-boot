package com.heidelpay.samples.shop.web;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.heidelpay.samples.shop.payment.AbstractPaymentController;

/**
 * Controller handling the basket.
 *
 */
@Controller
@RequestMapping("/basket")
public class BasketController extends AbstractPaymentController{

	/**
	 * shows the basket.
	 * @param card true if only card should be provided within the payment-request-api
	 * @param own if only the own, flexipay invoice, payment should be shown in the payment-request-api
	 * @return renders the basket view.
	 */
	@GetMapping("")
	public ModelAndView show(@RequestParam(required=false, name="card") Boolean card, 
			@RequestParam(required=false, name="own") Boolean own) {
		Map<String, Object> model = new HashMap<>();
		model.put("card", card);
		model.put("own", own);
		return new ModelAndView("shop/basket", model);
	}

	
	
}
