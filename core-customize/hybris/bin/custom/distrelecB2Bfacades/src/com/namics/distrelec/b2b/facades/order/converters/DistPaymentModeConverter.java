/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.order.converters;

import com.namics.distrelec.b2b.core.model.payment.AbstractDistPaymentModeModel;
import com.namics.distrelec.b2b.facades.order.data.DistPaymentModeData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.session.SessionService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.List;

import static com.namics.distrelec.b2b.core.util.DistUtils.isInvoicePaymentMode;
public class DistPaymentModeConverter<SOURCE extends AbstractDistPaymentModeModel>
        extends AbstractPopulatingConverter<AbstractDistPaymentModeModel, DistPaymentModeData> {

    @Autowired
    @Qualifier("imageConverter")
    private Converter<MediaModel, ImageData> imageConverter;

    @Autowired
    private PriceDataFactory priceDataFactory;

    @Autowired
    private CommonI18NService commonI18NService;

    @Autowired
    private SessionService sessionService;

    @Override
    public void populate(final AbstractDistPaymentModeModel source, final DistPaymentModeData target) {
        super.populate(source, target);

        target.setCode(source.getCode());
        target.setName(source.getName());
        target.setDescription(source.getDescription());
        target.setUrl(source.getUrl());
        target.setTranslationKey(source.getTranslationKey());
        if (CollectionUtils.isNotEmpty(source.getIcons())) {
            final List<ImageData> iconData = new ArrayList<ImageData>();
            for (final MediaModel icon : source.getIcons()) {
                iconData.add(getImageConverter().convert(icon));
            }
            target.setIcons(iconData);
        }

        if (source.getHop() != null) {
            target.setHop(source.getHop().booleanValue());
        }

        if (source.getIframe() != null) {
            target.setIframe(source.getIframe().booleanValue());
        }

        // DISTRELEC-6854 is the payment mode selectable ?
        final Object obj = getSessionService().getAttribute("paymentMode#" + target.getCode());
        final boolean selectable = obj == null || BooleanUtils.isTrue((Boolean) obj);
        target.setSelectable(selectable);

        target.setCreditCardPayment(source.getPaymentInfoType().getCode().equals(CreditCardPaymentInfoModel._TYPECODE));
        target.setInvoicePayment(isInvoicePaymentMode(source));
    }

    public Converter<MediaModel, ImageData> getImageConverter() {
        return imageConverter;
    }

    public void setImageConverter(final Converter<MediaModel, ImageData> imageConverter) {
        this.imageConverter = imageConverter;
    }

    public PriceDataFactory getPriceDataFactory() {
        return priceDataFactory;
    }

    public void setPriceDataFactory(final PriceDataFactory priceDataFactory) {
        this.priceDataFactory = priceDataFactory;
    }

    public CommonI18NService getCommonI18NService() {
        return commonI18NService;
    }

    public void setCommonI18NService(final CommonI18NService commonI18NService) {
        this.commonI18NService = commonI18NService;
    }

    public SessionService getSessionService() {
        return sessionService;
    }

    public void setSessionService(final SessionService sessionService) {
        this.sessionService = sessionService;
    }
}
