package com.heidelpay.samples.shop.payment;

import java.util.Currency;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.heidelpay.payment.Payment;
import com.heidelpay.payment.PaymentException;
import com.heidelpay.payment.Paypage;
import com.heidelpay.payment.communication.HttpCommunicationException;
import com.heidelpay.samples.shop.model.Basket;

/**
 * Controller handling pay-page payments.
 *
 */
@Controller
@RequestMapping("/payment/paypage")
public class PaypageController extends AbstractPaymentController {

	@Autowired
	ConfiguredPaypage paypage;

	/**
	 * shows the paypage as a modal dialog.
	 * @return rendering the basket view with a modal embedded paypage.
	 * @throws PaymentException
	 * @throws HttpCommunicationException
	 */
	@GetMapping("")
	public ModelAndView paypage() throws PaymentException, HttpCommunicationException {
		Map<String, Object> model = new HashMap<>();
		Paypage page = heidelpay.paypage(createPaypage(basketHolder.get()));
		applyPaymentOnBasket(page.getPaymentId());
		model.put("paypage", page);
		return new ModelAndView("shop/basket", model);

	}

	/**
	 * handles a sucessful payment.
	 * @return renders the success view.
	 * @throws HttpCommunicationException
	 */
	@GetMapping("success")
	public RedirectView handlePayPage() throws HttpCommunicationException {
		Basket basket = basketRepository.getOne(basketHolder.get().getId());
		Payment payment = heidelpay.fetchPayment(basket.getPaymentId());

		basket.setState(payment.getPaymentState().toString());
		basketRepository.save(basket);

		if (payment.getPaymentState() != null) {
			basketHolder.reset();
			return new RedirectView("/payment/success/" + basket.getId());

		} else {
			return new RedirectView("/basket");
		}

	}

	private Paypage createPaypage(Basket basket) {

		paypage.setAmount(basket.calculateSum());
		paypage.setCurrency(Currency.getInstance(basket.getCurrency()));

		return paypage;
	}
}
