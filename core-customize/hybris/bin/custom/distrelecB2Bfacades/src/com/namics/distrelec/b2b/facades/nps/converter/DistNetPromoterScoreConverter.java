/*
 * Copyright 2000-2015 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.nps.converter;

import com.namics.distrelec.b2b.core.model.feedback.DistNetPromoterScoreModel;
import com.namics.distrelec.b2b.core.service.nps.data.DistNetPromoterScoreData;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;

/**
 * {@code DistNetPromoterScoreConverter}
 * <p>
 * Converter class to create a {@link DistNetPromoterScoreData} from a {@link DistNetPromoterScoreModel}.
 * </p>
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 3.4
 */
public class DistNetPromoterScoreConverter extends AbstractPopulatingConverter<DistNetPromoterScoreModel, DistNetPromoterScoreData> {

    /*
     * (non-Javadoc)
     *
     * @see de.hybris.platform.converters.impl.AbstractPopulatingConverter#populate(java.lang.Object, java.lang.Object)
     */
    @Override
    public void populate(final DistNetPromoterScoreModel source, final DistNetPromoterScoreData target) {
        target.setCode(source.getCode());
        target.setReason(source.getReason());
        target.setType(source.getType());
        target.setValue(source.getValue() != null ? source.getValue().intValue() : 0);
        target.setErpCustomerID(source.getErpCustomerID());
        target.setErpContactID(source.getErpContactID());
        target.setOrderNumber(source.getOrderNumber());
        target.setEmail(source.getEmail());
        target.setFirstname(source.getFirstname());
        target.setLastname(source.getLastname());
        target.setSalesOrg(source.getSalesOrg());
        target.setDomain(source.getDomain());
        target.setCreationDate(source.getCreationtime());
        target.setDeliveryDate(source.getDeliveryDate());
        target.setCompanyName(source.getCompanyName());
        target.setText(source.getText());
        target.setSubreason(source.getSubReason());
    }
}
