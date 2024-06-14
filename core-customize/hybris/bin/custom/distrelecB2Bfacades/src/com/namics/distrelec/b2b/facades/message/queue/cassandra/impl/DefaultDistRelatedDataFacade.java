/*
 * Copyright 2000-2017 Distrelec AG. All rights reserved.
 */
package com.namics.distrelec.b2b.facades.message.queue.cassandra.impl;

import com.namics.distrelec.b2b.core.internal.link.service.DistInternalLinkService;
import com.namics.distrelec.b2b.core.message.queue.Constants;
import com.namics.distrelec.b2b.core.message.queue.model.*;
import com.namics.distrelec.b2b.core.model.DistManufacturerModel;
import com.namics.distrelec.b2b.core.service.category.DistCategoryService;
import com.namics.distrelec.b2b.core.service.manufacturer.DistManufacturerService;
import com.namics.distrelec.b2b.core.service.product.DistProductService;
import com.namics.distrelec.b2b.facades.message.queue.DistSimpleMessageFacade;
import com.namics.distrelec.b2b.facades.message.queue.cassandra.DistRelatedDataFacade;
import com.namics.distrelec.b2b.facades.message.queue.cassandra.ILCodeHelper;
import com.namics.distrelec.b2b.facades.message.queue.data.RelatedData;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.i18n.I18NService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;
import java.util.TreeMap;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

/**
 * {@code DefaultDistRelatedDataFacade}
 *
 * @author <a href="abhinay.jadhav@distrelec.com">Abhinay Jadhav</a>, Distrelec
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 6.1
 */
public class DefaultDistRelatedDataFacade implements DistRelatedDataFacade {

    private final static Logger LOG = LogManager.getLogger(DefaultDistRelatedDataFacade.class);

    // configuration parameter to enable reading the related data
    private static final String CFG_FEATURE_RELATEDDATA_ENABLE_READ = "feature.relateddata.enable.read";

    // configuration parameter to enable reading the related data
    private static final String CFG_FEATURE_RELATEDDATA_ENABLE_WRITE = "feature.relateddata.enable.write";

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private CommonI18NService commonI18NService;

    @Autowired
    private CMSSiteService cmsSiteService;

    @Autowired
    private DistSimpleMessageFacade distSimpleMessageFacade;

    @Autowired
    private DistProductService productService;

    @Autowired
    private DistCategoryService categoryService;

    @Autowired
    private DistManufacturerService manufacturerService;

    @Autowired
    private I18NService i18NService;

    @Autowired
    private DistInternalLinkService distInternalLinkService;

    /*
     * (non-Javadoc)
     *
     * @see com.namics.distrelec.b2b.facades.message.queue.cassandra.DistRelatedDataFacade#findProductRelatedData(java.lang.String)
     */
    @Override
    public RelatedData findProductRelatedData(final String code) {
        final ProductModel product = getProductService().getProductForCode(code);
        final CategoryModel category = product.getPrimarySuperCategory() != null ? product.getPrimarySuperCategory()
                : (CollectionUtils.isNotEmpty(product.getSupercategories()) ? product.getSupercategories().iterator().next() : null);

        if (category == null || product.getManufacturer() == null) {
            LOG.warn(category == null ? "No Category defined for product {}" : "No manufacturer defined for product {}", product.getCode());
            return null;
        }

        return findRelatedData(ILCodeHelper.getCode(product), RowType.PRODUCT, product.getCode());
    }

