package com.namics.distrelec.b2b.facades.order.invoice.converters;

import com.distrelec.webservice.sap.v1.CurrencyCode;
import com.distrelec.webservice.sap.v1.InvoiceSearchRequest;
import com.distrelec.webservice.sap.v1.InvoiceStatus;
import com.distrelec.webservice.sap.v1.ObjectFactory;
import com.namics.distrelec.b2b.core.inout.erp.util.SoapConversionHelper;
import com.namics.distrelec.b2b.core.service.salesorg.DistSalesOrgService;
import com.namics.distrelec.b2b.core.service.search.pagedata.DistInvoiceHistoryPageableData;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import java.util.HashMap;
import java.util.Map;
public class DistInvoiceSearchRequestConverter implements Converter<DistInvoiceHistoryPageableData, InvoiceSearchRequest> {

    private static final Map<String, String> SORT_CRETERIA_MAP = new HashMap<String, String>();
    static {
        SORT_CRETERIA_MAP.put("byDate", "CREATIONDATE");
        SORT_CRETERIA_MAP.put("byTotalPrice", "TOTAL");
        SORT_CRETERIA_MAP.put("byStatus", "STATUS");
    }

    private UserService userService;
    private DistSalesOrgService distSalesOrgService;

    // The SOAP Requests object factory
    @Autowired
    private ObjectFactory sapObjectFactory;

    @Override
    public InvoiceSearchRequest convert(final DistInvoiceHistoryPageableData source) {
        return convert(source, sapObjectFactory.createInvoiceSearchRequest());
    }

    @Override
    public InvoiceSearchRequest convert(final DistInvoiceHistoryPageableData source, final InvoiceSearchRequest prototype) {
        final B2BCustomerModel currentUser = (B2BCustomerModel) getUserService().getCurrentUser();

        // Customer data
        prototype.setSalesOrganization(getDistSalesOrgService().getCurrentSalesOrg().getCode());
        prototype.setCustomerId(currentUser.getDefaultB2BUnit().getErpCustomerID());
        prototype.setFilterContactId(currentUser.isShowAllOrderhistory() ? null : source.getContactId());

        // Invoice - Order filters
        prototype.setFilterInvoiceId(source.getInvoiceNumber());
        prototype.setFilterOrderId(source.getOrderNumber());
        prototype.setFilterCurrencyCode(CurrencyCode.valueOf(currentUser.getSessionCurrency().getIsocode()));

        // Date filter
        prototype.setFilterInvoiceDateStart(SoapConversionHelper.convertDate(source.getFromDate()));
        prototype.setFilterInvoiceDateEnd(SoapConversionHelper.convertDate(source.getToDate()));

        // Total filter
        prototype.setFilterInvoiceTotalMin(source.getMinTotal());
        prototype.setFilterInvoiceTotalMax(source.getMaxTotal());

        if (source.getStatus() != null && !source.getStatus().equals("ALL")) {
            prototype.setFilterInvoiceStatus(InvoiceStatus.valueOf(source.getStatus()));
        }

        prototype.setResultOffset(SoapConversionHelper.getResultOffsetFromPaginationData(source));
        prototype.setResultSize(SoapConversionHelper.getResultSizeFromPaginationData(source));

        final boolean sortTypeAsc = source.getSortType() == null || source.getSortType().equals("ASC") ? true : false;
        prototype.setSortAscending(sortTypeAsc);

        if (source.getSort() != null) {
            prototype.setSortCriteria(SORT_CRETERIA_MAP.get(source.getSort()));
        }
        return prototype;
    }

    public UserService getUserService() {
        return userService;
    }

    @Required
    public void setUserService(final UserService userService) {
        this.userService = userService;
    }

    public DistSalesOrgService getDistSalesOrgService() {
        return distSalesOrgService;
    }

    @Required
    public void setDistSalesOrgService(final DistSalesOrgService distSalesOrgService) {
        this.distSalesOrgService = distSalesOrgService;
    }
}
