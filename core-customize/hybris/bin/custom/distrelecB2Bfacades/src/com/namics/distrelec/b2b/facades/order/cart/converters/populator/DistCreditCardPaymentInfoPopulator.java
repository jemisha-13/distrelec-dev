/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.order.cart.converters.populator;

import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.service.order.DistCardPaymentService;
import com.namics.distrelec.b2b.core.service.user.DistUserService;
import com.namics.distrelec.b2b.core.util.DistUtils;
import com.namics.distrelec.b2b.facades.order.checkout.DistCheckoutFacade;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.CardTypeData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class DistCreditCardPaymentInfoPopulator implements Populator<CreditCardPaymentInfoModel, CCPaymentInfoData> {

    @Autowired
    private DistUserService userService;

    @Autowired
    private DistCheckoutFacade checkoutFacade;

    @Autowired
    private DistCardPaymentService distCardPaymentService;

    @Override
    public void populate(final CreditCardPaymentInfoModel source, final CCPaymentInfoData target) throws ConversionException {
        target.setCardNumber(DistUtils.maskCreditCardNumber(source.getNumber()));
        target.setId(source.getPk().toString());
        target.setCardTypeData(getCardTypeData(source));
        target.setAccountHolderName(source.getCcOwner());
        target.setStartMonth(source.getValidFromMonth());
        target.setStartYear(source.getValidFromYear());
        target.setExpiryMonth(source.getValidToMonth());
        target.setExpiryYear(source.getValidToYear());
        target.setSaved(source.isSaved());
        if (checkoutFacade.isNotLimitedUserType() && userService.getCurrentUser() instanceof B2BCustomerModel) {
            final B2BCustomerModel customer = (B2BCustomerModel) userService.getCurrentUser();
            target.setDefaultPaymentInfo(source.equals(customer.getDefaultPaymentInfo()));
        }
        target.setIsValid(distCardPaymentService.isCreditCardExpiryDateValid(source));
    }

    private CardTypeData getCardTypeData(final CreditCardPaymentInfoModel source) {
        final CardTypeData ctd = new CardTypeData();
        ctd.setCode(source.getType().getCode());
        ctd.setName(source.getType().getCode());
        return ctd;
    }
}
