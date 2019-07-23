const payment = new HeidelpayApi('s-pub-2a10ifVINFAjpQJ9qW8jBe5OJPBx6Gxa', '/payment/native/pay',
					function(data){return "/payment/success/" + data.basketId})

payment.init();

payment.addShippingOption("Standard-Versand", "Standard (5 Tage)", "EUR", 0);
payment.addShippingOption("Express", "Übernacht", "EUR", 12.00);


function pay(own, both) {
	
	if (window.PaymentRequest) {
		doPay( grabPaymentDetails() );
	} else {
		// redirect to checkout...
		alert("Payment Api not supported by your Browser");
	}

}

function grabPaymentDetails() {
	let basket = []
	
	let basketItems = document.getElementsByClassName('basket-row');
	let sum = 0;
	// Array.from fixes Edge-Browser
	for(let row of Array.from(basketItems)) {
		basket.push({
			label:row.dataset.title,
			amount:{
				currency:'EUR',
				value: parseFloat(row.dataset.total)
			}
		})
		sum = sum + parseFloat(row.dataset.total);
	}
	let total = {
		label : 'Total',
		amount : {
			currency : 'EUR',
			value : sum
		}

	}
	return applyDiscount( {
		total : total,
		displayItems:basket
	});
}

function applyDiscount(paymentDetails) {
	
	let subtotalItem = {
			label: 'Zwischensumme:',
			amount: {
				currency:'EUR',
				value: paymentDetails.total.amount.value
			}
	}
	let discount = (paymentDetails.total.amount.value * 0.1).toFixed(2);
	let discountItem = {
			label: '10% Rabatt',
			amount: {
				currency:'EUR',
				value: ( -discount )
			}
	}
	
	paymentDetails.total.amount.value = paymentDetails.total.amount.value - discount;
	
	paymentDetails.displayItems.push(subtotalItem);
	paymentDetails.displayItems.push(discountItem)
	
	return paymentDetails;
}
	

function doPay(paymentDetails) {
	payment.doPay(paymentDetails);
}
