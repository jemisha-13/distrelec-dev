/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2012 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package com.namics.distrelec.b2b.facades.constants;

import com.namics.distrelec.b2b.core.service.customer.strategies.CheckoutCustomerStrategy;

/**
 * Constants used in the Web tier.
 */
public final class WebConstants {
    private WebConstants() {
        // empty
    }

    public static final String MODEL_KEY_ADDITIONAL_BREADCRUMB = "additionalBreadcrumb";
    public static final String BREADCRUMBS_KEY = "breadcrumbs";

    public static final String CONTINUE_URL = "session_continue_url";

    public static final String COOKIE_KEY_CHANNEL = "channel";
    public static final String COOKIE_KEY_COUNTRY = "country";
    public static final String COOKIE_KEY_CURRENCY = "currency";
    public static final String COOKIE_KEY_LANGUAGE = "language";
    public static final String COOKIE_KEY_COOKIE_MESSAGE_CONFIRMED = "cookieMessageConfirmed";

    public static final String URL_PARAM_KEY_CHANNEL = "channel";
    public static final String URL_PARAM_KEY_LANGUAGE = "language";
    public static final String URL_PARAM_KEY_COUNTRY = "country";
    public static final String URL_PARAM_KEY_USERNAME = "username";
    public static final String URL_PARAM_KEY_PASSWORD = "password";
    public static final String URL_PARAM_KEY_HOOKURL = "hook_url";
    public static final String URL_PARAM_KEY_TOKEN = "customerToken";
    public static final String URL_PARAM_KEY = "param";
    public static final String URL_PARAM_KEY_COUNTRY_REDIRECT = "countryRedirect";
    public static final String ITEMS_PER_PAGE = "itemsPerPage";
    public static final String PAGE_SIZE = "pageSize";
    public static final String DEFAULT_ITEMS_PER_PAGE = "10";

    public static final String TEST_FIX_DELAY_IN_MS = "testFixDelayInMillis";
    public static final String TEST_RANDOM_DELAY_IN_MS = "testRandomDelayInMillis";

    public static final String ANONYMOUS_CHECKOUT = CheckoutCustomerStrategy.ANONYMOUS_CHECKOUT;

    public static final String AGREED_TERMS = "agreedTerms";
    public static final String SUBMIT_APPROVAL = "submitApproval";
    public static final String EXCEEDED_BUDGET = "exceededBudget";

    public static final String SPRING_SECURITY_CHECK_URL = "/j_spring_security_check";

    public static final String ACCOUNT_NOT_ACTIVE = "accountNotActive";
    public static final String ACCOUNT_MIGRATED = "accountMigrated";
    public static final String LOGIN_WRONG_COUNTRY = "loginWrongCountry";
    public static final String REDIRECT_COUNTRY = "redirectCountry";
    public static final String REDIRECT_SITE = "redirectSite";
    public static final String WRONG_CAPTCHA = "wrongCaptcha";
    public static final String DUPLICATE_EMAIL = "duplicateEmailAuth";

    public static final String CHECKOUT_START = "checkoutStart";
    public static final String LOGIN_SUCCESS = "loginSuccess";
    public static final String CHECKOUT_LOGIN_SUCCESS = "checkoutLoginSuccess";
    public static final String CHECKOUT_REGISTER_SUCCESS = "checkoutRegisterSuccess";
    public static final String LOGOUT_SUCCESS = "logoutSuccess";

    public static final String UPDATE_SUCCESS = "updateSuccess";
    public static final String DO_NOT_STORE_REFERRER = "doNotStoreReferrer";

    public static final String INVOICE_PAYMENT_MODE_REQUESTED = "invoicePaymentModeRequested";

    public static final String SHORT_TIME_SESSION_PARAM = "shortTimeSession";

    public static final String HAS_EOL_PRODUCTS = "eol";
    public static final String PRODUCTS_EOL = "eolProducts";

    public static final String HAS_PUNCHED_OUT_PRODUCTS = "punchout";
    public static final String PRODUCTS_PUNCHED_OUT = "punchedOutProducts";

    public static final String PRODUCT_RECOMMENDATIONS = "productRecommendations";

    public static final String CURRENT_CMS_PAGE = "CurrentCMSPage";
    public static final String COMPLETE_DELIVERY = "completeDelivery";

    public static final String PRODUCTS_PHASE_OUT = "phaseOutProducts";

    public static final String DEACTIVATED_CONTACT_URL = "/?deactivated=true";


    public static final String ALLOWED_TO_SUBMIT_NPS = "allowedToSubmitNPS";
    public static final String LAST_NPS_DATE = "lastNpsDate";
    public static final String STATUS = "status";
    public static final String OK = "OK";
    public static final String NOK = "NOK";
    public static final String MESSAGE_KEY = "messageKey";
    public static final String MESSAGE_KEY_ARG = "messageKeyArg";
    public static final String NPS_FORM_REASONS = "npsFormReasons";
    public static final String NPS_FORM_SUBREASONS = "npsFormSubReasons";
    public static final String NPS_FORM = "npsForm";
    public static final String APPLICATION_JSON = "application/json";
    public static final Integer DEFAULT_STARTING_NPS_SCORE = -1;
    public static final String CONSENT_CONFIRMATION_SENT = "consentConfirmationSent";
    public static final String CONSENT_CONFIRMATION_FAILED = "consentConfirmationFailed";
    public static final String CONSENT_CONFIRMATION_SUCCESS = "consentConfirmationSuccess";
    public static final String EMAIL_UPDATE_SUCCESS = "emailChangeSuccess";

    public static final String SHOW_CAPTCHA = "SHOW_CAPTCHA";
    public static final String HAS_MOQ_UPDATED_SINCE_LAST_CART_LOAD = "hasMoqUpdatedSinceLastCartLoad";

    public static final String MOV_MISSING_VALUE = "movMissingValue";
    public static final String MOV_LIMIT_VALUE = "movLimitValue";
    public static final String MOV_CART_VALUE = "movCartValue";
    public static final String MOV_DISPLAY_MESSAGE_ON_PAGE_LOAD = "movDisplayMessageOnPageLoad";

    public static final String EXPORT_MEDIA_URL = "export";
}
