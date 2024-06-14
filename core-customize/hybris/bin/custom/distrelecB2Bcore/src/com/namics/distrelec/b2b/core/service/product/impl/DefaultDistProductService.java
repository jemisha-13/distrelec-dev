/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.product.impl;

import static com.namics.distrelec.b2b.core.constants.DistConfigConstants.ErpSalesStatus.ATTRIBUTE_NO_PRODUCT_FOR_SALE_SALES_STATUS_MPA;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateIfSingleResult;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;
import static java.lang.String.format;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.namics.distrelec.b2b.core.constants.DistConfigConstants;
import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.inout.erp.AvailabilityService;
import com.namics.distrelec.b2b.core.model.DistPIMAlternateRankingModel;
import com.namics.distrelec.b2b.core.model.DistSalesOrgModel;
import com.namics.distrelec.b2b.core.model.DistSalesOrgProductModel;
import com.namics.distrelec.b2b.core.model.DistSalesStatusModel;
import com.namics.distrelec.b2b.core.model.marketing.DistHeroProductsModel;
import com.namics.distrelec.b2b.core.service.customer.strategies.CheckoutCustomerStrategy;
import com.namics.distrelec.b2b.core.service.i18n.NamicsCommonI18NService;
import com.namics.distrelec.b2b.core.service.product.DistProductReferenceService;
import com.namics.distrelec.b2b.core.service.product.DistProductService;
import com.namics.distrelec.b2b.core.service.product.dao.DistProductDao;
import com.namics.distrelec.b2b.core.service.product.data.PIMAlternateResult;
import com.namics.distrelec.b2b.core.service.product.data.PunchoutFilterResult;
import com.namics.distrelec.b2b.core.service.product.model.ProductAvailabilityExtModel;
import com.namics.distrelec.b2b.core.service.salesorg.DistSalesOrgService;
import com.namics.distrelec.b2b.core.service.site.DistrelecBaseStoreService;
import com.namics.distrelec.b2b.core.util.ErpStatusUtil;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.catalog.CatalogService;
import de.hybris.platform.catalog.enums.ProductReferenceTypeEnum;
import de.hybris.platform.catalog.model.ProductReferenceModel;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.commerceservices.stock.CommerceStockService;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.product.impl.DefaultProductService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.BaseStoreModel;

/**
 * Default implementation of {@link DistProductService}.
 *
 * @since Distrelec 1.0
 */
public class DefaultDistProductService extends DefaultProductService implements DistProductService {

    private static final Logger LOG = Logger.getLogger(DefaultDistProductService.class);

    //@formatter:off
    private static final String HAS_SIMILARS_QUERY = "SELECT COUNT({pk}) FROM {CategoryProductRelation AS cpr"
            + " JOIN " + DistSalesOrgProductModel._TYPECODE + " AS sop ON {cpr.target} = {sop." + DistSalesOrgProductModel.PRODUCT + "}"
            + " JOIN " + DistSalesStatusModel._TYPECODE + " AS ss ON {sop." + DistSalesOrgProductModel.SALESSTATUS + "}={ss.pk}}"
            + " WHERE {cpr.source} = ?source AND {cpr.target} != ?target AND {sop." + DistSalesOrgProductModel.SALESORG + "} = ?salesOrg"
            + " AND {ss." + DistSalesStatusModel.BUYABLEINSHOP + "}=1 AND {ss." + DistSalesStatusModel.VISIBLEINSHOP + "}=1"
            + " AND ({sop." + DistSalesOrgProductModel.ENDOFLIFEDATE + "} IS NULL OR {sop." + DistSalesOrgProductModel.ENDOFLIFEDATE + "} >= ?today)";

    private static final String SIMILAR_PRODUCTS_QUERY = "SELECT {cpr.target} FROM {CategoryProductRelation AS cpr"
            + " JOIN " + DistSalesOrgProductModel._TYPECODE + " AS sop ON {cpr.target} = {sop." + DistSalesOrgProductModel.PRODUCT + "}"
            + " JOIN " + DistSalesStatusModel._TYPECODE + " AS ss ON {sop." + DistSalesOrgProductModel.SALESSTATUS + "}={ss.pk}}"
            + " WHERE {cpr.source} = ?source AND {cpr.target} != ?target AND {sop." + DistSalesOrgProductModel.SALESORG + "} = ?salesOrg"
            + " AND {ss." + DistSalesStatusModel.BUYABLEINSHOP + "}=1 AND {ss." + DistSalesStatusModel.VISIBLEINSHOP + "}=1"
            + " AND ({sop." + DistSalesOrgProductModel.ENDOFLIFEDATE + "} IS NULL OR {sop." + DistSalesOrgProductModel.ENDOFLIFEDATE + "} >= ?today)";


