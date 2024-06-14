package com.namics.distrelec.b2b.facades.order.converters;

import com.namics.distrelec.b2b.core.model.order.DistErpVoucherInfoModel;
import com.namics.distrelec.b2b.facades.order.data.DistErpVoucherInfoData;
import com.namics.distrelec.b2b.facades.product.DistPriceDataFactory;

import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
public class DistErpVoucherInfoDataConverter extends AbstractPopulatingConverter<AbstractOrderModel, DistErpVoucherInfoData> {

    @Autowired
    private DistPriceDataFactory priceDataFactory;

    @Override
    protected DistErpVoucherInfoData createTarget() {
        return new DistErpVoucherInfoData();
    }

    @Override
    public void populate(final AbstractOrderModel abstractOrder, final DistErpVoucherInfoData voucherInfoData) {
        super.populate(abstractOrder, voucherInfoData);

        final DistErpVoucherInfoModel voucherInfo = abstractOrder.getErpVoucherInfo();
        voucherInfoData.setCode(voucherInfo.getCode());
        voucherInfoData.setFixedValue(createPriceData(abstractOrder, voucherInfo));
        voucherInfoData.setValid(voucherInfo.isValidInERP());
        voucherInfoData.setCalculatedInERP(voucherInfo.isCalculatedInERP());
        final String returnCode = voucherInfo.getReturnERPCode();
        voucherInfoData.setReturnERPCode(returnCode);
        voucherInfoData.setFixedFormattedValue(priceDataFactory.createWithoutCurrency(PriceDataType.BUY, BigDecimal.valueOf(voucherInfo.getFixedValue()),
                                                                                      abstractOrder.getCurrency().getIsocode()));
    }

    protected PriceData createPriceData(final AbstractOrderModel abstractOrder, final DistErpVoucherInfoModel voucherInfo) {
        return priceDataFactory.create(PriceDataType.BUY,
                                       voucherInfo.getFixedValue() != null ? BigDecimal.valueOf(voucherInfo.getFixedValue()) : BigDecimal.valueOf(0d),
                                       abstractOrder.getCurrency().getIsocode());
    }
}
