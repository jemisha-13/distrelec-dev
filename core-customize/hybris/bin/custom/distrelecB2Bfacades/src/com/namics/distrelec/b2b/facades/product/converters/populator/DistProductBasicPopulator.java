/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.product.converters.populator;

import static com.namics.distrelec.b2b.facades.product.converters.populator.ProductVolumePricesPopulator.ORDER_QUANTITY_MINIMUM;
import static com.namics.distrelec.b2b.facades.product.converters.populator.ProductVolumePricesPopulator.ORDER_QUANTITY_STEP;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.namics.distrelec.b2b.core.model.DistBatteryComplianceModel;
import com.namics.distrelec.b2b.core.model.DistRestrictionOfHazardousSubstancesModel;
import com.namics.distrelec.b2b.core.model.DistSalesOrgProductModel;
import com.namics.distrelec.b2b.core.model.DistTransportGroupModel;
import com.namics.distrelec.b2b.core.reevoo.service.DistReevooService;
import com.namics.distrelec.b2b.core.service.category.DistCategoryService;
import com.namics.distrelec.b2b.core.service.codelist.DistrelecCodelistService;
import com.namics.distrelec.b2b.core.service.product.DistSalesOrgProductService;
import com.namics.distrelec.b2b.core.service.url.DistUrlResolver;
import com.namics.distrelec.b2b.facades.helper.DistDateFormatPerLocaleHelper;
import com.namics.distrelec.b2b.facades.product.converters.DistTransportGroupConverter;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.product.converters.populator.ProductBasicPopulator;
import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.product.CommerceProductService;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionHelper;
import de.hybris.platform.servicelayer.dto.converter.Converter;

/**
 * Custom implementation of ProductBasicPopulator. Some additional basic
 * attributes are set here.
 *
 * @param <SOURCE>
 * @param <TARGET>
 * @author sivakumaran, Namics AG
 * @since Namics Extensions 1.0
 */
