package com.heidelpay.samples.shop.payment;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.servlet.view.RedirectView;

import com.heidelpay.payment.Heidelpay;
import com.heidelpay.payment.PaymentException;
import com.heidelpay.payment.communication.HttpCommunicationException;
import com.heidelpay.payment.paymenttypes.Card;
import com.heidelpay.payment.paymenttypes.PaymentType;
import com.heidelpay.payment.paymenttypes.SepaDirectDebit;
import com.heidelpay.samples.shop.dao.CustomerRepository;
import com.heidelpay.samples.shop.model.Customer;
import com.heidelpay.samples.shop.model.PaymentReference;

/**
 * Contoller handling storing, and registration of payment-types.
 */
@Controller
@RequestMapping("paymenttype")
public class PaymentTypeController {

	@Value("${shop.heidelpay.key.public}")
	protected String publicKey;
	

	@Value("${shop.heidelpay.api}")
	protected String api;
	
	@Autowired CustomerRepository customerDao;
	@Autowired Heidelpay heidelpay;
	
	/**
	 * Show a view with different options to register a payment-type-
	 * @param customerId
	 * @return
	 */
	@GetMapping("register/{customerId}")
	public ModelAndView registerView(@PathVariable Long customerId) {
		Map<String, Object> map = new HashMap<>();
		map.put("customer", customerDao.getOne(customerId));
		map.put("publicKey", publicKey);
		map.put("api", api);
		return new ModelAndView("paymenttype/register", map);
	}
	
	/**
	 * Stores and optionally registered a paymentType.
	 * 
	 * @param paymentTypeReference - id of heidelpays paymentType
	 * @param paymentType the type, internally used
	 * @param doregister if true the payment-type will be registered for recurring payments
	 * @param customerId internal customerId
	 * @return
	 * @throws PaymentException
	 * @throws HttpCommunicationException
	 * @throws MalformedURLException
	 */
	@PostMapping("register/{customerId}")
	public RedirectView register(@RequestParam String paymentTypeReference, @RequestParam String paymentType, 
			@RequestParam(required = false, name="") Boolean doregister, @PathVariable Long customerId) throws PaymentException, HttpCommunicationException, MalformedURLException {
		
		Customer customer = customerDao.getOne(customerId);
		PaymentReference reference = new PaymentReference();
		reference.setReference(paymentTypeReference);
		reference.setType(paymentType);
		
		PaymentType type = heidelpay.fetchPaymentType(paymentTypeReference);
		
		reference.setDetails( extractDetails(type) );
		
		if(doregister != null && doregister == true) {		
			heidelpay.recurring(paymentTypeReference, new URL(returnUrl()));
			reference.setRegistered( true );
		}
		
		customer.addPaymentReference(reference);
		
		customerDao.save( customer );
		
		return new RedirectView("/customer");
	}
	
	
	
	/**
	 * Illustrates the server-2-server registration of a SEPA direct debit payment-type.
	 * @param iban the iban
	 * @param customerId the internal customer id
	 * @return
	 * @throws HttpCommunicationException
	 * @throws PaymentException
	 * @throws MalformedURLException
	 */
	@PostMapping("register/{customerId}/iban")
	public RedirectView registerIban(@RequestParam String iban, @PathVariable Long customerId) throws HttpCommunicationException, PaymentException, MalformedURLException {
		PaymentType type =  heidelpay.createPaymentType(new SepaDirectDebit(iban));
		return register(type.getId(), "SEPA", false, customerId);
	}
	
	/**
	 * deletes the registration locally.
	 * @param customerId
	 * @param id
	 * @return
	 */
	@GetMapping("register/{customerId}/deregister/{id}")
	public RedirectView deregister(@PathVariable Long customerId, @PathVariable Long id) {
		Customer customer  = customerDao.getOne(customerId);
		Optional<PaymentReference> ref = customer.getPaymentReferences().stream().filter(it -> it.getId() == id).findFirst();
		if(ref.isPresent()) {
			customer.removePaymentReference(ref.get());
			customerDao.save(customer);
		}
		
		return new RedirectView("/customer");
	}
	
	private String extractDetails(PaymentType type) {

		if(type instanceof SepaDirectDebit) {
			SepaDirectDebit sepa = (SepaDirectDebit)type;
			return sepa.getIban();
		} else if(type instanceof Card) {
			Card card = (Card)type;
			return card.getNumber();
		} else {
			return "";
		}
		
	}
	
	protected String returnUrl() {

		String url = ServletUriComponentsBuilder.fromCurrentContextPath()
				.path("/customer").build().toString();
		
		// payment only allows https....
		if(url.contains("http://localhost")) {
			url = url.replace("http://local", "https://local");
		}
		
		return url;	
	}
}
