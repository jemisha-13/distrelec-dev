/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.erp;

import java.util.List;

import com.namics.distrelec.b2b.core.model.DistCodelistModel;
import com.namics.distrelec.b2b.core.model.payment.AbstractDistPaymentModeModel;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;

/**
 * Service to provide payment options.
 * 
 * @author pbueschi, Namics AG
 * @since Distrelec 1.0
 */
public interface PaymentOptionService {

    List<AbstractDistPaymentModeModel> getSupportedPaymentOptions(final CartModel cart, final boolean userHasBudget);

    DistCodelistModel getErpPaymentModeForAbstractDistPaymentMode(final AbstractDistPaymentModeModel paymentMode);

    DistCodelistModel getErpPaymentModeForAbstractDistPaymentMode(final AbstractDistPaymentModeModel paymentMode, final CreditCardPaymentInfoModel ccPaymentInfo);

    String getDeuCSCodeForCreditCardTypeCode(final AbstractDistPaymentModeModel paymentMode, final String creditCardTypeCode);

    AbstractDistPaymentModeModel getAbstractDistPaymentModeForErpPaymentModeCode(final String erpPaymentMethodCode);

    AbstractDistPaymentModeModel getAbstractDistPaymentModeForCreditCardTypeCode(final String creditCardTypeCode);

    List<AbstractDistPaymentModeModel> getSupportedPaymentOptionsForUser(B2BCustomerModel b2bCustomer);

    List<AbstractDistPaymentModeModel> getSupportedPaymentOptionsForUser(B2BCustomerModel b2bCustomer, boolean useCache);

    AbstractDistPaymentModeModel getDefaultPaymentOptionForUser(B2BCustomerModel b2bCustomer);

    AbstractDistPaymentModeModel getDefaultPaymentOptionForUser(B2BCustomerModel b2bCustomer, boolean useCache);

    boolean updateDefaultPaymentOption(final CartModel cart, final AbstractDistPaymentModeModel paymentOption);
}
