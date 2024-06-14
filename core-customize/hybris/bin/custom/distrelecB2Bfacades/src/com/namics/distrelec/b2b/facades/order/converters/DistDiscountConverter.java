package com.namics.distrelec.b2b.facades.order.converters;

import com.namics.distrelec.b2b.facades.order.data.DistDiscountData;

import de.hybris.platform.commercefacades.storesession.data.CurrencyData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.price.DiscountModel;

public class DistDiscountConverter extends AbstractPopulatingConverter<DiscountModel, DistDiscountData> {

    private Populator<CurrencyModel, CurrencyData> currencyPopulator;

    @Override
    public void populate(final DiscountModel source, final DistDiscountData target) {
        target.setAbsolute(source.getAbsolute());
        target.setCode(source.getCode());
        target.setDiscountString(source.getDiscountString());
        target.setGlobal(source.getGlobal());
        target.setPriority(source.getPriority());
        target.setValue(source.getValue());
        if (source.getCurrency() != null) {
            final CurrencyData currencyData = new CurrencyData();
            currencyPopulator.populate(source.getCurrency(), currencyData);
            target.setCurrency(currencyData);
        }
        super.populate(source, target);
    }

    public Populator<CurrencyModel, CurrencyData> getCurrencyPopulator() {
        return currencyPopulator;
    }

    public void setCurrencyPopulator(final Populator<CurrencyModel, CurrencyData> currencyPopulator) {
        this.currencyPopulator = currencyPopulator;
    }
}
