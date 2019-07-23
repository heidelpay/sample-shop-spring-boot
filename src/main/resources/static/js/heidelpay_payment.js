const heidelpay_invoice = 'https://heidelpay-payment.herokuapp.com/heidelpay-invoice';
	
class HeidelpayApi {
	constructor(publicKey, callbackUrl, successUrlGenerator) {
		this.heidelpayAdapter = new HeidelpayApiAdapter(publicKey);
		this.shopCallbackUrl = callbackUrl;
		this.successUrl = successUrlGenerator;
	}
	
	init() {
		const creditCard = {
				supportedMethods : 'basic-card',
				data : {
					supportedNetworks : [ 'visa', 'mastercard', 'amex' ],
					supportedTypes : [ 'credit', 'debit' ],
				}
		}
		
		if(card == true) {
			this.supportedPaymentMethods = [creditCard];	
		} else if(own == true) {
			this.supportedPaymentMethods = [{supportedMethods: heidelpay_invoice}];
		} else {
			this.supportedPaymentMethods = [creditCard, {supportedMethods: heidelpay_invoice}];	
		}
		
		
		this.paymentOptions = {
				requestPayerEmail:false,
				requestShipping:true,
				requestPayerName:false,
				requestPayerPhone:false
		};
		
		this.shippingOptions = [];		  
	}
	
	addShippingOption(id, label, currency, amount) {
		this.shippingOptions.push(
			{
				  id: id,
				  label: label,
				  amount: {
					  currency: currency,
					  value: amount,
				  }
			  }
		)
	}
	
	
	doPay(paymentDetails) {
		
		paymentDetails.shippingOptions = []
		
		let req = PaymentRequestFactory.createPaymentRequest(paymentDetails, this.supportedPaymentMethods, this.paymentOptions, this.shippingOptions);
		
		req.show().then((paymentResponse) => {
			
			if(paymentResponse.methodName === "basic-card") {
				this.processCreditCardPayment(paymentResponse, paymentDetails);
			} else if (paymentResponse.methodName === heidelpay_invoice && paymentResponse.details.completed === true){
				this.processInvoicePayment(paymentResponse, paymentDetails);
			} else {
				return paymentResponse.complete("fail")
			}
		})
		.catch((error) => {
			console.log(error)
		});

	}


	 finalizePayment(paymentResponse, paymentDetails, type, customerId, data) {
		var _this = this;
		this.heidelpayAdapter.createPaymentMethod(type, data,
				function(response){
					_this.completePayment(paymentResponse, response, paymentDetails, customerId)
				},
				function(err){console.log(err)})		
	}

	processCreditCardPayment(paymentResponse, paymentDetails) {
		var customer = this.heidelpayAdapter.extractCustomer(paymentResponse.shippingAddress, paymentResponse.details); 
		var card = this.heidelpayAdapter.extractCard(paymentResponse.details);
		var _this = this;
		this.heidelpayAdapter.createCustomer(customer,
				function(created){_this.finalizePayment(paymentResponse, paymentDetails, "card", created.data.id, card)},
				function(err){paymentResponse.complete("fail")});
		
	}


	processInvoicePayment(paymentResponse, paymentDetails) {

		var customer = this.heidelpayAdapter.extractCustomer(paymentResponse.shippingAddress, paymentResponse.details); 
		var _this = this;
	
		this.heidelpayAdapter.createCustomer(customer,
				function(created){_this.finalizePayment(paymentResponse, paymentDetails, "invoice-guaranteed", created.data.id)},
				function(err){paymentResponse.complete("fail")});
		
	}