    private static final String HERO_PRODUCTS_QUERY = "SELECT {pk} FROM {" + DistHeroProductsModel._TYPECODE + "} WHERE {salesOrg} =?salesOrg OR ({salesOrg} IS NULL AND {master}=1) ORDER BY {salesOrg}";

    private static final String ALTERNATE_PRODUCTS_RANKING_QUERY = "select {PK} from {DistPIMAlternateRanking as AlternateRaking} where {AlternateRaking.level}= ?level order by {AlternateRaking.rank}";

    private static final String DSOP_QUERY = "select {dsop.pk} from {DistSalesOrgProduct as dsop} where {dsop.product} = ";
    private static final String INITIAL_DSOP_QUERY = "select {p.pk} from {Product as p} where {p.firstAppearanceDate} is not null";

    private static final List<Class<Long>> R_CLASS_LIST = Arrays.asList(Long.class);

    private static final String ALWAYS_BUYABLE_CATEGORY_GROUPS_CONFIG_PREFIX = "itemCategoryGroup.buyableOutOfStock.";
    private static final String ALWAYS_BUYABLE_CATEGORY_GROUPS_DEFAULT_CONFIG = ALWAYS_BUYABLE_CATEGORY_GROUPS_CONFIG_PREFIX + "default";

    @Autowired
    private FlexibleSearchService flexibleSearchService;

    @Autowired
    private UserService userService;

    @Autowired
    private DistSalesOrgService distSalesOrgService;

    @Autowired
    private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService;

    @Autowired
    private DistrelecBaseStoreService baseStoreService;

    @Autowired
    private CatalogService catalogService;

    @Autowired
    private DistProductReferenceService productReferenceService;

    @Autowired
    private CheckoutCustomerStrategy checkoutCustomerStrategy;

    @Autowired
    private ModelService modelService;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    @Qualifier("erp.availabilityService")
    private AvailabilityService availabilityService;

    @Autowired
    private CMSSiteService cmsSiteService;

    @Autowired
    private ErpStatusUtil erpStatusUtil;

    @Autowired
    private NamicsCommonI18NService namicsCommonI18NService;

    @Autowired
    private CommerceStockService commerceStockService;

    @Override
    public boolean isEndOfLife(final ProductModel product) {
        return product.getEndOfLifeDate() != null && System.currentTimeMillis() > product.getEndOfLifeDate().getTime();
    }

    @Override
    public boolean isProductBuyable(final ProductModel product) {
        return isProductListedForCurrentSalesOrg(product) && !isProductPunchedOut(product);
    }

    @Override public boolean isProductBuyable( ProductModel product, boolean isPunchedOut) {
        return isProductListedForCurrentSalesOrg(product) && !isPunchedOut;
    }

    @Override
    public boolean isProductListedForCurrentSalesOrg(final ProductModel product) {
        return StringUtils.isNotBlank(product.getCodeErpRelevant())
                && getProductDao().productIsListedForSalesOrg(product, distSalesOrgService.getCurrentSalesOrg());
    }

    @Override
    public boolean isProductPunchedOut(final ProductModel product) {
        return CollectionUtils.isNotEmpty(getPunchOutFilters(product, null));
    }

    @Override
    public boolean isProductPunchedOutForSiteChannel(ProductModel product, SiteChannel channel) {
        return CollectionUtils.isNotEmpty(getPunchOutFilters(product, channel));
    }

    @Override
    public Collection<PunchoutFilterResult> getPunchOutFilters(ProductModel product) {
        return getPunchOutFilters(product, null);
    }

    @Override
    public List<PunchoutFilterResult> getPunchOutFilters(final ProductModel product, SiteChannel siteChannel) {
        B2BCustomerModel customer = null;
        if (!isAnonymousUser(userService.getCurrentUser()) || checkoutCustomerStrategy.isAnonymousCheckout()) {
            customer = (B2BCustomerModel) checkoutCustomerStrategy.getCurrentUserForCheckout();
        }

        final Date today = new Date();
        final DistSalesOrgModel salesOrg = distSalesOrgService.getCurrentSalesOrg();
        final B2BUnitModel unit = b2bUnitService.getParent(customer);
        final BaseStoreModel baseStore = baseStoreService.getCurrentBaseStore();

        if (siteChannel == null) {
            siteChannel = baseStore.getChannel();
        }

        if (customer != null && customer.getDefaultShipmentAddress() != null) {
            return getProductDao().findPunchOutFilters(salesOrg, unit, siteChannel, customer.getDefaultShipmentAddress().getCountry(), product, today);
        } else {
            CountryModel currentCountry = namicsCommonI18NService.getCurrentCountry();
            if (currentCountry != null) {
                return getProductDao().findPunchOutFilters(salesOrg, unit, siteChannel, currentCountry, product, today);
            } else {
                return getProductDao().findPunchOutFilters(salesOrg, unit, siteChannel, baseStore.getDeliveryCountries(), product, today);
            }
        }
    }

