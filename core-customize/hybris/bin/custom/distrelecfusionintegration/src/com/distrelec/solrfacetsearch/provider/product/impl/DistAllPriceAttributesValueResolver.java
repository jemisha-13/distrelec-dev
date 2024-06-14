package com.distrelec.solrfacetsearch.provider.product.impl;

import static com.distrelec.solrfacetsearch.indexer.impl.DistProductDocumentContextProvider.PRICEINFO_CONTEXT_FIELD;
import static com.distrelec.solrfacetsearch.indexer.impl.DistProductDocumentContextProvider.SALES_ORG_PRODUCT;
import static de.hybris.platform.europe1.jalo.GeneratedPriceRow.MINQTD;
import static java.util.stream.Collectors.groupingBy;
import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.collections4.MapUtils.isNotEmpty;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.distrelec.solrfacetsearch.dao.DistProductSearchExportDAO;
import com.google.gson.Gson;
import com.namics.distrelec.b2b.core.cms.daos.DistCMSSiteDao;
import com.namics.distrelec.b2b.core.jalo.DistPriceRow;
import com.namics.distrelec.b2b.core.model.DistSalesOrgProductModel;
import com.namics.distrelec.b2b.core.model.DistSalesUnitModel;

import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.enums.SolrPropertiesTypes;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.spi.InputDocument;

public class DistAllPriceAttributesValueResolver extends AbstractDistProductValueResolver {

    private static final Logger LOG = LogManager.getLogger(DistAllPriceAttributesValueResolver.class);

    public static final String SALES_STATUS_NEW_NOT_BUYABLE = "20";

    private static final String NET = "Net";

    private static final String GROSS = "Gross";

    private Gson gson;

    private DistProductSearchExportDAO distProductSearchExportDAO;

    public DistAllPriceAttributesValueResolver(EnumerationService enumerationService, DistCMSSiteDao distCMSSiteDao,
                                               DistProductSearchExportDAO distProductSearchExportDAO, Gson gson) {
        super(distCMSSiteDao, distProductSearchExportDAO, enumerationService);

        this.gson = gson;
        this.distProductSearchExportDAO = distProductSearchExportDAO;
    }

    @Override
    protected void addFieldValues(InputDocument document, IndexerBatchContext batchContext, IndexedProperty indexedProperty, ProductModel product,
                                  ValueResolverContext<Object, Object> valueResolverContext) throws FieldValueProviderException {
        long t1 = System.currentTimeMillis();

        CMSSiteModel cmsSite = batchContext.getFacetSearchConfig().getIndexConfig().getCmsSite();
        if (cmsSite == null) {
            return;
        }

        processCMSite(document, indexedProperty, product, valueResolverContext, cmsSite);

        LOG.debug("Fetching all prices for product[{}] took[{}]ms", product.getCode(), System.currentTimeMillis() - t1);
    }

    private void processCMSite(InputDocument document, IndexedProperty indexedProperty, ProductModel product,
                               ValueResolverContext<Object, Object> valueResolverContext, CMSSiteModel cmsSite) {
        List<PriceInformation> priceInfos = getDocumentContextAttribute(document, PRICEINFO_CONTEXT_FIELD);
        if (isEmpty(priceInfos)) {
            return;
        }

        try {
            createPriceFields(document, indexedProperty, product, valueResolverContext, cmsSite, priceInfos);
        } catch (FieldValueProviderException e) {
            LOG.error(String.format("Could not fetch price fields for product %s", product.getCode()), e);
            throw new RuntimeException(e);
        }
    }

