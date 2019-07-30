package com.heidelpay.samples.shop.payment;

import java.math.BigDecimal;
import java.util.Currency;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.heidelpay.payment.Heidelpay;
import com.heidelpay.payment.Payout;
import com.heidelpay.payment.communication.HttpCommunicationException;
import com.heidelpay.payment.paymenttypes.PaymentType;
import com.heidelpay.payment.paymenttypes.SepaDirectDebit;
import com.heidelpay.samples.shop.dao.CustomerRepository;
import com.heidelpay.samples.shop.model.Customer;
import com.heidelpay.samples.shop.model.PayoutReference;

/**
 * Integrating payouts {@link https://docs.heidelpay.com/docs/payouts}
 *
 */
@Controller
@RequestMapping("payouts")
public class PayoutController {

	@Value("${shop.heidelpay.key.public}")
	protected String publicKey;

	@Value("${shop.heidelpay.api}")
	protected String api;

	@Autowired
	private CustomerRepository customerDao;

	@Autowired 
	protected Heidelpay heidelpay;
	
	/**
	 * renders a list of customers and their {@code PayoutReference} statistics.
	 * @return
	 */
	@RequestMapping("")
	public ModelAndView index() {
		ModelAndView modelAndView = new ModelAndView("payouts/index.html");
		modelAndView.addObject("customers", customerDao.findAll());
		return modelAndView;
	}

	/**
	 * Renders the view for creating a specific payout. The ui illustrates three different approaches of integrating with the heidelpay Payment API:
	 * <ul>
	 * <li>Use of the heidelpay UI-COmponents: <i>noHeidelpayUI=false</i></li>
	 * <li>Transfer the IBAN via the server: <i>noHeidelpayUI=true && serverSide==true</i></li>
	 * <li>Use custom javascript to transfer the IBAN from the browser to heidelpay: noHeidelpayUI=true && serverSide==false<i></i></li>
	 * </ul>
	 * @param id the customer id
	 * @param noHeidelpayUi
	 * @param serverSide
	 * @return the view to be rendered
	 */
	@GetMapping("{id}")
	public ModelAndView payout(@PathVariable Long id, 
			@RequestParam(required=false, name="noHeidelpayUi") boolean noHeidelpayUi,
			@RequestParam(required=false, name="serverSide") boolean serverSide) {
		String view;
		if(noHeidelpayUi == true) {
			if(serverSide == true) {
				view = "payouts/detail_server_side.html";
			} else {
				view = "payouts/detail_own_js.html";
			}
		} else {
			view = "payouts/detail_with_ui_component.html";
		}
		
		
		ModelAndView modelAndView = new ModelAndView(view);
		modelAndView.addObject("customer", customerDao.getOne(id));
		modelAndView.addObject("publicKey", publicKey);
		modelAndView.addObject("api", api);
		return modelAndView;
	}

	/**
	 * performs the payout by transfering the IBAN from the server side.
	 * @param amount the amount to be payed out.
	 * @param customerId the customer id
	 * @param iban the iban
	 * @return redirects to the index page
	 * @throws HttpCommunicationException
	 */
	@PostMapping("iban")
	public RedirectView doPayout(@RequestParam BigDecimal amount, @RequestParam Long customerId,
			@RequestParam String iban) throws HttpCommunicationException {
		PaymentType paymentType = heidelpay.createPaymentType(new SepaDirectDebit(iban));
		return doPayout(customerId, paymentType.getId(), amount);
	}
	
	/**
	 * Performs a payout using the given paymentType reference.
	 * @param customerId the customer id
	 * @param paymentType the paymentType to pay to
	 * @param amount the amount to be payed out
	 * @return redirects to the index page
	 * @throws HttpCommunicationException
	 */
	@PostMapping()
	public RedirectView doPayout(@RequestParam Long customerId, @RequestParam String paymentType,
			@RequestParam BigDecimal amount) throws HttpCommunicationException {

			Customer customer = customerDao.getOne(customerId);
			Payout payout = heidelpay.payout(amount, Currency.getInstance("EUR"), paymentType, null);
			
			PayoutReference reference = new PayoutReference();
			reference.setAmount(payout.getAmount());
			reference.setPaymentType(paymentType);
			reference.setReference(payout.getPaymentId() + "//" + payout.getId());
			reference.setCurrency(payout.getCurrency().getDisplayName());
			
			if(payout.getPayment().getPaymentType() instanceof SepaDirectDebit) {
				SepaDirectDebit sepa = (SepaDirectDebit)payout.getPayment().getPaymentType();
				reference.setDetail(sepa.getIban());
			}
			
			customer.addPayout(reference);
			
			customerDao.saveAndFlush(customer);
	
			return new RedirectView("/payouts");
	}
	
	

}
