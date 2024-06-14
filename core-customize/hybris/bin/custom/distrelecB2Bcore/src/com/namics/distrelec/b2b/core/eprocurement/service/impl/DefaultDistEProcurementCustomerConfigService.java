/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.eprocurement.service.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.namics.distrelec.b2b.core.service.classification.DistFeatureValueConversionService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ExpressionException;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import com.namics.distrelec.b2b.core.enums.DistFieldLocation;
import com.namics.distrelec.b2b.core.eprocurement.service.DistEProcurementCustomerConfigService;
import com.namics.distrelec.b2b.core.model.eprocurement.AribaCustomerConfigModel;
import com.namics.distrelec.b2b.core.model.eprocurement.DistCustomerConfigModel;
import com.namics.distrelec.b2b.core.model.eprocurement.DistFieldConfigModel;
import com.namics.distrelec.b2b.core.model.eprocurement.OCICustomerConfigModel;
import com.namics.distrelec.b2b.core.model.eprocurement.OciShippingConfigModel;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.classification.ClassificationService;
import de.hybris.platform.classification.features.Feature;
import de.hybris.platform.classification.features.FeatureList;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.internal.service.AbstractBusinessService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.user.UserService;

/**
 * Default implementation for <code>DistEProcurementCustomerConfigService</code>.
 * 
 * @author pbueschi, Namics AG
 */
public class DefaultDistEProcurementCustomerConfigService extends AbstractBusinessService implements DistEProcurementCustomerConfigService {

    private static final Logger LOG = LogManager.getLogger(DefaultDistEProcurementCustomerConfigService.class);

    @Autowired
    private UserService userService;

    @Autowired
    private FlexibleSearchService flexibleSearchService;

    @Autowired
    private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService;

    @Autowired
    private ClassificationService classificationService;