public class DistProductBasicPopulator<SOURCE extends ProductModel, TARGET extends ProductData>
                                      extends ProductBasicPopulator<SOURCE, TARGET> {

    @Autowired
    @Qualifier("mediaContainerToImageMapConverter")
    private Converter<MediaContainerModel, Map<String, ImageData>> mediaContainerToImageMapConverter;

    @Autowired
    private DistSalesOrgProductService distSalesOrgProductService;

    @Autowired
    private DistReevooService distReevooService;

    @Autowired
    private DistTransportGroupConverter distTransportGroupConverter;

    @Autowired
    @Qualifier("distProductFamilyUrlResolver")
    private DistUrlResolver<CategoryModel> productFamilyUrlResolver;

    @Autowired
    @Qualifier("commerceProductService")
    private CommerceProductService commerceProductService;

    @Autowired
    @Qualifier("categoryConverter")
    private Converter<CategoryModel, CategoryData> categoryConverter;

    @Autowired
    @Qualifier("distCategoryService")
    private DistCategoryService distCategoryService;

    @Autowired
    private DistrelecCodelistService codeListService;

    @Autowired
    private DistDateFormatPerLocaleHelper distDateFormatPerLocaleHelper;

    protected static final Logger LOG = LogManager.getLogger(DistProductBasicPopulator.class);

    public static final String ENERGY_EFFICIENCY_KEY = "Energyclasses_LOV";

    private static final String ENERGY_CLASS_BUILTIN_LED_KEY = "Energyclasses_built-in_LED_LOV";

    private static final String ENERGY_CLASS_FITTING_KEY = "Energyclasses_fitting_LOV";

    private static final String ENERGY_CLASS_INCLUDED_BULB_KEY = "energyclasses_included_bulb_LOV";

    private static final String CALC_ENERGY_LABEL_TOP_KEY = "calc_energylabel_top_text";

    private static final String CALC_ENERGY_LABEL_BOTTOM_KEY = "calc_energylabel_bottom_text";

    private static final String ENERGY_POWER = "Leistung_W";

    private static final String ROHS_CONFORM_12 = "12";

    private static final String ROHS_CONFORM_14 = "14";

    private static final String ROHS_UNDER_REVIEW = "99";

    @Override
    public void populate(final SOURCE productModel, final TARGET productData) {
        productData.setCode((String) getProductAttribute(productModel, ProductModel.CODE));
        productData.setCodeErpRelevant((String) getProductAttribute(productModel, ProductModel.CODEERPRELEVANT));
        productData.setTypeName((String) getProductAttribute(productModel, ProductModel.TYPENAME));
        productData.setSvhc((String) getProductAttribute(productModel, ProductModel.SVHC));
        productData.setSvhcReviewDate((Date) getProductAttribute(productModel, ProductModel.SVHCREVIEWDATE));
        productData.setFormattedSvhcReviewDate(distDateFormatPerLocaleHelper.formatDateForCurrentLocale((Date) getProductAttribute(productModel,
                                                                                                                                   ProductModel.SVHCREVIEWDATE)));
        productData.setScip(productModel.getScip());
        productData.setPromotionText((String) getProductAttribute(productModel, ProductModel.PROMOTIONTEXT));
        productData.setDimensions(productModel.getDimensions());
        productData.setManufacturer(
                                    null != productModel.getManufacturer() ? productModel.getManufacturer().getName() : "");
        productData.setNameEN(productModel.getName(Locale.ENGLISH));
        productData.setDescriptionEN(productModel.getDescription(Locale.ENGLISH));
        productData.setProductFamilyNameEN(productModel.getProductFamilyName(Locale.ENGLISH));
        productData.setGrossWeight(productModel.getGrossWeight());
        productData.setGrossWeightUnit(
                                       productModel.getGrossWeightUnit() != null ? productModel.getGrossWeightUnit().getName() : null);
        productData.setOriginalPackSize(
                                        productModel.getSalesUnit() != null ? productModel.getSalesUnit().getName() : null);
        setCalibrationInformation(productData, productModel);
        DistRestrictionOfHazardousSubstancesModel rohs = (DistRestrictionOfHazardousSubstancesModel) getProductAttribute(
                                                                                                                         productModel, ProductModel.ROHS);

        if (rohs != null) {
            if (StringUtils.equals(ROHS_CONFORM_12, rohs.getCode()) || StringUtils.equals(ROHS_CONFORM_14, rohs.getCode())) {
                rohs = codeListService.getDistrelecRestrictionOfHazardousSubstances(ROHS_UNDER_REVIEW);
            }
            productData.setRohs(rohs.getRelevantName());
            productData.setRohsEN(rohs.getRelevantName(Locale.ENGLISH));
            productData.setRohsCode(rohs.getCode());
            productData.setRohsExemptions(productModel.getRohsExemptions());

            final String exemptionsFormatted = String.join("; ", productModel.getRohsExemptions());
            productData.setRohsExemptionsFormatted(exemptionsFormatted);
        }
        productData.setEligibleForReevoo(distReevooService.isProductEligibleForReevoo(productModel));
        final DistTransportGroupModel transportGroup = (DistTransportGroupModel) getProductAttribute(productModel,
                                                                                                     ProductModel.TRANSPORTGROUP);
        if (transportGroup != null) {
            productData.setTransportGroup(transportGroup.getName());
            productData.setTransportGroupData(distTransportGroupConverter.convert(transportGroup));
        }
        final DistBatteryComplianceModel batteryCompliance = (DistBatteryComplianceModel) getProductAttribute(
                                                                                                              productModel, ProductModel.BATTERYCOMPLIANCE);
        if (batteryCompliance != null) {
            productData.setBatteryComplianceCode(batteryCompliance.getCode());
        }
        addPrimaryImageAsFirst(productModel, productData);

        final DistSalesOrgProductModel salesOrgProduct = distSalesOrgProductService
                                                                                   .getCurrentSalesOrgProduct(productModel);
        if (salesOrgProduct != null) {
            if (salesOrgProduct.getSalesStatus() != null) {
                productData.setBuyable(salesOrgProduct.getSalesStatus().isBuyableInShop());
                productData.setSalesStatus(salesOrgProduct.getSalesStatus().getCode());
            }
            productData.setEndOfLifeDate(salesOrgProduct.getEndOfLifeDate());
            productData.setOrderQuantityMinimum(salesOrgProduct.getOrderQuantityMinimum());
            productData.setOrderQuantityStep(salesOrgProduct.getOrderQuantityStep());
            productData.setCustomsCode(salesOrgProduct.getCustomsCode());
            productData.setItemCategoryGroup(salesOrgProduct.getItemCategoryGroup());
        }

        if (productData.getOrderQuantityMinimum() == null || productData.getOrderQuantityMinimum() <= 0) {
            productData.setOrderQuantityMinimum(ORDER_QUANTITY_MINIMUM);
        }
        if (productData.getOrderQuantityStep() == null || productData.getOrderQuantityStep() <= 0) {
            productData.setOrderQuantityStep(ORDER_QUANTITY_STEP);
        }
        productData.setSalesUnit(productModel.getRelevantSalesUnit());
        productData.setBuyableReplacementProduct(productModel.getBuyableReplacementProduct().booleanValue());
        productData.setReplacementReason(
                                         productModel.getReplacementReason() == null ? "" : productModel.getReplacementReason().getName());

        if (productModel.getCatPlusSupplierAID() != null) {
            productData.setCatPlusSupplierAID(productModel.getCatPlusSupplierAID());
            productData.setCatPlusItem(true);
        }

        productData.setProductFamilyUrl(getProductFamilyUrl(productModel.getPimFamilyCategoryCode()));
        productData.setProductFamilyName(productModel.getProductFamilyName());

        // DISTRELEC-8887 : Display "old" article number on product detail
        // page
        productData
                   .setMovexArticleNumber(productModel.getCodeMovex() == null ? "" : productModel.getCodeMovex());
        productData.setNavisionArticleNumber(
                                             productModel.getCodeNavision() == null ? "" : productModel.getCodeNavision());
        productData.setElfaArticleNumber(productModel.getCodeElfa() == null ? "" : productModel.getCodeElfa());

        // DISTRELEC-11625
        productData.setEnumber(productModel.getEnumber() == null ? "" : productModel.getEnumber());

        productData.setAlternativeAliasMPN(productModel.getAlternativeAliasMPN());
        productData.setSignalWord(productModel.getSignalWord());

        if (null != productModel.getEnergyEffiencyLabels()) {
            final ObjectMapper objectMapper = new ObjectMapper();
            try {
                final Map<String, String> eelData = objectMapper.readValue(productModel.getEnergyEffiencyLabels(),
                                                                           Map.class);
                productData.setEnergyEfficiency(
                                                eelData.containsKey(ENERGY_EFFICIENCY_KEY) ? eelData.get(ENERGY_EFFICIENCY_KEY) : null);
                productData.setEnergyBottomText(eelData.containsKey(CALC_ENERGY_LABEL_BOTTOM_KEY)
                                                                                                  ? eelData.get(CALC_ENERGY_LABEL_BOTTOM_KEY)
                                                                                                  : null);
                productData.setEnergyPower(
                                           eelData.containsKey(ENERGY_POWER) ? eelData.get(ENERGY_POWER).toString() : null);
                productData.setEnergyTopText(
                                             eelData.containsKey(CALC_ENERGY_LABEL_TOP_KEY) ? eelData.get(CALC_ENERGY_LABEL_TOP_KEY)
                                                                                            : null);
                productData.setEnergyClassesBuiltInLed(eelData.containsKey(ENERGY_CLASS_BUILTIN_LED_KEY)
                                                                                                         ? eelData.get(ENERGY_CLASS_BUILTIN_LED_KEY)
                                                                                                         : null);
                productData.setEnergyClassesIncludedBulb(eelData.containsKey(ENERGY_CLASS_INCLUDED_BULB_KEY)
                                                                                                             ? eelData.get(ENERGY_CLASS_INCLUDED_BULB_KEY)
                                                                                                             : null);
                productData.setEnergyClassesFitting(
                                                    eelData.containsKey(ENERGY_CLASS_FITTING_KEY) ? eelData.get(ENERGY_CLASS_FITTING_KEY)
                                                                                                  : null);

                if (productModel.getEnergyEfficiencyLabelImage() != null) {
                    productData.setEnergyEfficiencyLabelImage(mediaContainerToImageMapConverter.convert(productModel.getEnergyEfficiencyLabelImage()));
                }

            } catch (final IOException ex) {
                LOG.error(ex);
            }
        }
        final Collection<CategoryModel> categories = getCommerceProductService()
                                                                                .getSuperCategoriesExceptClassificationClassesForProduct(productModel);
        productData.setCategories(getCategoryConverter().convertAll(categories));
        productData.setHasSvhc(productModel.getHasSvhc());
        productData.setAvailableInSnapEda(productModel.isAvailableInSnapEda());
        productData.setWeeeCategory((String) getProductAttribute(productModel, ProductModel.WEEECATEGORY));
        productData.setIsBetterWorld(productModel.getIsBetterWorld());

        super.populate(productModel, productData);
    }

    private void setCalibrationInformation(final ProductData productData, final SOURCE productModel) {
        if (isTrue(productModel.getCalibrationService())) {
            productData.setCalibrationService(true);
            productData.setCalibrated(productModel.isCalibrated());
            ProductModel calibratedProduct = productModel.getCalibrationProduct();

            if (calibratedProduct != null) {
                productData.setCalibrationItemArtNo(calibratedProduct.getCode());
            }
        }
    }

    protected void addPrimaryImageAsFirst(final ProductModel productModel, final ProductData productData) {
        if (productModel.getPrimaryImage() != null || productModel.getIllustrativeImage() != null
                || productModel.getGalleryImages() != null) {
            final List<Map<String, ImageData>> images = new ArrayList<>();

            if (productModel.getPrimaryImage() != null) {
                images.add(mediaContainerToImageMapConverter.convert(productModel.getPrimaryImage()));
            } else if (productModel.getIllustrativeImage() != null) {
                images.add(mediaContainerToImageMapConverter.convert(productModel.getIllustrativeImage()));
                productData.setIllustrativeImage(true);
            }

            if (CollectionUtils.isNotEmpty(productModel.getGalleryImages())) {
                ConversionHelper.convertAll(productModel.getGalleryImages(), mediaContainerToImageMapConverter, images);
            }

            productData.setProductImages(images);
            productData.setEan(productModel.getEan());

        }
    }

    protected String getProductFamilyUrl(final String pimFamilyCategoryCode) {
        if (pimFamilyCategoryCode != null) {
            Optional<CategoryModel> productFamilyOptional = getDistCategoryService().findProductFamily(pimFamilyCategoryCode);
            final boolean productFamilyValid = getDistCategoryService().hasMultipleProductsInFamily(pimFamilyCategoryCode);
            if (productFamilyOptional.isPresent() && productFamilyValid) {
                return getProductFamilyUrlResolver().resolve(productFamilyOptional.get());
            }
        }
        return null;
    }

    public Converter<MediaContainerModel, Map<String, ImageData>> getMediaContainerToImageMapConverter() {
        return mediaContainerToImageMapConverter;
    }

    public void setMediaContainerToImageMapConverter(
                                                     final Converter<MediaContainerModel, Map<String, ImageData>> mediaContainerToImageMapConverter) {
        this.mediaContainerToImageMapConverter = mediaContainerToImageMapConverter;
    }

    public DistSalesOrgProductService getDistSalesOrgProductService() {
        return distSalesOrgProductService;
    }

    public void setDistSalesOrgProductService(final DistSalesOrgProductService distSalesOrgProductService) {
        this.distSalesOrgProductService = distSalesOrgProductService;
    }

    public DistTransportGroupConverter getDistTransportGroupConverter() {
        return distTransportGroupConverter;
    }

    public void setDistTransportGroupConverter(final DistTransportGroupConverter distTransportGroupConverter) {
        this.distTransportGroupConverter = distTransportGroupConverter;
    }

    public DistUrlResolver<CategoryModel> getProductFamilyUrlResolver() {
        return productFamilyUrlResolver;
    }

    public void setCategoryUrlResolver(final DistUrlResolver<CategoryModel> productFamilyUrlResolver) {
        this.productFamilyUrlResolver = productFamilyUrlResolver;
    }

    public CommerceProductService getCommerceProductService() {
        return commerceProductService;
    }

    public void setCommerceProductService(final CommerceProductService commerceProductService) {
        this.commerceProductService = commerceProductService;
    }

    public Converter<CategoryModel, CategoryData> getCategoryConverter() {
        return categoryConverter;
    }

    public void setCategoryConverter(final Converter<CategoryModel, CategoryData> categoryConverter) {
        this.categoryConverter = categoryConverter;
    }

    public DistCategoryService getDistCategoryService() {
        return distCategoryService;
    }

    public void setDistCategoryService(final DistCategoryService distCategoryService) {
        this.distCategoryService = distCategoryService;
    }

    public DistReevooService getDistReevooService() {
        return distReevooService;
    }

    public void setDistReevooService(final DistReevooService distReevooService) {
        this.distReevooService = distReevooService;
    }

}
