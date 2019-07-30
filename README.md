# Heidelpay Demo Shop

Initially written as sample for an article on the W3C's Payment Request Api Specification at [javamagazin](https://jaxenter.de/ausgaben/java-magazin-9-19), this Demo Shop is continously extended to provide various samples for the heidelpay Payment Api.

NOTE: this shop must not be used in production!

## Run

The Demo-Shop is a Spring Boot App. Assuming Java (>= 8) is installed on your system, simply clone the repo and call `gradlew bootRun`

## tl;dr
The heidelpay sdk is configured in the application.properties and 'initialized' in the `PaymentDemoShopConfiguration`. The `PaymentDemoShopApplication` bootstraps some random products for the shop. On your local system the Shop will be available under http://localhost:8080. Once you have added some products into your basket, you can klick the link in the header to come to http://localhost/8080/basket. There you will find the various options to pay.
The 'shop' itself is implemented within the `ShopController`, the basket within the `BasketController`. The `BasketHolder` is an utility giving access to the session scoped basket.
The payment integrations can be found in the `com.heidelpay.samples.shop.payment`package. You might also want to have a look into the application.properties to configure the demo with your own heidelpay keys.
The UI is built with thymeleaf, the templates are structured according to the features (e.g shop, payment, ...). Within the shop.properties smaller customizations to the shop can be made. 

## Payments

For a detailed documentation please refer to: 

* [Quickstart](https://docs.heidelpay.com/docs/quickstart)
* [Api Explorer](https://docs.heidelpay.com/reference)
* [Java SDK](https://docs.heidelpay.com/docs/java-sdk)
* [UI-Components](https://docs.heidelpay.com/docs/web-integration)
* [Documentation](https://docs.heidelpay.com/docs)

### Java-SDK
All integration samples are based on the Java-SDK to be found on [github](https://github.com/heidelpay/heidelpayJava) under the Apache 2 license. The default implementation of the SDK's http communication is based on the commons-http-client. However, you can fully customize the networking layer by implementing the `com.heidelpay.payment.communication.HeidelpayRestCommunication`interface, best by subclassing the [com.heidelpay.payment.communication.AbstractHeidelpayRestCommunication](https://github.com/heidelpay/heidelpayJava/blob/master/src/main/java/com/heidelpay/payment/communication/AbstractHeidelpayRestCommunication.java).

### heidelpay UI-Components
The heidelpay UI-Components are a rich set of customizable UI-Components for the Web-Integration. Using the heidelpay UI-Components might save a lot of time while bringing the best user experience to the browser. A demonstration of the components can be found [here](https://static.heidelpay.com/demo/).
However, while it is recommended to use those components, the API can be used completely without. In the Payouts and Registration section we illustrate how to use the Api by implementing an own javascript, or completely without using javascript by utilizing the server side. 

### W3C Payment Request Api

The implementation of the [W3C' Payment Request Api Specification](https://www.w3.org/TR/payment-request) is described in the javamagazin article. The integration of the Payment Request Api shows both, how to finalize a Credit-Card Payment based on the heidelpay Payment Api and how to integrate your own payment methods. A sample for a payment method could be found [here](https://github.com/heidelpay/sample-payment-request-api).
The checkout UI is fully realized by the Browser based on the Payment Request API. The processing of the payment itself is done server-2-server in the PaymentRequstApiController. The initialization of the browsers Payment Request Api support is done within the payment-api.js. Since dealing with credit card data on the server side needs to be PCI-DSS certified, the CreditCard data is transferred directly from the browser to heidelpay's server. This is done within the heidelpay-payment.js.

### PayPage
As a simple way of integrating payments, the heidelpay Payment APi offers a embedded PayPage. The PayPage allows a bunch of payment methods to be integrated without writing a single integration for each method. The Paypage is integrated once and gives all available methods to the customer.
The paypage is directly initiated from the "Pay with Paypage" button in the `shop/basket.html` template. For getting the paypage rendered the `https://static.heidelpay.com/v1/checkout.js` javascript must be loaded. The paypage is then opened as shown in the `payment/fragments :: paypage`fragment.
Basic configuration of the Paypage is done within the `ConfiguredPaypage`class. The Paypage handling is implemented in the `PaypageController`.


### Native Integration
Currently credit-card payment and SEPA direct debit is supported within the sample shop. As you can see, integrating new payment methods is almost a frontend task. While there are dedicated templates for the input of the payment methods, the server-side handling of the payments for all methods is done in the generic `PaymentController`'s `charge`action.

## Registration
Payment-types could be registered at heidelpay for recurring payments, e.g. in case of subscription models. The `PaymentTypeController`implements the options for registrating/storing the payment-type references for later any later use. The `paymenttyppe/register.html`template illustrates how to register with heidelpay's UI-Components, with a custom Javascript, or via server-2-server communication.

## Payouts
Payouts are a very specific use case where money gets transfered to the customer. 
To learn more about payouts in general, please refer to https://docs.heidelpay.com/docs/payouts.

The Demoshops illustrates 3 versions for performing payouts, all based on SEPA direct-debit:

* Based on the heidelpay UI components Sepa field
* Transfering the IBAN from server to server
* Own javascript sending the IBAN to heidelpay from the browser

The payouts demonstration can be accessed under http://localhost:8080/payouts, showing 3 Demo-Customers. From this index-page you can call the various UI versions. See the `payouts/index.html` template for details.

### Payout with heidelpay UI components
The most common and simple integration. As with the native payment integration, the IBAN is transferred directly from the browser to the heidelpay servers. The browser than sends the user to the `PayoutsController`'s `payout`action by posting the reference to the payment-type.
The `PayoutsController` utilizes the heidelpay SDK with the `heidelpay.payout()`method for processing the payout. The payout is stored at the `Customer`'s payouts, a list of `PayoutReferences`. 
The UI is rendered by the `payouts/detail_with_ui_components.html`template. The IBAN field is rendered via heidelpay's UI Components.

### Server-2-server Payment
While credit-card data must not be processed on non certified servers, a IBAN can. The `payouts/detail_server_side.html` template builds a form containing both, the IBAN and the amount to be payed out. Both is transfered to the shop server and there handled in `payout`action.

### Own Javascript
If neither the heidelpay UI components could be used, nor an IBAN should be ever touch the shop server, the IBAN could be sent directly to heidelpay's `/types/sepa-direct-debit` endpoint. This is shown in the `payouts/detail_own_js.html` template. Once the payment-type was created via javascript, the returned references is used to go into the same flow as when using the heidelpay UI components. 