    @Autowired
    private DistFeatureValueConversionService featureConversionService;

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.eprocurement.service.DistEProcurementCustomerConfigService#getCustomerConfig()
     */
    @Override
    public DistCustomerConfigModel getCustomerConfig() {
        final UserModel currentUser = userService.getCurrentUser();
        if (currentUser != null) {
            try {
                final B2BUnitModel company = b2bUnitService.getParent((B2BCustomerModel) currentUser);
                return getCustomerConfigForCompany(company);
            } catch (final Exception e) {
                // company without customerConfig is possible
                return null;
            }
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.eprocurement.service.DistEProcurementCustomerConfigService#getCustomerConfigForCompany(de.hybris.
     * platform.b2b.model.B2BUnitModel)
     */
    @Override
    public DistCustomerConfigModel getCustomerConfigForCompany(final B2BUnitModel company) {
        if (company != null) {
            try {
                final DistCustomerConfigModel distCustomerConfigExample = new DistCustomerConfigModel();
                distCustomerConfigExample.setCompany(company);
                return flexibleSearchService.getModelByExample(distCustomerConfigExample);
            } catch (final Exception e) {
                // company without customerConfig is possible
                return null;
            }
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.eprocurement.service.DistEProcurementCustomerConfigService#getAllowedCategories()
     */
    @Override
    public List<CategoryModel> getAllowedCategories() {
        final DistCustomerConfigModel distCustomerConfig = getCustomerConfig();
        if (distCustomerConfig == null || !BooleanUtils.isTrue(distCustomerConfig.getRestrictCatalogs())
                || CollectionUtils.isEmpty(distCustomerConfig.getAllowedCategories())) {
            return Collections.EMPTY_LIST;
        }

        return distCustomerConfig.getAllowedCategories();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.eprocurement.service.DistEProcurementCustomerConfigService#getFieldConfigs()
     */
    @Override
    public Set<DistFieldConfigModel> getFieldConfigs() {
        final DistCustomerConfigModel distCustomerConfig = getCustomerConfig();
        if (distCustomerConfig == null || CollectionUtils.isEmpty(distCustomerConfig.getFieldConfigs())) {
            return Collections.EMPTY_SET;
        }

        return distCustomerConfig.getFieldConfigs();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.namics.distrelec.b2b.core.eprocurement.service.DistEProcurementCustomerConfigService#getFieldConfigsForProduct(de.hybris.platform
     * .core.model.product.ProductModel)
     */
    @Override
    public Map<String, String> getFieldConfigsForProduct(final ProductModel product) {
        final Set<DistFieldConfigModel> fieldConfigs = getFieldConfigs();
        if (CollectionUtils.isEmpty(fieldConfigs)) {
            return Collections.EMPTY_MAP;
        }

        final Map<String, String> fieldConfigsForProduct = new HashMap<String, String>();
        fieldConfigs.forEach(fieldConfig -> {
            String fieldValue = StringUtils.EMPTY;
            if (!BooleanUtils.isTrue(fieldConfig.getDynamic())) {
                fieldValue = fieldConfig.getParameter();
            } else {
                if (DistFieldLocation.PRODUCT.equals(fieldConfig.getLocation())) {
                    fieldValue = getDynamicAttributeValue(product, fieldConfig.getLocation(), fieldConfig.getParameter());
                } else if (DistFieldLocation.CLASSIFICATION.equals(fieldConfig.getLocation())) {
                    fieldValue = getClassificationAttributeValue(product, fieldConfig.getParameter());
                }
            }
            fieldConfigsForProduct.put(fieldConfig.getDomain(), fieldValue);
        });

        return fieldConfigsForProduct;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.eprocurement.service. DistEProcurementCustomerConfigService#getHeaderFields()
     */
    @Override
    public Map<String, String> getHeaderFields() {
        final DistCustomerConfigModel distCustomerConfig = getCustomerConfig();
        if (distCustomerConfig == null || CollectionUtils.isEmpty(distCustomerConfig.getFieldConfigs())) {
            return Collections.EMPTY_MAP;
        }

        return distCustomerConfig.getFieldConfigs().stream() //
                .filter(fieldConfig -> BooleanUtils.isTrue(fieldConfig.getHeader())) //
                .collect(Collectors.toMap(DistFieldConfigModel::getDomain, DistFieldConfigModel::getParameter));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.eprocurement.service.DistEProcurementCustomerConfigService#getFieldConfigDomainNames()
     */
    @Override
    public List<String> getFieldConfigDomainNames() {
        final DistCustomerConfigModel distCustomerConfig = getCustomerConfig();
        if (distCustomerConfig == null || CollectionUtils.isEmpty(distCustomerConfig.getFieldConfigs())) {
            return Collections.EMPTY_LIST;
        }

        return distCustomerConfig.getFieldConfigs().stream() //
                .filter(fieldConfig -> !BooleanUtils.isTrue(fieldConfig.getHeader())) //
                .map(DistFieldConfigModel::getDomain) //
                .collect(Collectors.toList());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.eprocurement.service.DistEProcurementCustomerConfigService#getFieldsForShippingProduct()
     */
    @Override
    public Map<String, String> getFieldsForShippingProduct() {
        final OciShippingConfigModel shippingConfig = ((OCICustomerConfigModel) getCustomerConfig()).getShippingConfig();
        final Map<String, String> fieldsForShippingProduct = new HashMap<String, String>();
        // INDIV_OCI_SHIPPING_ARTNR -> product.code
        // INDIV_OCI_SHIPPING_TEXT -> product.name
        fieldsForShippingProduct.put("INDIV_CHARGE_CLASSIFICATION_CODE", shippingConfig.getClassification());
        return fieldsForShippingProduct;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.eprocurement.service.DistEProcurementCustomerConfigService#hasShippingProduct()
     */
    @Override
    public boolean hasShippingProduct() {
        final DistCustomerConfigModel customerConfig = getCustomerConfig();
        if (customerConfig != null && customerConfig instanceof OCICustomerConfigModel) {
            final OCICustomerConfigModel ociCustomerConfig = (OCICustomerConfigModel) customerConfig;
            if (BooleanUtils.isTrue(ociCustomerConfig.getUseShipping()) && ociCustomerConfig.getShippingConfig() != null) {
                return true;
            }
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.eprocurement.service. DistEProcurementCustomerConfigService#hasMegaFlyOutDisabled()
     */
    @Override
    public boolean hasMegaFlyOutDisabled() {
        final DistCustomerConfigModel customerConfig = getCustomerConfig();
        return (customerConfig != null && customerConfig instanceof OCICustomerConfigModel)
                ? BooleanUtils.isTrue(((OCICustomerConfigModel) customerConfig).getDisableMegaFlyOut()) : false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.eprocurement.service. DistEProcurementCustomerConfigService#openInNewWindow()
     */
    @Override
    public boolean openInNewWindow() {
        return isOpenInnewWindowEnabledForCustomer(getCustomerConfig());
    }

    private boolean isOpenInnewWindowEnabledForCustomer(final DistCustomerConfigModel customerConfig) {
        if (customerConfig instanceof OCICustomerConfigModel) {
            return BooleanUtils.isTrue(((OCICustomerConfigModel) customerConfig).getOpenInNewWindow());
        }
        if (customerConfig instanceof AribaCustomerConfigModel) {
            return BooleanUtils.isTrue(((AribaCustomerConfigModel) customerConfig).getOpenInNewWindow());
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.eprocurement.service.DistEProcurementCustomerConfigService#getDefaultRedirectURL()
     */
    @Override
    public String getDefaultRedirectURL() {
        final DistCustomerConfigModel customerConfig = getCustomerConfig();
        return (customerConfig != null && customerConfig instanceof OCICustomerConfigModel) ? ((OCICustomerConfigModel) customerConfig).getDefaultRedirectURL()
                : null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.namics.distrelec.b2b.core.eprocurement.service.DistEProcurementCustomerConfigService#isShippingProduct(de.hybris.platform.core.
     * model.product.ProductModel)
     */
    @Override
    public boolean isShippingProduct(final ProductModel product) {
        return getModelService().isNew(product);
    }

    /**
     * Gets value for dynamic attribute on provided field location.
     * 
     * @param product
     * @param location
     * @param expression
     * @return dynamic attribute value
     */
    private String getDynamicAttributeValue(final ProductModel product, final DistFieldLocation location, final String expression) {
        String value = null;
        if (product != null && location != null && StringUtils.isNotBlank(expression)) {
            final ExpressionParser parser = new SpelExpressionParser();
            final StandardEvaluationContext evalCtx = new StandardEvaluationContext();
            final String itemType = location.getCode().toLowerCase();
            evalCtx.setVariable(itemType, product);
            try {
                value = (String) parser.parseExpression("#" + itemType + "." + expression).getValue(evalCtx);
            } catch (final ExpressionException e) {
                LOG.info("Could not parse Expression: " + expression, e);
            }
        }
        return StringUtils.defaultString(value);
    }

    protected String getClassificationAttributeValue(final ProductModel product, final String parameter) {
        final FeatureList features = classificationService.getFeatures(product);
        final ClassAttributeAssignmentModel classAttribute = features.getClassAttributeAssignments().stream()
                .filter(assignement -> assignement.getClassificationAttribute() != null
                        && StringUtils.equalsIgnoreCase(parameter, assignement.getClassificationAttribute().getCode())) //
                .findFirst().orElse(null);
        if (classAttribute == null) {
            return StringUtils.EMPTY;
        }
        
        final Feature feature = features.getFeatureByAssignment(classAttribute);
        return (feature == null || feature.getValue() == null || feature.getValue().getValue() == null) ? StringUtils.EMPTY
                : StringUtils.defaultIfBlank(featureConversionService.toString(feature.getValue()), StringUtils.EMPTY);
    }

    // Getters & Setters

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(final UserService userService) {
        this.userService = userService;
    }

    public FlexibleSearchService getFlexibleSearchService() {
        return flexibleSearchService;
    }

    public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }

    public B2BUnitService<B2BUnitModel, B2BCustomerModel> getB2bUnitService() {
        return b2bUnitService;
    }

    public void setB2bUnitService(final B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService) {
        this.b2bUnitService = b2bUnitService;
    }

    public ClassificationService getClassificationService() {
        return classificationService;
    }

    public void setClassificationService(final ClassificationService classificationService) {
        this.classificationService = classificationService;
    }

    @Override
    public boolean isCustomFooter() {
        final DistCustomerConfigModel customerConfig = getCustomerConfig();
        if (customerConfig != null) {
            if (customerConfig instanceof OCICustomerConfigModel) {
                return BooleanUtils.isTrue(((OCICustomerConfigModel) customerConfig).isCustomFooter());
            }
            if (customerConfig instanceof AribaCustomerConfigModel) {
                return BooleanUtils.isTrue(((AribaCustomerConfigModel) customerConfig).isCustomFooter());
            }
        }
        return false;
    }
}