    /*
     * (non-Javadoc)
     *
     * @see com.namics.distrelec.b2b.facades.message.queue.cassandra.DistRelatedDataFacade#findCategoryRelatedData(java.lang.String)
     */
    @Override
    public RelatedData findCategoryRelatedData(final String code) {
        final CategoryModel category = getCategoryService().getCategoryForCode(code);
        return findRelatedData(ILCodeHelper.getCode(category), RowType.CATEGORY, null);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.namics.distrelec.b2b.facades.message.queue.cassandra.DistRelatedDataFacade#findManufacturerRelatedData(java.lang.String)
     */
    @Override
    public RelatedData findManufacturerRelatedData(final String code) {
        final DistManufacturerModel manufacturer = getManufacturerService().getManufacturerByCode(code);
        return findRelatedData(ILCodeHelper.getCode(manufacturer), RowType.MANUFACTURER, null);
    }

    /**
     * Fetch the related data of the object model, which can be either a {@link ProductModel}, {@link CategoryModel} or
     * {@link DistManufacturerModel}
     *
     * @param code
     *            the object model code.
     * @param type
     *            the type of the object model
     * @param originalCode
     * @return the {@link RelatedData} relative to the object model given by its code.
     */
    private RelatedData findRelatedData(final String code, final RowType type, final String originalCode) {
        final String site = cmsSiteService.getCurrentSite().getUid();
        final String language = i18NService.getCurrentLocale().getLanguage();
        // Fetch related products data

        // WARNING: if read is disable and write enabled, every request will send a message
        final boolean is_read_enabled = getConfigurationService().getConfiguration().getBoolean(CFG_FEATURE_RELATEDDATA_ENABLE_READ, false);
        final boolean is_write_enabled = getConfigurationService().getConfiguration().getBoolean(CFG_FEATURE_RELATEDDATA_ENABLE_WRITE, false);

        CInternalLinkData iLinkData = null;
        if (is_read_enabled) {
            LOG.debug("Fetch related data code: {}, originalCode: {}, site: {}, type:{}, lang:{} ", code, originalCode, site, type.getCode(), language);
            iLinkData = getDistInternalLinkService().findInternalLink(code, site, type.getCode(), language);
        }

        // We send an asynchronous message in one of the following cases:
        // 1) the data found does not contain any relevant information
        // 2) the data found does not have a timestamp or the timestamp is older than 30 days ago.
        final int MAX_AGE = getConfigurationService().getConfiguration().getInt(Constants.IL_MAX_AGE_DAYS, 30);
        if (iLinkData == null || CollectionUtils.isEmpty(iLinkData.getDatas()) //
                || iLinkData.getTimestamp() == null //
                || iLinkData.getTimestamp().before(DateUtils.addDays(new Date(), -MAX_AGE))) {
            if (is_write_enabled) {
                String eventCode = StringUtils.isNotBlank(originalCode) ? originalCode : code;
                getDistSimpleMessageFacade().sendMessage(eventCode, type);
                if (is_read_enabled && iLinkData == null) {
                    LOG.debug("Fetch related data code: {}, originalCode: {}, site: {}, type:{}, lang:{} ", code, originalCode, site, type.getCode(), language);
                    iLinkData = getDistInternalLinkService().findInternalLink(code, site, type.getCode(), language);
                }
            }
        }

        final RelatedData relatedData = (iLinkData == null || CollectionUtils.isEmpty(iLinkData.getDatas())) ? null
                : new RelatedData(type, iLinkData.getDatas().stream().collect(groupingBy(CRelatedData::getType, TreeMap::new, toList())));

        if (RowType.PRODUCT == type && //
                relatedData != null && relatedData.getRelatedDataMap() != null && //
                relatedData.getRelatedDataMap().containsKey(RelatedDataType.RELATED_PRODUCT)) {
            final List<CRelatedData> relatedProductList = relatedData.getRelatedDataMap().get(RelatedDataType.RELATED_PRODUCT);
            if (relatedProductList != null && !relatedProductList.isEmpty()) {
                final int maxLinks = getConfigurationService().getConfiguration().getInt(Constants.IL_MAX_LINKS_KEY, 5); // Default value 5
                final List<CIValue> ciValues = relatedProductList.get(0).getValues();
                ciValues.removeIf(ciValue -> ciValue.getUrl().endsWith(StringUtils.isNotBlank(originalCode) ? originalCode : code));
                if (ciValues.size() > maxLinks) {
                    relatedProductList.get(0).setValues(ciValues.stream().limit(maxLinks).collect(toList()));
                }
            }
        }

        return relatedData;
    }

    public DistSimpleMessageFacade getDistSimpleMessageFacade() {
        return distSimpleMessageFacade;
    }

    public void setDistSimpleMessageFacade(final DistSimpleMessageFacade distSimpleMessageFacade) {
        this.distSimpleMessageFacade = distSimpleMessageFacade;
    }

    public CommonI18NService getCommonI18NService() {
        return commonI18NService;
    }

    public void setCommonI18NService(final CommonI18NService commonI18NService) {
        this.commonI18NService = commonI18NService;
    }

    public CMSSiteService getCmsSiteService() {
        return cmsSiteService;
    }

    public void setCmsSiteService(final CMSSiteService cmsSiteService) {
        this.cmsSiteService = cmsSiteService;
    }

    public DistProductService getProductService() {
        return productService;
    }

    public void setProductService(final DistProductService productService) {
        this.productService = productService;
    }

    public DistCategoryService getCategoryService() {
        return categoryService;
    }

    public void setCategoryService(final DistCategoryService categoryService) {
        this.categoryService = categoryService;
    }

    public DistManufacturerService getManufacturerService() {
        return manufacturerService;
    }

    public void setManufacturerService(final DistManufacturerService manufacturerService) {
        this.manufacturerService = manufacturerService;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public DistInternalLinkService getDistInternalLinkService() {
        return distInternalLinkService;
    }

    public void setDistInternalLinkService(final DistInternalLinkService distInternalLinkService) {
        this.distInternalLinkService = distInternalLinkService;
    }
}