    private boolean isAnonymousUser(UserModel currentUser) {
        return userService.isAnonymousUser(currentUser);
    }

    @Override
    public Collection<PunchoutFilterResult> isProductPunchedOutForCart(final CartModel cart) {
        if (cart != null && CollectionUtils.isNotEmpty(cart.getEntries())) {
            final List<ProductModel> products = new ArrayList<>();
            final List<AbstractOrderEntryModel> orderEntries = cart.getEntries();
            for (final AbstractOrderEntryModel orderEntry : orderEntries) {
                products.add(orderEntry.getProduct());
            }
            final Date today = new Date();
            final DistSalesOrgModel salesOrg = distSalesOrgService.getCurrentSalesOrg();
            B2BUnitModel unit = null;
            if (cart.getUser() instanceof B2BCustomerModel) {
                unit = b2bUnitService.getParent((B2BCustomerModel) cart.getUser());
            }
            final BaseStoreModel baseStore = baseStoreService.getCurrentBaseStore();
            final SiteChannel siteChannel = baseStore.getChannel();

            if (cart.getDeliveryAddress() != null) {
                return getProductDao().findPunchOutFilters(salesOrg, unit, siteChannel, cart.getDeliveryAddress().getCountry(), products, today);
            } else {
                CountryModel currentCountry = namicsCommonI18NService.getCurrentCountry();
                if (currentCountry != null) {
                    return getProductDao().findPunchOutFilters(salesOrg, unit, siteChannel, currentCountry, products, today);
                } else {
                    return getProductDao().findPunchOutFilters(salesOrg, unit, siteChannel, baseStore.getDeliveryCountries(), products, today);
                }
            }

        }
        return Collections.emptyList();
    }

    @Override
    public ProductModel getProductForPimId(final String pimId) {
        validateParameterNotNull(pimId, "Parameter pimId must not be null");
        final List<ProductModel> products = getProductDao().getProductForPimId(pimId);
        validateIfSingleResult(products, format("Product with pimId '%s' not found!", pimId),
                format("Product pimId '%s' is not unique, %d products found!", pimId, Integer.valueOf(products.size())));
        return products.get(0);
    }

    @Override
    public ProductModel getCatalogPlusProductForCode(final UserModel user, final String catPlusSupplierAID) {
        validateParameterNotNull(user, "Parameter user must not be null");
        validateParameterNotNull(catPlusSupplierAID, "Parameter catPlusSupplierAID must not be null");

        // CatalogPlus items are not available for guests and B2C customers
        if (userService.isAnonymousUser(user) || ((B2BCustomerModel) user).getCustomerType() == CustomerType.B2C) {
            return null;
        }

        return getProductDao().findCatalogPlusProduct(catalogService.getCatalogForId("distrelecCatalogPlusProductCatalog").getActiveCatalogVersion(),
                catPlusSupplierAID);
    }

    @Override
    public List<ProductModel> getProductListForCodes(final List<String> productCodes) {
        if (CollectionUtils.isEmpty(productCodes)) {
            return Collections.emptyList();
        }
        return getProductDao().findProductsByCodes(productCodes);
    }

    @Override
    public List<ProductModel> getProductListForSapCodes(final List<String> productCodes) {
        return getProductDao().findProductsBySapCodes(productCodes);
    }

    @Override
    public ProductModel getProductForTypeOrCode(final String code) {
        return getProductDao().findProductByTypeOrCode(code);
    }

    @Override
    public List<ProductModel> getSimilarProducts(final ProductModel product) {
        return getSimilarProducts(product, 0, 4);
    }

    @Override
    public String getProductSalesStatus(final ProductModel product) {
        return getProductDao().getProductSalesStatus(product, distSalesOrgService.getCurrentSalesOrg());
    }

    @Override
    public DistSalesStatusModel getProductSalesStatusModel(final ProductModel product) {
        return getProductDao().getProductSalesStatusModel(product, distSalesOrgService.getCurrentSalesOrg());
    }

    @Override
    public DistSalesOrgProductModel getDistSalesOrgProductModel(final ProductModel product, final DistSalesOrgModel salesOrg) {
        return getProductDao().getDistSalesOrgProductModel(product, salesOrg);
    }

    @Override
    public DistSalesOrgProductModel getDistSalesOrgProductModel(final ProductModel product) {
        return getDistSalesOrgProductModel(product, distSalesOrgService.getCurrentSalesOrg());
    }