    private void createPriceFields(InputDocument document, IndexedProperty indexedProperty, ProductModel product,
                                   ValueResolverContext<Object, Object> valueResolverContext, CMSSiteModel cmsSite,
                                   List<PriceInformation> priceInfos) throws FieldValueProviderException {
        DistSalesOrgProductModel salesOrgProduct = getDocumentContextAttribute(document, SALES_ORG_PRODUCT);
        if (salesOrgProduct == null) {
            LOG.warn("No salesOrgProduct found for product[{}] and cmsSite[{}], product should not be indexed", product.getCode(), cmsSite.getUid());
            return;
        }

        Map<Boolean, List<PriceInformation>> groupedPrices = priceInfos.stream()
                                                                       .collect(groupingBy(this::isSpecialPrice));
        List<PriceInformation> specialPrices = groupedPrices.get(Boolean.TRUE);
        List<PriceInformation> normalPrices = groupedPrices.get(Boolean.FALSE);

        List<PriceInformation> activePrices = isNotEmpty(specialPrices) ? specialPrices : normalPrices;

        if (isEmpty(activePrices)) {
            LOG.warn("No active prices found for product[{}] in cmsSite[{}]", product.getCode(), cmsSite.getUid());
            return;
        }
        LOG.debug("Number of prices found for product[{}] and in priceGroup {} is:{}", product.getCode(), cmsSite.getUserPriceGroup().getCode(),
                  activePrices.size());

        SortedMap<Long, Double> scaleNetPrices = convertPrices(activePrices, 1d);
        addPricesAsJson(document, indexedProperty, valueResolverContext, NET, scaleNetPrices);

        double taxFactor = distProductSearchExportDAO.getTaxFactor(product, cmsSite, salesOrgProduct);
        SortedMap<Long, Double> scaleGrossPrices = convertPrices(activePrices, taxFactor);
        addPricesAsJson(document, indexedProperty, valueResolverContext, GROSS, scaleGrossPrices);

        if (isNotEmpty(specialPrices)) {
            addOriginalFieldsForDiscountPrices(document, indexedProperty, valueResolverContext, normalPrices, scaleNetPrices, taxFactor);
        }

        Optional<Double> singleMinPriceNet = getSingleMinPrice(scaleNetPrices);
        Optional<Double> singleMinPriceGross = getSingleMinPrice(scaleGrossPrices);

        if (singleMinPriceNet.isPresent()) {
            addSinglePrice(document, indexedProperty, product, valueResolverContext, singleMinPriceNet, NET);
        }
        if (singleMinPriceGross.isPresent()) {
            addSinglePrice(document, indexedProperty, product, valueResolverContext, singleMinPriceGross, GROSS);
        }

        setAdditionalFields(document, indexedProperty, valueResolverContext, cmsSite);
    }

    private boolean isSpecialPrice(PriceInformation priceInformation) {
        return getPriceRow(priceInformation).isSpecialPrice();
    }

    private DistPriceRow getPriceRow(PriceInformation priceInformation) {
        return (DistPriceRow) priceInformation.getQualifierValue("pricerow");
    }

    private SortedMap<Long, Double> convertPrices(List<PriceInformation> priceInfos, double taxFactor) {
        SortedMap<Long, Double> scalePrices = new TreeMap<>();
        for (PriceInformation priceInfo : priceInfos) {
            long minQty = (long) priceInfo.getQualifierValue(MINQTD);
            // we only add the first price for each minQty, the first as it is returned from the distPriceService
            scalePrices.computeIfAbsent(minQty, k -> priceInfo.getValue().getValue() * taxFactor);
        }
        return scalePrices;
    }

    private void addPricesAsJson(InputDocument document, IndexedProperty indexedProperty, ValueResolverContext<Object, Object> valueResolverContext,
                                 String fieldSuffix, SortedMap<Long, Double> scalePrices) throws FieldValueProviderException {
        if (isNotEmpty(scalePrices)) {
            IndexedProperty propertyNetPrices = createNewIndexedProperty(indexedProperty, "scalePrices" + fieldSuffix, SolrPropertiesTypes.STRING.getCode());
            document.addField(propertyNetPrices, gson.toJson(scalePrices), valueResolverContext.getFieldQualifier());
        }
    }

