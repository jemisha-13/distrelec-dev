package com.namics.distrelec.b2b.facades.order.converters;

import com.distrelec.webservice.if15.v1.CurrencyCode;
import com.distrelec.webservice.if15.v1.OrderSearchRequestV2;
import com.namics.distrelec.b2b.core.inout.erp.util.SoapConversionHelper;
import com.namics.distrelec.b2b.core.model.DistOrderStatusModel;
import com.namics.distrelec.b2b.core.service.codelist.DistrelecCodelistService;
import com.namics.distrelec.b2b.core.service.search.pagedata.DistOrderHistoryPageableData;
import com.namics.distrelec.b2b.core.service.user.DistUserService;
import com.namics.distrelec.b2b.facades.order.Constants;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import org.apache.commons.lang.StringUtils;

import static org.apache.commons.lang.StringUtils.EMPTY;

public class SapSearchOrderRequestConverter extends AbstractPopulatingConverter<DistOrderHistoryPageableData, OrderSearchRequestV2> {

    public static final String ALL = "ALL";

    private DistUserService distUserService;

    private DistrelecCodelistService codelistService;

    @Override
    public void populate(final DistOrderHistoryPageableData source, final OrderSearchRequestV2 prototype) {
        final B2BCustomerModel user = distUserService.getCustomerByContactId(source.getContactId(), B2BCustomerModel.class);
        final B2BUnitModel currentUnit = user.getDefaultB2BUnit();
        final String salesOrgCode = currentUnit.getSalesOrg().getCode();
        final String customerId = currentUnit.getErpCustomerID();

        prototype.setSalesOrganization(salesOrgCode);
        prototype.setCustomerId(customerId);

        if (StringUtils.isNotBlank(source.getFilterContactId())) {
            prototype.setFilterContactId(source.getFilterContactId());
        } else {
            prototype.setFilterContactId(user.isShowAllOrderhistory() ? null : source.getContactId());
        }

        prototype.setFilterArticleId(source.getProductNumber() == null ? EMPTY : source.getProductNumber());
        prototype.setFilterInvoiceId(source.getInvoiceNumber() == null ? EMPTY : source.getInvoiceNumber());
        prototype.setFilterOrderId(source.getOrderNumber() == null ? EMPTY : source.getOrderNumber());
        prototype.setFilterOrderReference(source.getReference() == null ? EMPTY : source.getReference());

        if (source.getFilterCurrencyCode() != null) {
            prototype.setFilterCurrencyCode(CurrencyCode.valueOf(source.getFilterCurrencyCode()));
        }

        prototype.setFilterOrderDateStart(SoapConversionHelper.convertDate(source.getFromDate()));
        prototype.setFilterOrderDateEnd(SoapConversionHelper.convertDate(source.getToDate()));
        prototype.setFilterOrderTotalMin(source.getMinTotal());
        prototype.setFilterOrderTotalMax(source.getMaxTotal());

        if (source.getStatus() == null || ALL.equalsIgnoreCase(source.getStatus())) {
            prototype.setFilterOrderStatus(null);
        } else {
            try {
                final DistOrderStatusModel orderStatus = codelistService.getDistOrderStatusForHybrisOrderStatusCode(source.getStatus());
                prototype.setFilterOrderStatus(orderStatus != null ? orderStatus.getCode() : null);
            } catch (final Exception ex) {
                prototype.setFilterOrderStatus(null);
            }
        }

        prototype.setResultOffset(SoapConversionHelper.getResultOffsetFromPaginationData(source));
        prototype.setResultSize(SoapConversionHelper.getResultSizeFromPaginationData(source));
        prototype.setSortAscending("ASC".equalsIgnoreCase(source.getSortType()));
        prototype.setSortCriteria(getSortCriteria(source.getSort()));
    }

    private String getSortCriteria(final String sort) {
        if (StringUtils.isNotBlank(sort)) {
            if (sort.equalsIgnoreCase("byStatus")) {
                return Constants.ORDER_STATUS;
            } else if (sort.equalsIgnoreCase("byTotalPrice")) {
                return Constants.ORDER_TOTAL;
            }
        }

        return Constants.ORDER_DATE;
    }

    public void setDistUserService(final DistUserService distUserService) {
        this.distUserService = distUserService;
    }

    public void setCodelistService(final DistrelecCodelistService codelistService) {
        this.codelistService = codelistService;
    }

}