	completePayment(paymentResponse, response, paymentDetails, customerId) {

		if(response.success) {
			var payment = response.data.id;
			
			var paymentData = new Object();
			paymentData.total = paymentDetails.total.amount.value;
			paymentData.currency = paymentDetails.total.amount.currency;
			paymentData.customer = customerId;
			paymentData.paymentMethod = payment;
		
			var _this = this;
			AjaxUtils.post(this.shopCallbackUrl, paymentData, 
				function( data ){
			    	window.location = _this.successUrl(data);
			    	paymentResponse.complete("success")
			    },
			    function( data ){
			        paymentResponse.complete("fail");
			    }
			)
								
		} else {
			
			paymentResponse.complete(response.data.errors[0].customerMessage);
		}

	}

	
		
}

class AjaxUtils {
	
	static post(url, data, success, error, optHeaders) {
		$.ajax({
			url:url,
			type:"POST",
			contentType:"application/json",
			dataType:"json",
			data: JSON.stringify(data),
		    headers:optHeaders,
			success: function( data, textStatus, jQxhr ){
		    	success(data);
		    },
		    error: function( xhr, textStatus, errorThrown ){
		    	error(xhr.responseJSON.message);
		    }
		});		
	}
	
}

class HeidelpayApiAdapter {
	
	constructor(publicKey) {
		this.heidelpayInstance = new heidelpay(publicKey)
	}
	
	createPaymentMethod(type, data, callback, errorHandler) {
		this.doPost("/types/" + type, data, callback, errorHandler);
	}
	
	createCustomer(customer, callback, errorHandler) {
		this.doPost("/customers", customer, callback, errorHandler);
	}
	
	doPost(endpoint, payload, callback, errorHandler) {

		this.heidelpayInstance.request.post(endpoint, payload)
		.then(function(response){
			callback(response)
		})
		.catch(function(err){errorHandler()});
	}
	
	extractCard(details) {
		var card = new Object();
		card.number = details.cardNumber;
		card.cvc = details.cardSecurityCode;
		card.expiryDate = details.expiryMonth + "/" + details.expiryYear.substring(2,4);
		return card
	}
	
	extractCustomer(shippingAddress, details) {
		var customer = new Object();
	   	var namearray = shippingAddress.recipient.split(' ');
	   	
		customer.firstname = namearray[0];
		customer.lastname = namearray[1];
		customer.birthDate = details.birthdate;
		customer.salutation = details.salutation;
		customer.phone = shippingAddress.phone;
		var address = new Object();
		address.zip = shippingAddress.postalCode;
		address.city = shippingAddress.city;
		address.street = shippingAddress.addressLine[0];
		// country code, eg "DE"
		address.country = shippingAddress.country;
		
		customer.billingAddress = address;
		
		return customer;
		
	}
	
}

class PaymentRequestFactory {
	
	static createPaymentRequest(paymentDetails, supportedPaymentMethods, paymentOptions, shippingOptions) {
		
		let req = new PaymentRequest(supportedPaymentMethods, paymentDetails,
				paymentOptions);

		PaymentRequestFactory.registerShippingAddressChangedListener(req, paymentDetails, shippingOptions);
		PaymentRequestFactory.registerShippingOptionsChangesListener(req, paymentDetails);

		return req;
	}
	
	static registerShippingAddressChangedListener(request, paymentDetails, shippingOptions) {
		request.addEventListener('shippingaddresschange', (event) => {
			 
			paymentDetails.shippingOptions = shippingOptions;

			event.updateWith(paymentDetails)
		});
		
	}
	static registerShippingOptionsChangesListener(request, paymentDetails) {
		
		request.addEventListener('shippingoptionchange', (event) => {
			
			  const paymentRequest = event.target;
			  const selectedId = paymentRequest.shippingOption;
			  
			  paymentDetails.shippingOptions.forEach((option) => {
				  if(option.id === selectedId) {
					  option.selected = true;
					  paymentDetails.displayItems.push( option )
					  paymentDetails.total.amount.value = paymentDetails.total.amount.value + option.amount.value;
				  }  
				  option.selected = option.id === selectedId;
			  });
					
			  event.updateWith(paymentDetails)
			});
		
	}
		
}