    @Override
    public DistSalesOrgProductModel getDistSalesOrgProductModel(final String productCode) {
        return getDistSalesOrgProductModel(getProductForCode(productCode));
    }

    @Override
    public Map<String, String> getSalesStatusForEntries(final List<AbstractOrderEntryModel> entries) {
        final List<String> productCodes = entries.stream().map(entry -> entry.getProduct().getCode()).collect(Collectors.toList());
        return getProductDao().getSalesStatusForProducts(productCodes, distSalesOrgService.getCurrentSalesOrg());
    }

    @Override
    public List<ProductModel> getSimilarProducts(final ProductModel product, final int offset, final int size) {

        if (size > 0) {
            final CategoryModel pimCategory = getSuperCategory(product.getSupercategories());
            if (pimCategory != null && pimCategory.getPimCategoryType() != null
                    && Boolean.TRUE.equals(pimCategory.getPimCategoryType().getCategoryWithSimilarProducts())) {
                final List<ProductModel> similarProducts = new ArrayList<>();

                final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(SIMILAR_PRODUCTS_QUERY);
                searchQuery.addQueryParameter("source", pimCategory);
                searchQuery.addQueryParameter("target", product);
                searchQuery.addQueryParameter("today", new Date());
                searchQuery.addQueryParameter("salesOrg", distSalesOrgService.getCurrentSalesOrg());

                final List<ProductModel> result = flexibleSearchService.<ProductModel>search(searchQuery).getResult();

                final int new_offset = Math.max(offset, 0);
                final int max_pos = new_offset + size;
                int pos = 0;
                for (final ProductModel pm : result) {
                    if (!isProductPunchedOut(pm) && (pos >= new_offset & pos++ < max_pos)) {
                        similarProducts.add(pm);
                        // As soon as we have the requested number, we stop processing the list.
                        if (similarProducts.size() >= size) {
                            break;
                        }
                    }
                }

                return similarProducts;
            }
        }

        return Collections.emptyList();
    }


    @Override
    public List<ProductModel> getProductsReferences(final List<ProductModel> sources, final List<ProductReferenceTypeEnum> referenceTypes, final int offset,
                                                    final int size) {
        final Collection<ProductReferenceModel> productReferences = productReferenceService.getProductReferencesForSourceProducts(sources, referenceTypes,
                true, offset, size);
        return productReferences.stream().map(ProductReferenceModel::getTarget).collect(Collectors.toList());
    }

    @Override
    public boolean hasDownloads(final String code) {
        return CollectionUtils.isNotEmpty(getProductForCode(code).getDownloadMedias());
    }

    @Override
    public boolean hasSimilarProducts(final String code) {
        final ProductModel source = getProductForCode(code);
        final CategoryModel pimCategory = getSuperCategory(source.getSupercategories());

        if (pimCategory != null && pimCategory.getPimCategoryType() != null
                && Boolean.TRUE.equals(pimCategory.getPimCategoryType().getCategoryWithSimilarProducts())) {

            final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(HAS_SIMILARS_QUERY);
            searchQuery.addQueryParameter("source", pimCategory);
            searchQuery.addQueryParameter("target", source);
            searchQuery.addQueryParameter("today", new Date());
            searchQuery.addQueryParameter("salesOrg", distSalesOrgService.getCurrentSalesOrg());
            searchQuery.setResultClassList(R_CLASS_LIST);
            return flexibleSearchService.<Long>search(searchQuery).getResult().get(0) > 0;
        }

        return false;
    }

    @Override
    public boolean hasAccessories(final String code) {
        return productReferenceService.hasAnyReferences(getProductForCode(code), Collections.singletonList(ProductReferenceTypeEnum.DIST_ACCESSORY), true);
    }

    @Override
    public List<ProductModel> getHeroProducts() {
        final FlexibleSearchQuery salesOrgQuery = new FlexibleSearchQuery(HERO_PRODUCTS_QUERY);
        salesOrgQuery.addQueryParameter("salesOrg", distSalesOrgService.getCurrentSalesOrg());
        try {
            final List<DistHeroProductsModel> heroProductsList = flexibleSearchService.<DistHeroProductsModel>search(salesOrgQuery).getResult();
            if (CollectionUtils.isNotEmpty(heroProductsList)) {
                final DistHeroProductsModel heroProducts = heroProductsList.get(0);
                return Arrays.asList(heroProducts.getPOne(), heroProducts.getPTwo(), heroProducts.getPThree(), heroProducts.getPFour(),
                        heroProducts.getPFive());
            }
        } catch (final Exception exp) {
            LOG.warn("An error occur while fetching the list of " + DistHeroProductsModel._TYPECODE, exp);
        }

        return Collections.emptyList();
    }

