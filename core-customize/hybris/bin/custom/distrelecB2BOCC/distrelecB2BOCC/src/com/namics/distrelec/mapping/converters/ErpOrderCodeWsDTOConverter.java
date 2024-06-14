package com.namics.distrelec.mapping.converters;

import java.util.function.Predicate;

import javax.annotation.Resource;

import de.hybris.platform.commercefacades.order.data.OrderData;
import org.apache.commons.lang.StringUtils;

import com.namics.distrelec.occ.core.order.data.ErpOrderCodeWsDTO;

import de.hybris.platform.cmsoccaddon.mapping.converters.DataToWsConverter;
import de.hybris.platform.commercewebservicescommons.dto.order.GeneratedVoucherWsDTO;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import ma.glasnost.orika.MapperFactory;

public class ErpOrderCodeWsDTOConverter implements DataToWsConverter<OrderData, ErpOrderCodeWsDTO> {

    @Resource(name = "dataMapper")
    private DataMapper dataMapper;

    @Override
    public Class getDataClass() {
        return OrderData.class;
    }

    @Override
    public Class getWsClass() {
        return ErpOrderCodeWsDTO.class;
    }

    @Override
    public Predicate<Object> canConvert() {
        return null;
    }

    @Override
    public ErpOrderCodeWsDTO convert(OrderData source, String fields) {
        ErpOrderCodeWsDTO wsDTO = new ErpOrderCodeWsDTO();
        final boolean success = StringUtils.isNotEmpty(source.getErpOrderCode());
        wsDTO.setStatus(success ? "ok" : "waiting");
        wsDTO.setErpCode(success ? source.getErpOrderCode() : StringUtils.EMPTY);
        if (source.getGeneratedVoucher() != null) {
            wsDTO.setErpVoucher(dataMapper.map(source.getGeneratedVoucher(), GeneratedVoucherWsDTO.class));
        }
        return wsDTO;
    }

    @Override
    public void customize(MapperFactory factory) {
    }
}
