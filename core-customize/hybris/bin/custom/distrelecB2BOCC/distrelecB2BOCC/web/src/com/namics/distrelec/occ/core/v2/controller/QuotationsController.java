package com.namics.distrelec.occ.core.v2.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.ws.rs.ForbiddenException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import com.namics.distrelec.b2b.facades.customer.DistCustomerFacade;
import com.namics.distrelec.b2b.facades.order.quotation.data.QuotationData;
import com.namics.distrelec.b2b.facades.order.quotation.data.QuoteStatusData;
import com.namics.distrelec.occ.core.security.SecuredAccessConstants;
import com.namics.distrelec.occ.core.v2.annotations.B2ERestricted;
import com.namics.distrelec.occ.core.v2.annotations.QuotationRestriction;
import com.namics.distrelec.occ.core.v2.enums.ShowMode;
import com.namics.distrelec.occ.core.v2.forms.QuotationHistoryForm;
import com.namics.distrelec.occ.core.v2.helper.OrdersHelper;
import com.namics.distrelec.occ.core.validator.QuotationHistoryFormValidator;

import de.hybris.platform.commercewebservicescommons.dto.order.QuotationHistoriesWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.QuotationHistoryWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.QuoteStatusWsDTO;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdAndUserIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(value = "/{baseSiteId}/users/{userId}/quotations")
@CacheControl(directive = CacheControlDirective.PRIVATE)
@Tag(name = "Quotations")
public class QuotationsController extends BaseCommerceController {
    private static final String DEFAULT_SHOW_MODE = "PAGE";

    @Autowired
    private DistCustomerFacade customerFacade;

    @Autowired
    private OrdersHelper ordersHelper;

    @Resource(name = "quotationHistoryFormValidator")
    private QuotationHistoryFormValidator quotationHistoryFormValidator;

    @Secured({ SecuredAccessConstants.ROLE_B2BCUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT })
    @B2ERestricted
    @QuotationRestriction
    @RequestMapping(method = RequestMethod.POST)
    @Operation(operationId = "quotationHistory", summary = "Get quotation history for user", description = "Returns history data that is in queue")
    @ApiBaseSiteIdAndUserIdParam
    public QuotationHistoriesWsDTO getQuotationHistory(@Parameter(description = "history quotation form", required = true) @RequestBody final QuotationHistoryForm quotationHistoryForm,
                                                       @Parameter(description = "show mode of quotation", required = true) @RequestParam(defaultValue = DEFAULT_SHOW_MODE) final ShowMode showMode,
                                                       @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        final List<String> orgContactList = userFacade.getMemberCustomersForB2BUnit(customerFacade.getCurrentCustomer().getUnit().getUid());
        if (null != quotationHistoryForm.getContactId() && !quotationHistoryForm.getContactId().isEmpty()
                && !orgContactList.contains(quotationHistoryForm.getContactId())) {
            throw new ForbiddenException("User is not allowed to access");
        }
        validate(quotationHistoryForm, "quotationHistoryForm", quotationHistoryFormValidator);
        return ordersHelper.searchQuotationHistory(quotationHistoryForm, showMode, fields);
    }

    @Secured({ SecuredAccessConstants.ROLE_B2BCUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT })
    @B2ERestricted
    @QuotationRestriction
    @RequestMapping(value = "/resubmit-quotation", method = RequestMethod.POST)
    @Operation(operationId = "resubmitQuotation", summary = "Resubmit quotation", description = "Resubmits quotation")
    @ApiBaseSiteIdAndUserIdParam
    public QuoteStatusWsDTO resubmitQuotation(@Parameter(description = "Previous quote ID", required = true) @RequestParam final String previousQuoteId,
                                              @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        final QuoteStatusData quoteStatusData = ordersHelper.resubmitQuotation(previousQuoteId);
        return getDataMapper().map(quoteStatusData, QuoteStatusWsDTO.class, fields);
    }

    @Secured({ SecuredAccessConstants.ROLE_B2BCUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT })
    @B2ERestricted
    @QuotationRestriction
    @RequestMapping(value = "/{quotationCode}", method = RequestMethod.GET)
    @Operation(operationId = "quotationDetails", summary = "Get quotation details", description = "Returns quotation details from the ERP")
    @ApiBaseSiteIdAndUserIdParam
    public QuotationHistoryWsDTO getQuotationDetails(@Parameter(description = "Quotation code", required = true) @PathVariable final String quotationCode,
                                                     @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        final QuotationData quotationData = ordersHelper.getQuotationDetails(quotationCode);
        if (quotationData == null) {
            throw new UnknownIdentifierException("No quotation found with code " + quotationCode);
        }
        return getDataMapper().map(quotationData, QuotationHistoryWsDTO.class, fields);
    }
}
