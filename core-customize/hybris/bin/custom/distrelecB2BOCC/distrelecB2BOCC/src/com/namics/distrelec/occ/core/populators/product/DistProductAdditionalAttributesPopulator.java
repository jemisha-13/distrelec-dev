package com.namics.distrelec.occ.core.populators.product;

import java.util.List;

import org.springframework.util.CollectionUtils;

import com.namics.distrelec.b2b.core.constants.DistConfigConstants;
import com.namics.distrelec.b2b.core.model.DistSalesStatusModel;
import com.namics.distrelec.b2b.core.service.product.DistProductService;
import com.namics.distrelec.b2b.core.service.site.DistrelecCMSSiteService;
import com.namics.distrelec.b2b.core.util.ErpStatusUtil;

import de.hybris.platform.commercefacades.product.converters.populator.AbstractProductPopulator;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

/**
 * This populator will populate the ProductData which are being done in ProductPageController
 * 
 * @param <SOURCE>
 * @param <TARGET>
 */
public class DistProductAdditionalAttributesPopulator<SOURCE extends ProductModel, TARGET extends ProductData> extends
                                                     AbstractProductPopulator<SOURCE, TARGET> {

    private DistProductService productService;

    private DistrelecCMSSiteService cmsSiteService;

    private ConfigurationService configurationService;

    private ErpStatusUtil erpStatusUtil;

    @Override
    public void populate(SOURCE source, TARGET target) throws ConversionException {
        Boolean phaseOutFlag = Boolean.FALSE;
        if (!cmsSiteService.isViewedInSharedInternationalSite()) {
            phaseOutFlag = populatePhaseOutFlag(source, phaseOutFlag);
        }
        target.setBuyablePhaseoutProduct(phaseOutFlag);
    }

    private Boolean populatePhaseOutFlag(SOURCE source, Boolean phaseOutFlag) {
        DistSalesStatusModel distSalesStatusModel = getProductService().getProductSalesStatusModel(source);
        final List<String> notforsale = getErpStatusUtil().getErpSalesStatusFromConfiguration(DistConfigConstants.ErpSalesStatus.ATTRIBUTE_NO_PRODUCT_FOR_SALE_GENERIC);
        if (distSalesStatusModel != null && CollectionUtils.containsInstance(notforsale, distSalesStatusModel.getCode())) {
            phaseOutFlag = Boolean.TRUE;
        }
        return phaseOutFlag;
    }

    public DistProductService getProductService() {
        return productService;
    }

    public void setProductService(DistProductService productService) {
        this.productService = productService;
    }

    public DistrelecCMSSiteService getCmsSiteService() {
        return cmsSiteService;
    }

    public void setCmsSiteService(DistrelecCMSSiteService cmsSiteService) {
        this.cmsSiteService = cmsSiteService;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public ErpStatusUtil getErpStatusUtil() {
        return erpStatusUtil;
    }

    public void setErpStatusUtil(ErpStatusUtil erpStatusUtil) {
        this.erpStatusUtil = erpStatusUtil;
    }

}
