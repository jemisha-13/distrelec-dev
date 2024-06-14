package com.namics.distrelec.b2b.core.dataimport.batch.converter.impl;

import static com.namics.distrelec.b2b.core.constants.DistConstants.Catalog.DISTRELEC_PRODUCT_CATALOG_ID;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.service.product.dao.DistProductDao;

import de.hybris.platform.catalog.CatalogVersionService;

public class DistErpSalesStatusConverter extends DefaultDistImpexConverter {

    private static final Logger LOG = LogManager.getLogger(DistErpSalesStatusConverter.class);

    private final String STATUS_60 = "60";

    private final String STATUS_61 = "61";

    private final String STATUS_62 = "62";

    private final String BIZ_SALES_ORG = "7801";

    private final String MVIEW_BTR = "BTR";

    private static final String ONLINE_CATALOG_VERSION = "Online";

    @Autowired
    private DistProductDao productDao;

    @Autowired
    private CatalogVersionService catalogVersionService;

    @Override
    public String convert(final Map<Integer, String> row, final Long sequenceId, final String erpSequenceId) {
        final String productCode = row.get(0);
        final String salesOrg = row.get(1);
        final String status = row.get(2);
        final String mView = row.get(9);

        if (isEolStatus(status) && !productSalesStatusShouldBeUpdated(productCode, salesOrg, status)) {
            return EMPTY;
        }

        if (isSalesOrgBlockedForBtrProducts(salesOrg, mView)) {
            return EMPTY;
        }

        return super.convert(row, sequenceId, erpSequenceId);
    }

    private boolean isEolStatus(String status) {
        boolean isEol = STATUS_60.equals(status) || STATUS_61.equals(status) || STATUS_62.equals(status);
        if (isEol) {
            catalogVersionService.setSessionCatalogVersion(DISTRELEC_PRODUCT_CATALOG_ID, ONLINE_CATALOG_VERSION);
        }
        return isEol;
    }

    private boolean productSalesStatusShouldBeUpdated(String productCode, String salesOrg, String status) {
        boolean shouldUpdateSalesOrgRecord = productDao.containsSalesStatusForProductWhichIsNotInStatus(productCode, salesOrg, status);
        if (!shouldUpdateSalesOrgRecord) {
            LOG.warn("Skip sales org product update for product {} and sales org {}", productCode, salesOrg);
        }
        return shouldUpdateSalesOrgRecord;
    }

    private boolean isSalesOrgBlockedForBtrProducts(String salesOrg, String mView) {
        // Block Waldom products (with "BTR" as mView flag's value) from importing to BIZ shop
        // https://jira.distrelec.com/browse/DISTRELEC-34417
        // https://jira.distrelec.com/browse/DISTRELEC-34232
        return BIZ_SALES_ORG.equalsIgnoreCase(salesOrg) && MVIEW_BTR.equalsIgnoreCase(mView);
    }

}