    private void addOriginalFieldsForDiscountPrices(InputDocument document, IndexedProperty indexedProperty,
                                                    ValueResolverContext<Object, Object> valueResolverContext, List<PriceInformation> normalPrices,
                                                    SortedMap<Long, Double> scaleNetPrices, double taxFactor) throws FieldValueProviderException {
        if (isEmpty(normalPrices)) {
            return;
        }

        Optional<PriceInformation> standardPriceInfo = normalPrices.stream()
                                                                   .filter(priceInfo -> getPriceRow(priceInfo).getUnitFactor() == 1)
                                                                   .findFirst();
        if (standardPriceInfo.isEmpty()) {
            return;
        }

        Double discountNetPrice = scaleNetPrices.get(Long.valueOf(1));
        double standardNetPrice = standardPriceInfo.get().getPriceValue().getValue();

        if (discountNetPrice != null && discountNetPrice < standardNetPrice && standardNetPrice != 0) {
            double standardGrossPrice = standardNetPrice * taxFactor;
            int percentageDiscount = (int) Math.round(100 - (discountNetPrice / standardNetPrice * 100));

            document.addField(createNewIndexedProperty(indexedProperty, "standardPrice" + NET,
                                                       SolrPropertiesTypes.DOUBLE.getCode()),
                              standardNetPrice, valueResolverContext.getFieldQualifier());
            document.addField(createNewIndexedProperty(indexedProperty, "standardPrice" + GROSS,
                                                       SolrPropertiesTypes.DOUBLE.getCode()),
                              standardGrossPrice, valueResolverContext.getFieldQualifier());
            document.addField(createNewIndexedProperty(indexedProperty, "percentageDiscount",
                                                       SolrPropertiesTypes.INT.getCode()),
                              percentageDiscount, valueResolverContext.getFieldQualifier());
        }
    }

    private Optional<Double> getSingleMinPrice(SortedMap<Long, Double> scaleNetPrices) {
        if (isNotEmpty(scaleNetPrices)) {
            Double singleMinPrice = scaleNetPrices.get(scaleNetPrices.firstKey());
            return Optional.of(singleMinPrice);
        }
        return Optional.empty();
    }

    private void addSinglePrice(InputDocument document, IndexedProperty indexedProperty, ProductModel product,
                                ValueResolverContext<Object, Object> valueResolverContext, Optional<Double> singleMinPrice,
                                String priceSuffix) throws FieldValueProviderException {
        if (singleMinPrice.isEmpty()) {
            return;
        }
        IndexedProperty propertySingleMinPrice = createNewIndexedProperty(indexedProperty, "singleMinPrice" + priceSuffix);
        document.addField(propertySingleMinPrice, singleMinPrice.get(), valueResolverContext.getFieldQualifier());

        DistSalesUnitModel salesUnit = product.getSalesUnit();
        if (salesUnit != null && salesUnit.getAmount() != null && salesUnit.getAmount() != 0) {
            double singleUnitPrice = singleMinPrice.get() / salesUnit.getAmount();
            IndexedProperty propertySingleUnitPrice = createNewIndexedProperty(indexedProperty, "singleUnitPrice" + priceSuffix);
            document.addField(propertySingleUnitPrice, singleUnitPrice, valueResolverContext.getFieldQualifier());
        }
    }

    private void setAdditionalFields(InputDocument document, IndexedProperty indexedProperty, ValueResolverContext<Object, Object> valueResolverContext,
                                     CMSSiteModel cmsSite) throws FieldValueProviderException {
        DistSalesOrgProductModel salesOrgProduct = getDocumentContextAttribute(document, SALES_ORG_PRODUCT);

        String currencyIsoCode = cmsSite.getDefaultCurrency().getIsocode();
        Long itemMin = salesOrgProduct.getOrderQuantityMinimum();
        Long itemStep = salesOrgProduct.getOrderQuantityStep();
        boolean buyableInShop = salesOrgProduct.getSalesStatus().isBuyableInShop();
        String code = salesOrgProduct.getSalesStatus().getCode();
        boolean visibleForSearch = SALES_STATUS_NEW_NOT_BUYABLE.equals(code) || buyableInShop;

        document.addField(createNewIndexedProperty(indexedProperty, "currency", SolrPropertiesTypes.STRING.getCode()), currencyIsoCode,
                          valueResolverContext.getFieldQualifier());
        document.addField(createNewIndexedProperty(indexedProperty, "itemMin"), itemMin, valueResolverContext.getFieldQualifier());
        document.addField(createNewIndexedProperty(indexedProperty, "itemStep"), itemStep, valueResolverContext.getFieldQualifier());
        document.addField(createNewIndexedProperty(indexedProperty, "buyable", SolrPropertiesTypes.BOOLEAN.getCode()), buyableInShop,
                          valueResolverContext.getFieldQualifier());
        document.addField(createNewIndexedProperty(indexedProperty, "salesStatus", SolrPropertiesTypes.INT.getCode()), code,
                          valueResolverContext.getFieldQualifier());
        document.addField(createNewIndexedProperty(indexedProperty, "visibleForSearch", SolrPropertiesTypes.BOOLEAN.getCode()), visibleForSearch,
                          valueResolverContext.getFieldQualifier());
    }

}
