package com.namics.distrelec.b2b.facades.order.converters;

import com.distrelec.webservice.if15.v1.VoucherResponse;
import com.namics.distrelec.b2b.facades.order.data.DistErpVoucherInfoData;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import org.apache.commons.lang.StringUtils;

public class DistVoucherToErpVoucherInfoConverter extends AbstractPopulatingConverter<VoucherResponse, DistErpVoucherInfoData> {

    @Override
    public void populate(final VoucherResponse source, final DistErpVoucherInfoData target) {
        // only one voucher is allowed, so get the first one
        if (source.isValid()) {
            target.setCode(source.getCode());
            target.setValid(source.isValid());
            final String returnCode = StringUtils.isEmpty(source.getErrorCode()) ? "99" : source.getErrorCode();
            target.setReturnERPCode(source.isValid() ? "00" : returnCode);
        }
    }

}
