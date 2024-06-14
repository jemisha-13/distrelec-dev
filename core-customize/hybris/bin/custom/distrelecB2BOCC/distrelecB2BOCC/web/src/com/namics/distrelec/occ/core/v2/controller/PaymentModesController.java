/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.namics.distrelec.occ.core.v2.controller;

import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;

import com.namics.distrelec.occ.core.readonly.ReadOnly;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.namics.distrelec.b2b.facades.order.data.DistPaymentModeData;

import de.hybris.platform.commercewebservicescommons.dto.order.PaymentModeWsDTO;
import de.hybris.platform.core.model.order.payment.PaymentModeModel;
import de.hybris.platform.order.PaymentModeService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Controller
@RequestMapping(value = "/{baseSiteId}/paymentmodes")
@Tag(name = "Payment Modes")
public class PaymentModesController extends BaseController {
    @Resource(name = "dataMapper")
    private DataMapper dataMapper;

    @Resource(name = "paymentModeService")
    private PaymentModeService paymentModeService;

    @Resource(name = "paymentModeConverter")
    private Converter<PaymentModeModel, DistPaymentModeData> paymentModeConverter;

    @ReadOnly
    @Secured("ROLE_TRUSTED_CLIENT")
    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    @Operation(operationId = "getPaymentModes", summary = "Gets all available payment modes.", description = "Gets all payment modes defined for the base store.")
    @ApiBaseSiteIdParam
    public List<PaymentModeWsDTO> getPaymentModes(@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) throws NullPointerException {
        List<DistPaymentModeData> result = paymentModeConverter.convertAll(paymentModeService.getAllPaymentModes());
        if (CollectionUtils.isNotEmpty(result)) {
            return getDataMapper().mapAsList(result, PaymentModeWsDTO.class, fields);
        } else {
            throw new NullPointerException("available payment modes not found!");
        }
    }
}