    @Override
    public void removeNonBuyableProducts(final Collection<ProductModel> products) {
        CollectionUtils.filter(products, product -> isProductListedForCurrentSalesOrg((ProductModel) product));
    }


    protected CategoryModel getSuperCategory(final Collection<CategoryModel> superCategories) {
        for (final CategoryModel superCategory : superCategories) {
            if (!(superCategory instanceof ClassificationClassModel)) {
                return superCategory;
            }
        }

        return null;
    }

    @Override
    public List<CMSSiteModel> getAvailableCMSSitesByProduct(final ProductModel product) {
        return getProductDao().getCMSSitesByProduct(product);
    }

    @Override
    protected DistProductDao getProductDao() {
        return (DistProductDao) super.getProductDao();
    }

    @Override
    public void updateDistSalesOrgProductNewLabel(final ProductModel product, final Date from, final Date until) {
        final SearchResult<DistSalesOrgProductModel> result = flexibleSearchService.search(DSOP_QUERY + product.getPk().toString());
        if (CollectionUtils.isNotEmpty(result.getResult())) {
            for (final DistSalesOrgProductModel dsop : result.getResult()) {
                dsop.setShowNewLabelFromDate(from);
                dsop.setShowNewLabelUntilDate(until);
                modelService.save(dsop);
            }
        }
    }

    @Override
    public void initialUpdateDistSalesOrgProductNewLabel() {
        final SearchResult<ProductModel> result = flexibleSearchService.search(INITIAL_DSOP_QUERY);
        if (CollectionUtils.isNotEmpty(result.getResult())) {
            final Calendar now = Calendar.getInstance();
            for (final ProductModel product : result.getResult()) {
                now.setTime(product.getFirstAppearanceDate());
                now.add(Calendar.MONTH, 6);
                final Date until = now.getTime();
                updateDistSalesOrgProductNewLabel(product, product.getFirstAppearanceDate(), until);
            }
        }
    }

    @Override
    public ProductModel findProductByCodeOrMPN(String productCodes, String mpn) {
        List<ProductModel> productCodeList = getProductDao().findProductsByCode(productCodes);
        if (CollectionUtils.isEmpty(productCodeList)) {
            final List<ProductModel> products = getProductDao().findProductByMPN(mpn);

            validateIfSingleResult(products, format("Product with code '%s' not found!", productCodes),
                    format("Product code '%s' is not unique, %d products found!", productCodes, products.size()));
            return products.get(0);
        }
        validateIfSingleResult(productCodeList, format("Product with code '%s' not found!", productCodes),
                format("Product code '%s' is not unique, %d products found!", productCodes, productCodeList.size()));
        return productCodeList.get(0);
    }

    @Override
    public boolean isDuplicateMPNProduct(String mpn) {
        final List<ProductModel> products = getProductDao().findProductByMPN(mpn);
        return products != null && products.size() > 1 ? Boolean.TRUE : Boolean.FALSE;
    }

    @Override
    public List<ProductModel> findProductByMPN(String mpn) {
        return getProductDao().findProductByMPN(mpn);
    }

    @Override
    public PIMAlternateResult getProductsReferencesForAlternative(List<ProductModel> sources, List<ProductReferenceTypeEnum> referenceTypes) {
        final Collection<ProductReferenceModel> productReferences = productReferenceService.getProductReferencesForSourceProducts(sources,
                referenceTypes,
                true);
        return internalGetProductsReferenceForAlternative(productReferences, true);
    }

    @Override
    public PIMAlternateResult getProductsReferencesForAlternative(final List<ProductModel> sources, final List<ProductReferenceTypeEnum> referenceTypes, final int offset,
                                                                  final int size) {
       return getProductsReferencesForAlternative(sources, referenceTypes, offset, size, true);
    }

    @Override
    public PIMAlternateResult getProductsReferencesForAlternative(final List<ProductModel> sources, final List<ProductReferenceTypeEnum> referenceTypes, final int offset,
                                                                  final int size, final boolean realStock) {
        final Collection<ProductReferenceModel> productReferences = productReferenceService.getProductReferencesForSourceProducts(sources, referenceTypes,
                true, offset, size);
        return internalGetProductsReferenceForAlternative(productReferences, realStock);
    }

    @Override
    public List<String> findExistingProductCodes(final List<String> codes) throws SQLException {
        return getProductDao().findExistingProductCodes(codes);
    }

