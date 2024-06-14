package com.namics.distrelec.b2b.facades.constants;

public interface DigitalDatalayerConstants {

	interface AdobeDTM {
		String DIGITAL_DATALAYER = "digitaldata";
		String DIGITAL_DATALAYER_DATA = "digitaldatalayer";
	}

	interface PageCategory {
		String HOMEPAGE = "homepage";
		String SEARCH = "search";
		String LOGIN = "login";
		String REGISTRATION = "registration";
		String FEEDBACK = "feedback";
		String CATEGORY = "category";
		String MANUFACTURER = "manufacturer";
		String CART = "cart";
		String CHECKOUT = "checkout";
		String TRANSACTION = "transaction";
	}

	interface PageName {
		interface Checkout {
			String ADDRESS = "billing";
			String ORDERDETAIL = "delivery";
			String REVIEW = "review";
			String PAYMENT = "payment";
			String CONFIRMATION = "confirmation";
			String CHECKOUT = "checkout";
			String CART = "cart";

		}
	}

	interface PageType {
		String HOMEPAGE = "homepage";
		String BOMTOOL = "bom tool";
		String CATEGORYPAGE = "category page";
		String SUBCATEGORYPAGE = "subcategory page";
		String CATEGORYPLPPAGE = "category plp";
		String PDPPAGE = "pdp page";
		String ERRORPAGE = "error page";
		String MYACCOUNTPAGE = "my account page";
		String CARTPAGE = "cart page";
		String CHECKOUTPAGE = "checkout page";
		String TRANSACTIONPAGE = "transaction page";
		String MANUFACTURERPAGE = "manufacturer page";
		String MANUFACTURERPLPPAGE = "manufacturer plp";
		String SERVICEPAGE = "service page";
		String CONFIGURATIONPAGE = "configuration page";
		String APPLICATIONSPAGE = "applications page";
		String LOGINREGISTRATIONPAGE = "login-registration page";
		String SEARCHRESULTSPLPPAGE = "search results plp";
		String SEARCHRESULTSPAGE = "searchresultspage";
		String FAQPAGE = "faq pages";
		String ABOUTUSPAGE = "about us pages";
		String FEEDBACKPAGE = "feedbackpage";
		String NPSPAGE = "npspage";
		String NEWSLETTERPAGE = "newsletterpage";
		String RESETPASSWORDPAGE = "resetpasswordpage";
		String RESETTOKEN = "resettokenpage";
		String LOGINPAGE = "loginpage";
		String REGISTRATIONPAGE = "registrationpage";
		String PRODUCTFAMILYPAGE = "product family";

	}

	interface Request {
		String WEBSHOP = "Distrelec Webshop";
		String EMAIL = "Webmail";
	}
}
