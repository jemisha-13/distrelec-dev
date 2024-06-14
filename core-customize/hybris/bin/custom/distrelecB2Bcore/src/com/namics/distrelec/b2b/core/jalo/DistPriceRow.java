package com.namics.distrelec.b2b.core.jalo;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;

import com.namics.distrelec.b2b.core.model.DistPriceRowModel;

import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.JaloBusinessException;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.enumeration.EnumerationValue;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.jalo.type.ComposedType;
import de.hybris.platform.jalo.user.User;

/**
 * Distrelec Price Row.
 * 
 * @author daehusir, Distrelec
 * 
 */
@SuppressWarnings("deprecation")
public class DistPriceRow extends GeneratedDistPriceRow {

    private final static Logger LOG = Logger.getLogger(DistPriceRow.class.getName());
    public static final String SCALE_SHIFT = "scaleShift".intern();
    public static final String DISCOUNTED_PRICE = "discountedPrice".intern();

    private ItemAttributeMap internalAllAttributes;

    @Override
    protected Item createItem(final SessionContext ctx, final ComposedType type, final ItemAttributeMap allAttributes) throws JaloBusinessException {
        this.internalAllAttributes = allAttributes;
        return super.createItem(ctx, type, allAttributes);
    }

    @Override
    protected int calculateMatchValue(final Product product, final EnumerationValue pg, final User user, final EnumerationValue cg) {
        final boolean _p = product != null;
        final boolean _c = user != null;
        final boolean _cg = cg != null;

        DistErpPriceConditionType erpPriceConditionType;
        try {
            if (internalAllAttributes != null && internalAllAttributes.get(DistPriceRowModel.ERPPRICECONDITIONTYPE) != null) {
                erpPriceConditionType = (DistErpPriceConditionType) (internalAllAttributes.get(DistPriceRowModel.ERPPRICECONDITIONTYPE));
            } else {
                erpPriceConditionType = this.getErpPriceConditionType();
            }
        } catch (final Exception e) {
            LOG.error("Price condition type not definied", e);
            erpPriceConditionType = null;
        }

        int value = 0;
        if (_p) {
            if (_c) {
                value = 6;
            } else if (_cg) {
                if (erpPriceConditionType == null) {
                    // DISTRELEC-6160 this make compatible with SAPEurope1PricesTranslatorTest
                    return 2;
                }
                if (erpPriceConditionType.getPriorityAsPrimitive() == 1) {
                    value = 5;
                } else if (erpPriceConditionType.getPriorityAsPrimitive() == 2) {
                    value = 4;
                } else if (erpPriceConditionType.getPriorityAsPrimitive() == 3) {
                    value = 3;
                } else {
                    value = 2;
                }
            } else {
                value = 1;
            }
        }
        return value;
    }

    public boolean isSpecialPrice() {
        return this.getErpPriceConditionType() != null && "ZN00".equals(this.getErpPriceConditionType().getCode());
    }

    @Override
    public void setErpPriceConditionType(SessionContext ctx, DistErpPriceConditionType value) {
        super.setErpPriceConditionType(ctx, value);
        updateMatchValue();
    }

    public Integer getMatchValue(final SessionContext ctx) {
        return (Integer) getProperty(ctx, MATCHVALUE);
    }

    public Integer getMatchValue() {
        return getMatchValue(getSession().getSessionContext());
    }

    public int getMatchValueAsPrimitive(final SessionContext ctx) {
        Integer value = getMatchValue(ctx);
        return (value != null) ? value.intValue() : 0;
    }

    public int getMatchValueAsPrimitive() {
        return getMatchValueAsPrimitive(getSession().getSessionContext());
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