    @Override
    public boolean isProductNotForSale(ProductModel product) {
        List<String> notForSaleStatusCodes = erpStatusUtil.getErpSalesStatusFromConfiguration(DistConfigConstants.ErpSalesStatus.ATTRIBUTE_NO_PRODUCT_FOR_SALE_GENERIC);
        if (CollectionUtils.isNotEmpty(notForSaleStatusCodes) && nonNull(product)) {
            return notForSaleStatusCodes.contains(getProductSalesStatus(product));
        }
        return Boolean.FALSE;
    }

    @Override
    public boolean isSuspendedProduct(ProductModel product) {
        List<String> notForSaleStatusCodes = erpStatusUtil.getErpSalesStatusFromConfiguration(DistConfigConstants.ErpSalesStatus.ATTRIBUTE_NO_PRODUCT_FOR_SALE_NOT_BUYABLE);
        if (CollectionUtils.isNotEmpty(notForSaleStatusCodes) && nonNull(product)) {
            return notForSaleStatusCodes.contains(getProductSalesStatus(product));
        }
        return Boolean.FALSE;
    }

    @Override
    public boolean isSAPCatalogProduct(ProductModel product) {
            return Arrays.asList(configurationService.getConfiguration().getString(DistConfigConstants.SAP_CATALOG_ARTICLES, StringUtils.EMPTY)
                    .split(DistConstants.Punctuation.COMMA))
                    .stream()
                    .anyMatch(code -> StringUtils.equals(code, product.getCode()));
    }

    private PIMAlternateResult internalGetProductsReferenceForAlternative(Collection<ProductReferenceModel> productReferences, boolean realStock) {
        PIMAlternateResult pimAlternateResult = new PIMAlternateResult();
        List<ProductModel> sortedList = new ArrayList<>();
        Map<String, String> codeCategoryMapping = new HashMap<>();
        final Set<String> alternateProductCodes = new HashSet<>();
        List<DistPIMAlternateRankingModel> level1Raking = getPIMAlternateRaking(1);
        List<String> productCodes = new ArrayList<>();
        for (ProductReferenceModel productReference : productReferences) {
            productCodes.add(productReference.getTarget().getCode());
        }

        Map<String, Integer> stock;
        if (realStock) {
            stock = getAvailability(productCodes);
        } else {
            stock = getStockLevels(productReferences);
        }

        for (DistPIMAlternateRankingModel ranking : level1Raking) {
            List<ProductModel> alternateCategoryProducts = new ArrayList<>();
            if (BooleanUtils.isFalse(ranking.getIsManufacturerCategory())) {
                String rankingCategory = ranking.getCategory();
                for (ProductReferenceModel productReference : productReferences) {
                    if (productReference.getReferenceType().getCode().equalsIgnoreCase(rankingCategory)) {
                        if (isSalesStatusValid(productReference.getTarget()) && isProductAvailableAfterOutOfStock(productReference.getTarget(), stock) && !alternateProductCodes.contains(productReference.getTarget().getCode())) {
                            alternateCategoryProducts.add(productReference.getTarget());
                            alternateProductCodes.add(productReference.getTarget().getCode());
                            codeCategoryMapping.put(productReference.getTarget().getCode(), rankingCategory);
                        }
                    }
                }
            } else {
                addManufacturerAlternatives(alternateCategoryProducts, productReferences, alternateProductCodes, ranking.getManufacturerName(), codeCategoryMapping, ranking.getCategory(), stock);
            }
            if (!alternateCategoryProducts.isEmpty() && alternateCategoryProducts.size() > 1) {
                alternateCategoryProducts = sortSecondLevelPIMAlternative(alternateCategoryProducts);
            }
            sortedList.addAll(alternateCategoryProducts);
        }
        pimAlternateResult.setAlternativeProducts(sortedList);
        pimAlternateResult.setCodeCategoryMapping(codeCategoryMapping);
        return pimAlternateResult;
    }

    private void addManufacturerAlternatives(List<ProductModel> alternateCategoryProducts, Collection<ProductReferenceModel> productReferences, Set<String> alternateProductCodes, String manufacturerName, Map<String, String> codeCategoryMapping, String manufacturerCategory, Map<String, Integer> stock) {
        for (ProductReferenceModel productReference : productReferences) {
            ProductModel alternateProduct = productReference.getTarget();
            if ((alternateProduct.getManufacturer() != null && alternateProduct.getManufacturer().getName() != null && alternateProduct.getManufacturer().getName().contains(manufacturerName)) && productReference.getTarget() != null && isSalesStatusValid(productReference.getTarget()) && isProductAvailableAfterOutOfStock(productReference.getTarget(), stock) && !alternateProductCodes.contains(productReference.getTarget().getCode())) {
                alternateCategoryProducts.add(productReference.getTarget());
                alternateProductCodes.add(productReference.getTarget().getCode());
                codeCategoryMapping.put(productReference.getTarget().getCode(), manufacturerCategory);
            }

        }
    }

    private List<ProductModel> sortSecondLevelPIMAlternative(List<ProductModel> alternateCategoryProducts) {
        List<DistPIMAlternateRankingModel> level2Ranking = getPIMAlternateRaking(2);
        if (CollectionUtils.isNotEmpty(level2Ranking)) {
            if (level2Ranking.get(0).getCategory().equalsIgnoreCase("creation_date")) {
                return sortProductListByCreationDate(alternateCategoryProducts);
            } else {
                return sortProductListByStock(alternateCategoryProducts);
            }
        } else {
            return sortProductListByStock(alternateCategoryProducts);
        }
    }

    private List<ProductModel> sortProductListByCreationDate(List<ProductModel> alternateCategoryProducts) {
        alternateCategoryProducts.sort(Comparator.comparing(ProductModel::getCreationtime).reversed());
        List<String> productCodes = new ArrayList<>();
        List<ProductModel> sortedList = new ArrayList<>();
        for (ProductModel alternateProduct : alternateCategoryProducts) {
            productCodes.add(alternateProduct.getCode());
        }
        List<ProductModel> zeroStockList = new ArrayList<>();
        List<ProductModel> nonZeroStockList = new ArrayList<>();
        Map<String, Integer> stock = getAvailability(productCodes);
        for (ProductModel alternate : alternateCategoryProducts) {
            Integer productStock = stock.get(alternate.getCode());
            if (productStock != null && productStock.longValue() > 0) {
                nonZeroStockList.add(alternate);
            } else {
                zeroStockList.add(alternate);
            }

        }
        sortedList.addAll(nonZeroStockList);
        sortedList.addAll(zeroStockList);
        return sortedList;
    }

    private List<ProductModel> sortProductListByStock(List<ProductModel> alternateCategoryProducts) {
        List<String> productCodes = new ArrayList<>();
        List<ProductModel> sortedList = new ArrayList<>();
        for (ProductModel alternateProduct : alternateCategoryProducts) {
            productCodes.add(alternateProduct.getCode());
        }
        List<ProductModel> zeroStockList = new ArrayList<>();
        List<ProductModel> nonZeroStockList = new ArrayList<>();
        Map<String, Integer> stock = getAvailability(productCodes);
        for (ProductModel alternate : alternateCategoryProducts) {
            Integer productStock = stock.get(alternate.getCode());
            if (productStock != null && productStock.longValue() > 0) {
                nonZeroStockList.add(alternate);
            } else {
                zeroStockList.add(alternate);
            }

        }
        sortedList.addAll(sortProductListByCreationDate(nonZeroStockList));
        sortedList.addAll(sortProductListByCreationDate(zeroStockList));
        return sortedList;
    }

    private Map<String, Integer> getAvailability(final List<String> productCodes) {
        final Map<String, Integer> availabilityMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(productCodes)) {
            final List<ProductAvailabilityExtModel> availabilities = availabilityService.getAvailability(productCodes, Boolean.FALSE);
            for (ProductAvailabilityExtModel stock : availabilities) {
                availabilityMap.put(stock.getProductCode(), stock.getStockLevelTotal());
            }
        }
        return availabilityMap;
    }

    /**
     * Returns the list of stock levels for referenced products.
     * @param productReferences the product references
     * @return the map of stock levels
     */
    private Map<String, Integer> getStockLevels(final Collection<ProductReferenceModel> productReferences) {
        Map<String, Integer> stock = new HashMap<>();
        BaseStoreModel baseStore = baseStoreService.getCurrentBaseStore();
        for (ProductReferenceModel productReference : productReferences) {
            ProductModel product = productReference.getTarget();
            String productCode = product.getCode();
            if (!stock.containsKey(productCode)) {
                Long stockLevel = commerceStockService.getStockLevelForProductAndBaseStore(product, baseStore);
                stock.put(productCode, stockLevel.intValue());
            }
        }
        return stock;
    }

    private List<DistPIMAlternateRankingModel> getPIMAlternateRaking(int level) {
        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(ALTERNATE_PRODUCTS_RANKING_QUERY);
        searchQuery.addQueryParameter("level", level);
        return flexibleSearchService.<DistPIMAlternateRankingModel>search(searchQuery).getResult();
    }

    private boolean isSalesStatusValid(final ProductModel product) {
        final List<String> erpSalesStatusFromConfiguration = erpStatusUtil.getErpSalesStatusFromConfiguration(ATTRIBUTE_NO_PRODUCT_FOR_SALE_SALES_STATUS_MPA);
        return !erpSalesStatusFromConfiguration.contains(getProductSalesStatus(product));
    }

    private boolean isProductInStock(Map<String, Integer> stockMap, String code) {
        Integer productStock = stockMap.get(code);
        if (productStock != null && productStock.longValue() > 0) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    private boolean isProductAvailableAfterOutOfStock(final ProductModel product, Map<String, Integer> stockMap) {
        final List<String> erpSalesStatusFromConfiguration = erpStatusUtil.getErpSalesStatusFromConfiguration(DistConfigConstants.ErpSalesStatus.ATTRIBUTE_IS_AVAILABLE_AFTER_STOCK_DEPLETION_IMPORTTOOL);
        if (erpSalesStatusFromConfiguration.contains(getProductSalesStatus(product))) {
            if (!isProductInStock(stockMap, product.getCode())) {
                return Boolean.FALSE;
            } else {
                return Boolean.TRUE;
            }
        }
        return Boolean.TRUE;
    }

    @Override
    public boolean isProductBuyableOutOfStock(ProductModel product) {
        DistSalesOrgProductModel salesOrgProduct = getDistSalesOrgProductModel(product);
        String productItemCategoryGroup = salesOrgProduct.getItemCategoryGroup();

        if (StringUtils.isBlank(productItemCategoryGroup)) {
            return false;
        }

        return getAlwaysBuyableItemCategoryGroups().contains(productItemCategoryGroup);
    }

    @Override
    public boolean isProductBANS(ProductModel product) {
        DistSalesOrgProductModel salesOrgProduct = getDistSalesOrgProductModel(product);
        String productItemCategoryGroup = salesOrgProduct.getItemCategoryGroup();

        if (StringUtils.isBlank(productItemCategoryGroup)) {
            return Boolean.FALSE;
        }

        return "BANS".contains(productItemCategoryGroup);
    }

    private List<String> getAlwaysBuyableItemCategoryGroups() {
        Configuration configuration = configurationService.getConfiguration();
        String currentSiteId = cmsSiteService.getCurrentSite().getUid();

        if (configuration.containsKey(ALWAYS_BUYABLE_CATEGORY_GROUPS_CONFIG_PREFIX + currentSiteId)) {
            return Arrays.asList(configuration.getString(ALWAYS_BUYABLE_CATEGORY_GROUPS_CONFIG_PREFIX + currentSiteId).split(DistConstants.Punctuation.COMMA));
        }

        if (configuration.containsKey(ALWAYS_BUYABLE_CATEGORY_GROUPS_DEFAULT_CONFIG)) {
            return Arrays.asList(configuration.getString(ALWAYS_BUYABLE_CATEGORY_GROUPS_DEFAULT_CONFIG).split(DistConstants.Punctuation.COMMA));
        }

        return Collections.emptyList();
    }

    @Override
    public List<ProductModel> getProductsBySiteIdAndSalesStatus(String site, String salesStatus, String itemCategoryGroup, int count, int page) {
        String queryText = "SELECT {p." + ProductModel.PK + "} FROM {" + ProductModel._TYPECODE + " AS p\n" +
                           " JOIN " + DistSalesOrgProductModel._TYPECODE + " AS sop ON {sop." + DistSalesOrgProductModel.PRODUCT + "}={p." + ProductModel.PK + "}\n" +
                           " JOIN " + DistSalesOrgModel._TYPECODE + " AS so ON {sop.salesOrg}={so." + DistSalesOrgModel.PK + "}\n" +
                           " JOIN " + DistSalesStatusModel._TYPECODE + " AS dss ON {sop.salesStatus}={dss." + DistSalesStatusModel.PK + "}\n" +
                           " JOIN " + CMSSiteModel._TYPECODE + " AS s ON {s.salesOrg}={so." + DistSalesOrgModel.PK + "}}\n" +
                           " \n" +
                           "WHERE {s." + CMSSiteModel.UID + "} = ?site\n" +
                           "AND {dss." + DistSalesStatusModel.CODE + "} = ?salesStatus\n" +
                           (isNotBlank(itemCategoryGroup) ? "AND {sop." + DistSalesOrgProductModel.ITEMCATEGORYGROUP + "} LIKE ?itemCategoryGroup\n" : "") +
                           "ORDER BY {p." + ProductModel.CODE + "}";

        FlexibleSearchQuery query = new FlexibleSearchQuery(queryText);
        query.addQueryParameter("site", site);
        query.addQueryParameter("salesStatus", salesStatus);
        if(isNotBlank(itemCategoryGroup)){
            query.addQueryParameter("itemCategoryGroup", itemCategoryGroup);
        }
        query.setStart(Math.max((page - 1) * count, 0));
        query.setCount(Math.max(1, Math.min(count, 500)));
        return flexibleSearchService.<ProductModel>search(query)
                                    .getResult();
    }
}
